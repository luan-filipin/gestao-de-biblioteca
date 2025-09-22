package com.api.biblioteca.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.exception.IsbnInexistenteException;
import com.api.biblioteca.repository.LivroRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@Transactional
class LivroControllerIT {
	
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LivroRepository livroRepository;
	
  //Configuração do Testcontainers
    @SuppressWarnings("resource")//Apenas para nao mostrar o warning.
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("livro_teste")
            .withUsername("admin")
            .withPassword("admin");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
  
    private String criarLivroJson(String isbn) {
        return """
                {
                  "titulo": "Clean Code",
                  "autor": "Robert C. Martin",
                  "isbn": "%s",
                  "categoria": "Programação",
                  "dataPublicacao": "2025-08-07"
                }
                """.formatted(isbn);
    }
    
    @DisplayName("Deve criar um livro com sucesso e retornar 201")
	@Test
	void DeveCriarLivroComSucesso() throws Exception{
    	
    	String json = criarLivroJson("12345");
    	
    	mockMvc.perform(post("/api/livros")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json))
    	.andExpect(status().isCreated())
    	.andExpect(jsonPath("$.isbn").value("12345"))
    	.andExpect(jsonPath("$.titulo").value("Clean Code"))
    	.andExpect(jsonPath("$.autor").value("Robert C. Martin"))
    	.andExpect(jsonPath("$.categoria").value("Programação"));
    	
