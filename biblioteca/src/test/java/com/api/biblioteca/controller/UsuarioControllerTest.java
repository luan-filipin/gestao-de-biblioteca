package com.api.biblioteca.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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

import com.api.biblioteca.config.GlobalExceptionUsuarioHandler;
import com.api.biblioteca.dto.CriarUsuarioDto;
import com.api.biblioteca.dto.response.AtualizaUsuarioDto;
import com.api.biblioteca.dto.response.UsuarioDto;
import com.api.biblioteca.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;



@WebMvcTest(UsuarioController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionUsuarioHandler.class)
class UsuarioControllerTest {
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
	private ObjectMapper objectMapper;
	
	@MockitoBean
	private UsuarioService usuarioService;
	
	
	@DisplayName("POST - Deve criar o usuario quando a requisição for valida.")
	@Test
	void deveCriarUsuarioQuandoARequisicaoForValida()throws Exception{
		
		LocalDateTime dataFixa = LocalDateTime.of(2025, 8, 6, 12, 0);

		CriarUsuarioDto dtoEntrada = new CriarUsuarioDto("Luan Brito", "teste@teste.com", "(11) 91234-5678");
		
		UsuarioDto entitySalvo = new UsuarioDto(1L,"Luan Brito", "teste@teste.com", dataFixa, "(11) 91234-5678");
		
		when(usuarioService.criarUsuario(dtoEntrada)).thenReturn(entitySalvo);
		
		mockMvc.perform(post("/api/usuarios")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dtoEntrada)))
		.andExpect(status().isCreated())
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.nome").value("Luan Brito"))
		.andExpect(jsonPath("$.email").value("teste@teste.com"))
		.andExpect(jsonPath("$.telefone").value("(11) 91234-5678"));
	}
	
	@DisplayName("POST - Deve lançar exception se a validação for invalida")
	@Test
	void deveLancarExceptionSeAValidacaoForInvalida() throws Exception{
		
		CriarUsuarioDto dtoEntrada = new CriarUsuarioDto("", "teste@teste.com", "(11) 91234-5678");
		
		mockMvc.perform(post("/api/usuarios")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(dtoEntrada)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.mensagem").value("Campos inválidos"))
		.andExpect(jsonPath("$.path").value("/api/usuarios"))
		.andExpect(jsonPath("$.timestamp").exists())
		.andExpect(jsonPath("$.erros[0].campo").value("nome"))
		.andExpect(jsonPath("$.erros[0].mensagem").value("O nome é obrigatorio!"));
	}

	@DisplayName("GET - Deve buscar o usuario pelo email com sucesso.")
	@Test
	void deveBuscarUsuarioPeloEmailComSucesso() throws Exception{
		
		LocalDateTime dataFixa = LocalDateTime.of(2025, 8, 6, 12, 0);

		String email = "teste@teste.com";
		
		UsuarioDto entitySalvo = new UsuarioDto(1L,"Luan Brito", "teste@teste.com", dataFixa, "(11) 91234-5678");
		
		when(usuarioService.buscarUsuarioPorEmail(email)).thenReturn(entitySalvo);

		mockMvc.perform(get("/api/usuarios")
				.param("email", email)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isOk())
		.andExpect(content().contentType(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.id").value(entitySalvo.id()))
		.andExpect(jsonPath("$.nome").value(entitySalvo.nome()))
		.andExpect(jsonPath("$.email").value(entitySalvo.email()))
		.andExpect(jsonPath("$.telefone").value(entitySalvo.telefone()));
		
	}
	
	@DisplayName("GET - Deve lançar exception se o email for invalido")
	@Test
	void deveLancarExceptionSeEmailForInvalido() throws Exception {
		
		String email = "email-invalido";
		
		mockMvc.perform(get("/api/usuarios")
				.param("email", email)
				.contentType(MediaType.APPLICATION_JSON))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.status").value(400))
			.andExpect(jsonPath("$.mensagem").value("Campos inválidos"))
			.andExpect(jsonPath("$.path").value("/api/usuarios"))
			.andExpect(jsonPath("$.timestamp").exists())
			.andExpect(jsonPath("$.erros[0].campo").value("buscarPorEmail.email"))
			.andExpect(jsonPath("$.erros[0].mensagem").value("Deve ser um endereço valido!"));
	}
	
	@DisplayName("DELETE - Deve deleter o usuario pelo email com sucesso")
	@Test
	void deveDeletarUsuarioComSucesso() throws Exception{
		
		String email = "teste@teste.com";
		
		doNothing().when(usuarioService).deletaUsuarioPeloEmail(email);
		
		mockMvc.perform(delete("/api/usuarios")
				.param("email", email)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isNoContent());
	}
	
	@DisplayName("DELETE - Deve lançar exception se o email for invalido")
	@Test
	void deveLancarExceptionSeEmailForInvalidoParaDeletar() throws Exception{
		
		String email = "email-invalido";
		
		mockMvc.perform(delete("/api/usuarios")
				.param("email", email)
				.contentType(MediaType.APPLICATION_JSON))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.mensagem").value("Campos inválidos"))
		.andExpect(jsonPath("$.path").value("/api/usuarios"))
		.andExpect(jsonPath("$.timestamp").exists())
		.andExpect(jsonPath("$.erros[0].campo").value("deletaPeloEmail.email"))
		.andExpect(jsonPath("$.erros[0].mensagem").value("Deve ser um endereço valido!"));
		
	}
	
	@DisplayName("PUT - Deve atualiza um usuario pelo email com sucesso")
	@Test
	void deveAtualizarUsuarioPeloEmailComSucesso() throws Exception{
		
		LocalDateTime dataFixa = LocalDateTime.of(2025, 8, 6, 12, 0);
		String email = "teste@teste.com";
		
		AtualizaUsuarioDto usuarioEntrada = new AtualizaUsuarioDto("Teste Teste", email, "(11) 91234-5678");
		
		UsuarioDto entity = new UsuarioDto(1L,"Luan Brito", "teste@teste.com", dataFixa, "(11) 91234-5678");

		when(usuarioService.atualizaUsuarioPeloEmail(email, usuarioEntrada)).thenReturn(entity);
		
		mockMvc.perform(put("/api/usuarios")
				.param("email", email)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(usuarioEntrada)))
		.andExpect(status().isOk())
		.andExpect(jsonPath("$.id").value(1))
		.andExpect(jsonPath("$.nome").value("Luan Brito"))
		.andExpect(jsonPath("$.email").value("teste@teste.com"))
		.andExpect(jsonPath("$.telefone").value("(11) 91234-5678"));
	}
	
	@DisplayName("PUT - Deve lançar exception se o email nao for valido")
	@Test
	void deveLancarExceptionSeEmailNaoForValidoPUT() throws Exception{
		
		String email = "email-invalido";
		
		AtualizaUsuarioDto usuarioEntrada = new AtualizaUsuarioDto("Teste Teste", email, "(11) 91234-5678");

		
		mockMvc.perform(put("/api/usuarios")
				.param("email", email)
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(usuarioEntrada)))
		.andExpect(status().isBadRequest())
		.andExpect(jsonPath("$.status").value(400))
		.andExpect(jsonPath("$.mensagem").value("Campos inválidos"))
		.andExpect(jsonPath("$.path").value("/api/usuarios"))
		.andExpect(jsonPath("$.timestamp").exists())
		.andExpect(jsonPath("$.erros[0].campo").value("email"))
		.andExpect(jsonPath("$.erros[0].mensagem").value("Deve ser um endereço valido!"));
	}
}
