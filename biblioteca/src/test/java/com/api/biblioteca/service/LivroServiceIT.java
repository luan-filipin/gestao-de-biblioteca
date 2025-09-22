package com.api.biblioteca.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.api.biblioteca.dto.AtualizarLivroDto;
import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.LivroDto;
import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.exception.IsbnInexistenteException;
import com.api.biblioteca.exception.IsbnJaExisteException;
import com.api.biblioteca.repository.LivroRepository;

@SpringBootTest
@Testcontainers
@Transactional
class LivroServiceIT {

	@Autowired
	private LivroServiceImp livroServiceImp;
	
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
    
    // Metodos auxiliares para não repetir a criação dos objetos.
    private CriarLivroDto criarLivroDtoAux() {
        return new CriarLivroDto(
                "Clean Code",
                "Robert C. Martin",
                "12345",
                "Programação",
                LocalDate.of(2025, 8, 7)
        );
    }
    
	private AtualizarLivroDto LivroDtoAtualizaAux() {
		return new AtualizarLivroDto("Desenvolvimento de APIs REST com Spring Boot", "João da Silva", "Desenvolvimento Web", LocalDate.of(2025, 8, 7));
	}
    
    @DisplayName("Deve criar livro com sucesso")
	@Test
	void deveCriarEBuscarLivroComSucesso() {
    	
    	CriarLivroDto dtoEntrada = criarLivroDtoAux();
    	
    	// Valida DTO retornado
    	LivroDto criado = livroServiceImp.criarLivro(dtoEntrada);
    	
    	assertThat(criado).isNotNull();
    	assertThat(criado.isbn()).isEqualTo(dtoEntrada.isbn());
    	assertThat(criado.titulo()).isEqualTo(dtoEntrada.titulo());
    	assertThat(criado.autor()).isEqualTo(dtoEntrada.autor());
    	assertThat(criado.categoria()).isEqualTo(dtoEntrada.categoria());
    	
    	// Valida persistência no banco
    	Livro livroNoBanco = livroRepository.findByIsbn(dtoEntrada.isbn()).orElseThrow(IsbnJaExisteException::new);
    	
    	assertThat(livroNoBanco).isNotNull();
    	assertThat(livroNoBanco.getIsbn()).isEqualTo(dtoEntrada.isbn());
    	assertThat(livroNoBanco.getTitulo()).isEqualTo(dtoEntrada.titulo());
    	assertThat(livroNoBanco.getAutor()).isEqualTo(dtoEntrada.autor());
    	assertThat(livroNoBanco.getCategoria()).isEqualTo(dtoEntrada.categoria());
    }
    
    @DisplayName("Deve lançar exception se ISBN ja existir.")
	@Test
	void deveLancarExceptionSeIsbnJaExistir() {
    	
    	CriarLivroDto dtoEntrada = criarLivroDtoAux();
    	livroServiceImp.criarLivro(dtoEntrada);
    	
        assertThatThrownBy(() -> livroServiceImp.criarLivro(dtoEntrada))
        .isInstanceOf(IsbnJaExisteException.class)
        .hasMessage("O Livro ja existe");
    }
    
    @DisplayName("Deve buscar livro com sucesso.")
   	@Test
   	void deveBuscarLivroComSucesso() {
    	CriarLivroDto dtoEntrada = criarLivroDtoAux();
    	livroServiceImp.criarLivro(dtoEntrada);
    	
    	LivroDto livro = livroServiceImp.buscaLivroPeloIsbn(dtoEntrada.isbn());
    	
    	assertThat(livro).isNotNull();
    	assertThat(livro.isbn()).isEqualTo(dtoEntrada.isbn());
    	assertThat(livro.titulo()).isEqualTo(dtoEntrada.titulo());
    	assertThat(livro.autor()).isEqualTo(dtoEntrada.autor());
    	assertThat(livro.categoria()).isEqualTo(dtoEntrada.categoria());
    }
    
    @DisplayName("Deve lançar exception se livro nao existir")
   	@Test
   	void deveLancarExceptionSeLivroNaoExistir() {
    	
    	String isbnInexistente = "9999";
    	
        assertThatThrownBy(() -> livroServiceImp.buscaLivroPeloIsbn(isbnInexistente))
        .isInstanceOf(IsbnInexistenteException.class)
        .hasMessage("O livro nao existe");
    }
    
    @DisplayName("Deve deletar um livro com sucesso")
   	@Test
   	void deveDeletarLivroComSucesso() {
    	CriarLivroDto dtoEntrada = criarLivroDtoAux();
    	livroServiceImp.criarLivro(dtoEntrada);
    	
    	livroServiceImp.deletaLivroPeloIsbn(dtoEntrada.isbn());
    	
    	boolean exists = livroRepository.existsByIsbn(dtoEntrada.isbn());
    	
    	assertThat(exists).isFalse();
    }
    
    @DisplayName("Deve lançar exception se livro nao existir.")
   	@Test
    void deveLancarExceptionSeLivroNaoExistirParaDeletar() {
    	
    	String isbnInexistente = "9999";
    	
        assertThatThrownBy(() -> livroServiceImp.deletaLivroPeloIsbn(isbnInexistente))
        .isInstanceOf(IsbnInexistenteException.class)
        .hasMessage("O livro nao existe");
    }
    
    @DisplayName("Deve atualizar um livro com sucesso")
   	@Test
   	void deveAtualizarLivroComSucesso() {
    	
    	String isbn = "12345";
    	CriarLivroDto dtoEntrada = criarLivroDtoAux();
    	livroServiceImp.criarLivro(dtoEntrada);
    	
    	AtualizarLivroDto livroAtualizado = LivroDtoAtualizaAux();
    	
    	// Valida DTO retornado
    	LivroDto livroNovo =  livroServiceImp.atualizaLivroPeloIsBn(isbn, livroAtualizado);
    	
    	assertThat(livroNovo).isNotNull();
    	assertThat(livroNovo.isbn()).isEqualTo(isbn);
    	assertThat(livroNovo.autor()).isEqualTo(livroAtualizado.autor());
    	assertThat(livroNovo.titulo()).isEqualTo(livroAtualizado.titulo());
    	assertThat(livroNovo.categoria()).isEqualTo(livroAtualizado.categoria());
    	
    	// Valida persistência no banco
    	Livro livroNoBanco = livroRepository.findByIsbn(dtoEntrada.isbn()).orElseThrow(IsbnInexistenteException::new);
    	
    	assertThat(livroNoBanco.getTitulo()).isEqualTo(livroAtualizado.titulo());
    	assertThat(livroNoBanco.getAutor()).isEqualTo(livroAtualizado.autor());
    	assertThat(livroNoBanco.getCategoria()).isEqualTo(livroAtualizado.categoria());
    }
    
    @DisplayName("Deve lançar exception se ISBN nao existir")
   	@Test
   	void deveLancarExceptionSeIsbnNaoExistirParaAtualizar() {
    	
    	String isbnInexistente = "9999";
    	
    	AtualizarLivroDto livroAtualizado = LivroDtoAtualizaAux();
    	
        assertThatThrownBy(() -> livroServiceImp.atualizaLivroPeloIsBn(isbnInexistente, livroAtualizado))
        .isInstanceOf(IsbnInexistenteException.class)
        .hasMessage("O livro nao existe");
    }
}
