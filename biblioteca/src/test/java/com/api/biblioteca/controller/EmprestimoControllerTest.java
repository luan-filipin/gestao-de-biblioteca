package com.api.biblioteca.controller;

import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


import com.api.biblioteca.config.GlobalExceptionEmprestimoHandler;
import com.api.biblioteca.dto.AtualizarEmprestimoDto;
import com.api.biblioteca.dto.CriarEmprestimoDto;
import com.api.biblioteca.dto.response.AtualizarEmprestimoRespostaDto;
import com.api.biblioteca.dto.response.EmprestimoDto;
import com.api.biblioteca.service.EmprestimoService;
import com.fasterxml.jackson.databind.ObjectMapper;


@WebMvcTest(EmprestimoController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionEmprestimoHandler.class)
class EmprestimoControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private EmprestimoService emprestimoService;
	
	private AtualizarEmprestimoDto criarEmprestomoDtoAtualizadoAux(Boolean status) {
		return new AtualizarEmprestimoDto(LocalDate.of(2025, 8, 7), status);
	}
	
	@DisplayName("POST - Deve criar um emprestimo com sucesso.")
	@Test
	void deveCriarEmprestimoComSucesso()throws Exception{
		
		LocalDateTime dataEmprestimoFixa = LocalDateTime.of(2025, 8, 7, 10, 0);
		
		CriarEmprestimoDto emprestimoEntrada = new CriarEmprestimoDto(1L, 1L, LocalDate.now());
		
		EmprestimoDto emprestimo = new EmprestimoDto(1L, "Luan Brito", "Clean Code", dataEmprestimoFixa, LocalDate.now(), true);

		when(emprestimoService.criarEmprestimo(emprestimoEntrada)).thenReturn(emprestimo);
		
		mockMvc.perform(post("/api/emprestimos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(emprestimoEntrada)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.nomeUsuario").value("Luan Brito"))
		.andExpect(jsonPath("$.tituloLivro").value("Clean Code"))
		.andExpect(jsonPath("$.dataEmprestimo").exists())
		.andExpect(jsonPath("$.dataDevolucao").exists())
		.andExpect(jsonPath("$.status").value(true));
	}
	
	@DisplayName("POST - Deve lançar exception se houver campos invalidos.")
	@Test
	void deveLancarExceptionSeHouverCamposInvalidos() throws Exception{
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 8);

		CriarEmprestimoDto emprestimoEntrada = new CriarEmprestimoDto(null, 1L, dataFixa);
		
		mockMvc.perform(post("/api/emprestimos")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(emprestimoEntrada)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.path").value("/api/emprestimos"))
		.andExpect(jsonPath("$.timestamp").exists())
		.andExpect(jsonPath("$.erros[0].campo").value("livroId"))
		.andExpect(jsonPath("$.erros[0].mensagem").value("O ID do livro é obrigatório."));

	}
	
	@DisplayName("PUT - Deve atualizar o emprestimo pelo id com sucesso.")
	@Test
	void deveAtualizarEmprestimoComSucesso()throws Exception{
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		LocalDateTime dataEmprestimoFixa = LocalDateTime.of(2025, 8, 7, 10, 0);
		Long id = 1L;
		
		AtualizarEmprestimoDto emprestimoEntrada = criarEmprestomoDtoAtualizadoAux(true);
		AtualizarEmprestimoRespostaDto emprestimoDtoEsperado = new AtualizarEmprestimoRespostaDto(1L, "Luan Brito", "Clean Code", dataEmprestimoFixa, dataFixa, true);


		when(emprestimoService.atualizarEmprestimoPeloId(id, emprestimoEntrada)).thenReturn(emprestimoDtoEsperado);
		
		mockMvc.perform(put("/api/emprestimos")
				.param("id", String.valueOf(id))
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(emprestimoEntrada)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.nomeUsuario").value("Luan Brito"))
		.andExpect(jsonPath("$.tituloLivro").value("Clean Code"))
		.andExpect(jsonPath("$.dataEmprestimo").exists())
		.andExpect(jsonPath("$.dataDevolucao").exists())
		.andExpect(jsonPath("$.status").value(true));
	}
	
	@DisplayName("PUT - Deve lançar exception se houver campos invalidos")
	@Test
	void deveLancarExceptionSeTiverCamposInvalidos() throws Exception {

	    Long id = 1L;

	    AtualizarEmprestimoDto emprestimoEntrada = criarEmprestomoDtoAtualizadoAux(null);

	    mockMvc.perform(put("/api/emprestimos")
	            .param("id", String.valueOf(id))
	            .contentType(MediaType.APPLICATION_JSON)
	            .content(objectMapper.writeValueAsString(emprestimoEntrada)))
	        .andExpect(status().isBadRequest())
	        .andExpect(jsonPath("$.status").value(400))
	        .andExpect(jsonPath("$.path").value("/api/emprestimos"))
	        .andExpect(jsonPath("$.timestamp").exists())
	        .andExpect(jsonPath("$.erros[0].campo").value(org.hamcrest.Matchers.endsWith("status")))
	        .andExpect(jsonPath("$.erros[0].mensagem").value("O status do empréstimo é obrigatório."));
	}
}