    	assertThat(livroRepository.existsByIsbn("12345")).isTrue();
    }
    
    @DisplayName("Deve lançar exception se os campos estiverem nulos")
	@Test
	void deveLancarExceptionSeCampoEstiverNulo() throws Exception{
        
    	String jsonInvalido = """
                {
                  "titulo": "",
                  "autor": "Autor Teste",
                  "isbn": "12345",
                  "categoria": "Programação",
                  "dataPublicacao": "2025-08-07"
                }
                """;
    	
    	mockMvc.perform(post("/api/livros")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(jsonInvalido))
    	.andExpect(status().isBadRequest())
    	.andExpect(jsonPath("$.erros[0].campo").value("titulo"))
    	.andExpect(jsonPath("$.erros[0].mensagem").value("O titulo é obrigatorio!"));
    }
    
    @DisplayName("Deve buscar livro pelo isbn e retornar status code 200")
	@Test
	void deveBuscarLivroPeloIsbnComSucesso() throws Exception{
    	
    	String json = criarLivroJson("12345");
    	
    	mockMvc.perform(post("/api/livros")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json))
    	.andExpect(status().isCreated())
    	.andExpect(jsonPath("$.isbn").value("12345"))
    	.andExpect(jsonPath("$.titulo").value("Clean Code"))
    	.andExpect(jsonPath("$.autor").value("Robert C. Martin"))
    	.andExpect(jsonPath("$.categoria").value("Programação"));
    	
    	assertThat(livroRepository.existsByIsbn("12345")).isTrue();
    	
    	String isbn = "12345";
    	
    	mockMvc.perform(get("/api/livros")
    			.param("isbn", isbn))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.isbn").value("12345"))
    	.andExpect(jsonPath("$.titulo").value("Clean Code"))
    	.andExpect(jsonPath("$.autor").value("Robert C. Martin"))
    	.andExpect(jsonPath("$.categoria").value("Programação"));
    }
    
    @DisplayName("Deve lançar exception se o campo isbn estiver ausente")
	@Test
	void deveLancarExceptionSeCampoIsbnAusente()throws Exception{
    	
    	String isbn = "";
    	
    	mockMvc.perform(get("/api/livros")
    			.param("isbn", isbn))
    	.andExpect(status().isBadRequest())
    	.andExpect(jsonPath("$.erros[0].campo").value(org.hamcrest.Matchers.endsWith("isbn")))
    	.andExpect(jsonPath("$.erros[0].mensagem").value("O isbn é obrigatorio!"));
    }
    
    @DisplayName("Deve deletar livro e retornar status code 204")
   	@Test
   	void deveDeletarLivroComSucesso() throws Exception{
    	
    	String json = criarLivroJson("12345");
    	
    	mockMvc.perform(post("/api/livros")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json))
    	.andExpect(status().isCreated())
    	.andExpect(jsonPath("$.isbn").value("12345"))
    	.andExpect(jsonPath("$.titulo").value("Clean Code"))
    	.andExpect(jsonPath("$.autor").value("Robert C. Martin"))
    	.andExpect(jsonPath("$.categoria").value("Programação"));
    	
    	assertThat(livroRepository.existsByIsbn("12345")).isTrue();
    	
    	String isbn = "12345";
    	
    	mockMvc.perform(delete("/api/livros")
    			.param("isbn", isbn))
    	.andExpect(status().isNoContent());
    	
    	assertThat(livroRepository.existsByIsbn("12345")).isFalse();
    }
    
    @DisplayName("Deve lançar exception se campo isbn estiver ausente")
   	@Test
   	void deveLancarExceptionSeCampoIsbnAusenteParaDeletar()throws Exception{
    	
    	String isbn = "";
    	
    	mockMvc.perform(delete("/api/livros")
    			.param("isbn", isbn))
    	.andExpect(status().isBadRequest());
    }
    
    @DisplayName("Deve atualiza um livro e retornar o status code 200")
   	@Test
   	void devAtualizaLivroComSucesso() throws Exception{
    	
    	String json = criarLivroJson("12345");
    	
    	mockMvc.perform(post("/api/livros")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json))
    	.andExpect(status().isCreated())
    	.andExpect(jsonPath("$.isbn").value("12345"))
    	.andExpect(jsonPath("$.titulo").value("Clean Code"))
    	.andExpect(jsonPath("$.autor").value("Robert C. Martin"))
    	.andExpect(jsonPath("$.categoria").value("Programação"));
    	
    	assertThat(livroRepository.existsByIsbn("12345")).isTrue();
    	
    	String jsonEntrada = """
                {
                  "titulo": "Desenvolvimento de APIs REST com Spring Boot",
                  "autor": "João da Silva",
                  "categoria": "Desenvolvimento Web",
                  "dataPublicacao": "2025-08-07"
                }
                """;
    	
    	String isbn = "12345";
    	
    	mockMvc.perform(put("/api/livros")
    			.param("isbn", isbn)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(jsonEntrada))
    	.andExpect(status().isOk())
    	.andExpect(jsonPath("$.titulo").value("Desenvolvimento de APIs REST com Spring Boot"))
    	.andExpect(jsonPath("$.autor").value("João da Silva"))
    	.andExpect(jsonPath("$.categoria").value("Desenvolvimento Web"))
    	.andExpect(jsonPath("$.dataPublicacao").exists());
    	
    	Livro livroNoBanco = livroRepository.findByIsbn("12345").orElseThrow(IsbnInexistenteException::new);
    	assertThat(livroNoBanco).isNotNull();
    	assertThat(livroNoBanco.getTitulo()).isEqualTo("Desenvolvimento de APIs REST com Spring Boot");
    	assertThat(livroNoBanco.getAutor()).isEqualTo("João da Silva");
    	assertThat(livroNoBanco.getCategoria()).isEqualTo("Desenvolvimento Web");
    	assertThat(livroNoBanco.getDataPublicacao()).isEqualTo(LocalDate.of(2025, 8, 7));
    }
    
    @DisplayName("Deve lançar exception se campo isbn estiver ausente")
   	@Test
   	void deveLançarExceptionSeCampoIsbnEstiverAusenteParaAtualizar()throws Exception{
    	
    	String json = criarLivroJson("12345");
    	
    	String jsonEntrada = """
                {
                  "titulo": "Desenvolvimento de APIs REST com Spring Boot",
                  "autor": "João da Silva",
                  "categoria": "Desenvolvimento Web",
                  "dataPublicacao": "2025-08-07"
                }
                """;
    	
    	mockMvc.perform(post("/api/livros")
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(json))
    	.andExpect(status().isCreated())
    	.andExpect(jsonPath("$.isbn").value("12345"))
    	.andExpect(jsonPath("$.titulo").value("Clean Code"))
    	.andExpect(jsonPath("$.autor").value("Robert C. Martin"))
    	.andExpect(jsonPath("$.categoria").value("Programação"));
    	
    	assertThat(livroRepository.existsByIsbn("12345")).isTrue();
    	
    	String isbn = "";
    	
    	mockMvc.perform(put("/api/livros")
    			.param("isbn", isbn)
    			.contentType(MediaType.APPLICATION_JSON)
    			.content(jsonEntrada))
    	.andExpect(status().isBadRequest())
    	.andExpect(jsonPath("$.erros[0].campo").value(org.hamcrest.Matchers.endsWith("isbn")))
    	.andExpect(jsonPath("$.erros[0].mensagem").value("O isbn é obrigatorio!"));
    	
    }

}
