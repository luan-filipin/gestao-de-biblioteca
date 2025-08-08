package com.api.biblioteca.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.api.biblioteca.config.GlobalExceptionLivrosHandler;
import com.api.biblioteca.dto.AtualizarLivroDto;
import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.LivroDto;
import com.api.biblioteca.service.LivroService;
import com.api.biblioteca.service.RecomendaService;
import com.api.biblioteca.service.RecomendaServiceImp;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(LivroController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionLivrosHandler.class)
class LivroControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockitoBean
	private LivroService livroService;
	
	@MockitoBean
	private RecomendaService recomendaService;

	@DisplayName("POST - Deve criar um livro quando a requisição for valida.")
	@Test
	void deveCriarLivroSeRequisicaoForValida() throws Exception {

		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		CriarLivroDto dtoEntrada = new CriarLivroDto("Clean Code", "Robert C. Martin", "9780132350884", "Programação", dataFixa);
		LivroDto dtoEsperado = new LivroDto(1L, "Clean Code", "Robert C. Martin", "9780132350884", dataFixa,
				"Programação");

		when(livroService.criarLivro(dtoEntrada)).thenReturn(dtoEsperado);

		mockMvc.perform(post("/api/livros").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dtoEntrada))).andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.titulo").value("Clean Code"))
				.andExpect(jsonPath("$.autor").value("Robert C. Martin"))
				.andExpect(jsonPath("$.isbn").value("9780132350884"))
				.andExpect(jsonPath("$.categoria").value("Programação"));
	}

	@DisplayName("POST - Deve lançar exception se houver campo invalido.")
	@Test
	void deveLancarExceptionSeHouverCampoInvalido() throws Exception {
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		CriarLivroDto dtoEntrada = new CriarLivroDto("", "Robert C. Martin", "9780132350884", "Programação", dataFixa);

		mockMvc.perform(post("/api/livros").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dtoEntrada))).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").value("Campos inválidos")).andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.path").value("/api/livros")).andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.erros[0].campo").value("titulo"))
				.andExpect(jsonPath("$.erros[0].mensagem").value("O titulo é obrigatorio!"));
	}

	@DisplayName("GET - Deve buscar o livro pelo isbn com sucesso.")
	@Test
	void deveBuscarLivroPeloIsbnComSucesso() throws Exception {

		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		String isbn = "9780132350884";

		LivroDto dtoEsperado = new LivroDto(1L, "Clean Code", "Robert C. Martin", isbn, dataFixa, "Programação");

		when(livroService.buscaLivroPeloIsbn(isbn)).thenReturn(dtoEsperado);

		mockMvc.perform(get("/api/livros").param("isbn", isbn)).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(1)).andExpect(jsonPath("$.titulo").value("Clean Code"))
				.andExpect(jsonPath("$.autor").value("Robert C. Martin"))
				.andExpect(jsonPath("$.isbn").value("9780132350884"))
				.andExpect(jsonPath("$.categoria").value("Programação"));
	}

	@DisplayName("GET - Deve lançac exception se o isbn estiver ausente.")
	@Test
	void deveLancarExceptionSeIsbnEstiverAusente() throws Exception {
		String isbn = "";

		mockMvc.perform(get("/api/livros").param("isbn", isbn)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").value("Campos inválidos")).andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.path").value("/api/livros")).andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.erros[0].campo").value(org.hamcrest.Matchers.endsWith("isbn")))
				.andExpect(jsonPath("$.erros[0].mensagem").value("O isbn é obrigatorio!"));
	}

	@DisplayName("DELETE - Deve deletar um livro pelo isbn com sucesso.")
	@Test
	void deveDeletarUmLivroPeloIsbnComSucesso() throws Exception {
		String isbn = "9780132350884";

		doNothing().when(livroService).deletaLivroPeloIsbn(isbn);

		mockMvc.perform(delete("/api/livros").param("isbn", isbn)).andExpect(status().isNoContent());
	}

	@DisplayName("DELETE - Deve lançar exception se o isbn estiver ausente.")
	@Test
	void deveLancarExceptionSeOIsbnEstiverAusente() throws Exception {
		String isbn = "";

		mockMvc.perform(delete("/api/livros").param("isbn", isbn)).andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.mensagem").value("Campos inválidos")).andExpect(jsonPath("$.status").value(400))
				.andExpect(jsonPath("$.path").value("/api/livros")).andExpect(jsonPath("$.timestamp").exists())
				.andExpect(jsonPath("$.erros[0].campo").value(org.hamcrest.Matchers.endsWith("isbn")))
				.andExpect(jsonPath("$.erros[0].mensagem").value("O isbn é obrigatorio!"));
		
	}

	@DisplayName("PUT - Deve atualizar um livro pelo isbn com sucesso")
	@Test
	void deveAtualizarLivroPeloIsbnComSucesso() throws Exception {
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		String isbn = "9780132350884";
		
		AtualizarLivroDto dtoEntrada = new AtualizarLivroDto("Clean Code", "Robert C. Martin", "Programação", dataFixa);

		LivroDto dtoEsperado = new LivroDto(1L, "Clean Code", "Robert C. Martin", isbn, dataFixa, "Programação");
		
		when(livroService.atualizaLivroPeloIsBn(isbn, dtoEntrada)).thenReturn(dtoEsperado);
		
		mockMvc.perform(put("/api/livros")
				.param("isbn", isbn)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dtoEntrada)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.titulo").value("Clean Code"))
		.andExpect(jsonPath("$.autor").value("Robert C. Martin"))
		.andExpect(jsonPath("$.isbn").value("9780132350884"))
		.andExpect(jsonPath("$.categoria").value("Programação"));
	}
	
	@DisplayName("PUT - Deve lançar exception caso o isbn esteja ausente.")
	@Test
	void deveLancarExceptionCasoIsbnEstejaAusente()throws Exception{
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		String isbn = "";
		AtualizarLivroDto dtoEntrada = new AtualizarLivroDto("Clean Code", "Robert C. Martin", "Programação", dataFixa);
		
		mockMvc.perform(put("/api/livros")
				.param("isbn", isbn)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dtoEntrada)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.mensagem").value("Campos inválidos"))
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.path").value("/api/livros"))
		.andExpect(jsonPath("$.timestamp").exists())
		.andExpect(jsonPath("$.erros[0].campo").value(org.hamcrest.Matchers.endsWith("isbn")))
		.andExpect(jsonPath("$.erros[0].mensagem").value("O isbn é obrigatorio!"));
		
		
	} 
	
	@DisplayName("PUT - Deve lançar exception caso algum campo do Dto esteja ausente")
	@Test
	void deveLancarExceptionSeAlgumCampoDoDtoEstiverAusente() throws Exception{
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		String isbn = "9780132350884";
		AtualizarLivroDto dtoEntrada = new AtualizarLivroDto("", "Robert C. Martin", "Programação", dataFixa);

		mockMvc.perform(put("/api/livros")
				.param("isbn", isbn)
				.content(objectMapper.writeValueAsString(dtoEntrada))
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.mensagem").value("Campos inválidos"))
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.path").value("/api/livros"))
		.andExpect(jsonPath("$.timestamp").exists())
		.andExpect(jsonPath("$.erros[0].campo").value("titulo"))
		.andExpect(jsonPath("$.erros[0].mensagem").value("O campo titulo é obrigatorio!"));
	}
	
	@DisplayName("GET - Deve retornar as recomendações de livros com sucesso.")
	@Test
	void deveRetornarAsRecomendacoesDeLivrosComSucesso() throws Exception{
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		String email = "teste@teste.com";
		
		LivroDto dto1 = new LivroDto(1L, "Clean Code", "Robert C. Martin", "9780132350884", dataFixa, "Programação");
		LivroDto dto2 = new LivroDto(2L, "Desenvolvimento de APIs REST com Spring Boot", "João da Silva", "9788575227992", dataFixa, "Desenvolvimento Web");

		List<LivroDto> livrosDto = List.of(dto1, dto2);
		
		when(recomendaService.recomendarLivrosPorUsuario(email)).thenReturn(livrosDto);
		
		mockMvc.perform(get("/api/livros/recomendacao")
				.param("email", email)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(livrosDto)))
		.andExpect(status().isOk())
	    .andExpect(jsonPath("$", Matchers.hasSize(livrosDto.size())))
	    .andExpect(jsonPath("$[0].id").value(dto1.id()))
	    .andExpect(jsonPath("$[0].titulo").value(dto1.titulo()))
	    .andExpect(jsonPath("$[0].autor").value(dto1.autor()))
	    .andExpect(jsonPath("$[0].isbn").value(dto1.isbn()))
	    .andExpect(jsonPath("$[0].dataPublicacao").value(dto1.dataPublicacao().toString()))
	    .andExpect(jsonPath("$[0].categoria").value(dto1.categoria()))
	    .andExpect(jsonPath("$[1].id").value(dto2.id()))
	    .andExpect(jsonPath("$[1].titulo").value(dto2.titulo()))
	    .andExpect(jsonPath("$[1].autor").value(dto2.autor()))
	    .andExpect(jsonPath("$[1].isbn").value(dto2.isbn()))
	    .andExpect(jsonPath("$[1].dataPublicacao").value(dto2.dataPublicacao().toString()))
	    .andExpect(jsonPath("$[1].categoria").value(dto2.categoria()));
	}
	
	@DisplayName("GET - Deve lançar exception se o email estiver invalido.")
	@Test
	void deveLancarExceptionSeEmailEstiverInvalido() throws Exception{
		String email = "email-invalid";
		
		mockMvc.perform(get("/api/livros/recomendacao")
				.param("email", email))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.mensagem").value("Campos inválidos"))
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.path").value("/api/livros/recomendacao"))
		.andExpect(jsonPath("$.timestamp").exists())
		.andExpect(jsonPath("$.erros[0].campo").value(org.hamcrest.Matchers.endsWith("email")))
		.andExpect(jsonPath("$.erros[0].mensagem").value("Email invalido!"));
	}
	
	@DisplayName("GET - Deve lançar exception se nao houver email.")
	@Test
	void deveLancarExceptionSeNaoHouverEmail() throws Exception{
		String email = "";
		
		mockMvc.perform(get("/api/livros/recomendacao")
				.param("email", email))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.mensagem").value("Campos inválidos"))
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.path").value("/api/livros/recomendacao"))
		.andExpect(jsonPath("$.timestamp").exists())
		.andExpect(jsonPath("$.erros[0].campo").value(org.hamcrest.Matchers.endsWith("email")))
		.andExpect(jsonPath("$.erros[0].mensagem").value("O email é obrigatorio!"));
	}
}
