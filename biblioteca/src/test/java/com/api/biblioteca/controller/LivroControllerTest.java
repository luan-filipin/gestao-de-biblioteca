package com.api.biblioteca.controller;

import static org.mockito.Mockito.when;

import java.time.LocalDate;

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
import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.LivroDto;
import com.api.biblioteca.service.LivroService;
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
	
	@DisplayName("POST - Deve criar um livro quando a requisição for valida.")
	@Test
	void deveCriarLivroSeRequisicaoForValida() throws Exception{
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);

		CriarLivroDto dtoEntrada = new CriarLivroDto("Clean Code", "Robert C. Martin", "9780132350884", "Programação");
		
		LivroDto dtoEsperado = new LivroDto(1L, "Clean Code", "Robert C. Martin", "9780132350884", dataFixa, "Programação");

		
		when(livroService.criarLivro(dtoEntrada)).thenReturn(dtoEsperado);
		
		mockMvc.perform(post("/api/livros")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dtoEntrada)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.titulo").value("Clean Code"))
		.andExpect(jsonPath("$.autor").value("Robert C. Martin"))
		.andExpect(jsonPath("$.isbn").value("9780132350884"))
		.andExpect(jsonPath("$.categoria").value("Programação"));
	}
	
	@DisplayName("POST - Deve lançar exception se houver campo invalido.")
	@Test
	void deveLancarExceptionSeHouverCampoInvalido()throws Exception{
		
		CriarLivroDto dtoEntrada = new CriarLivroDto("", "Robert C. Martin", "9780132350884", "Programação");

		mockMvc.perform(post("/api/livros")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dtoEntrada)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.mensagem").value("Campos inválidos"))
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.path").value("/api/livros"))
		.andExpect(jsonPath("$.timestamp").exists())
		.andExpect(jsonPath("$.erros[0].campo").value("titulo"))
		.andExpect(jsonPath("$.erros[0].mensagem").value("O titulo é obrigatorio!"));
	}
	
	@DisplayName("GET - Deve buscar o livro pelo isbn com sucesso.")
	@Test
	void deveBuscarLivroPeloIsbnComSucesso() throws Exception {
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		String isbn = "9780132350884";

		LivroDto dtoEsperado = new LivroDto(
			1L, "Clean Code", "Robert C. Martin", isbn,
			dataFixa, "Programação"
		);

		when(livroService.buscaLivroPeloIsbn(isbn)).thenReturn(dtoEsperado);
		
		mockMvc.perform(get("/api/livros")
				.param("isbn", isbn))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1))
			.andExpect(jsonPath("$.titulo").value("Clean Code"))
			.andExpect(jsonPath("$.autor").value("Robert C. Martin"))
			.andExpect(jsonPath("$.isbn").value("9780132350884"))
			.andExpect(jsonPath("$.categoria").value("Programação"));
	}
	
	@DisplayName("GET - Deve lançac exception se o isbn estiver ausente.")
	@Test
	void deveLancarExceptionSeIsbnEstiverAusente()throws Exception{
		String isbn = "";
		
		mockMvc.perform(get("/api/livros")
				.param("isbn", isbn))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.mensagem").value("Campos inválidos"))
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.path").value("/api/livros"))
		.andExpect(jsonPath("$.timestamp").exists())
		.andExpect(jsonPath("$.erros[0].campo").value("buscaLivroPeloIsbn.isbn"))
		.andExpect(jsonPath("$.erros[0].mensagem").value("O isbn é obrigatorio!"));
	}
	
}
