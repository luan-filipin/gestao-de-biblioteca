package com.api.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.biblioteca.dto.CriarUsuarioDto;
import com.api.biblioteca.dto.response.AtualizaUsuarioDto;
import com.api.biblioteca.dto.response.UsuarioDto;
import com.api.biblioteca.entity.Usuario;
import com.api.biblioteca.exception.EmailInexistenteException;
import com.api.biblioteca.exception.EmailJaExisteException;
import com.api.biblioteca.mapper.CriarUsuarioMapper;
import com.api.biblioteca.mapper.UsuarioMapper;
import com.api.biblioteca.repository.UsuarioRepository;
import com.api.biblioteca.service.validador.UsuarioValidador;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {
	
	@Mock
	private UsuarioRepository usuarioRepository;
	@Mock
	private UsuarioMapper usuarioMapper;
	@Mock
	private CriarUsuarioMapper criarUsuarioMapper;
	@Mock
	private UsuarioValidador usuarioValidador;
	@InjectMocks
	private UsuarioServiceImp usuarioServiceImp;
	
	// Metodos auxiliares para não repetir a criação dos objetos
	private Usuario criarUsuarioAux(String nome, String email, String telefone) {
		return new Usuario(nome, email, LocalDateTime.of(2025, 8, 6, 12, 0), telefone);
	}
	
	private UsuarioDto criarDtoEsperadoAux(Long id, String nome, String email, LocalDateTime dataCadastro, String telefone) {
		return new UsuarioDto(id, nome, email, dataCadastro, telefone);
	}
	
	private CriarUsuarioDto criarUsuarioDtoAux(String nome, String email, String telefone) {
		return new CriarUsuarioDto(nome, email, telefone);
	}
	
	private AtualizaUsuarioDto criarUsuarioDtoAtualizadoAux(String nome, String telefone) {
		return new AtualizaUsuarioDto(nome, telefone);
	}
	
	@DisplayName("POST - Deve criar um usuario com sucesso.")
	@Test
	void deveCriarUsuarioComSucesso() {

	    CriarUsuarioDto dto = criarUsuarioDtoAux("Luan Brito", "teste@teste.com", "(11) 91234-5678");
	    Usuario entity = criarUsuarioAux("Luan Brito", "teste@teste.com", "(11) 91234-5678"); 
	    UsuarioDto usuarioDtoEsperado = criarDtoEsperadoAux(entity.getId(), "Luan Brito", "teste@teste.com", entity.getDataCadastro(), "(11) 91234-5678");

	    when(criarUsuarioMapper.toEntity(dto)).thenReturn(entity);
	    when(usuarioRepository.save(entity)).thenReturn(entity); // simula o retorno do save
	    when(usuarioMapper.toUsuarioDto(entity)).thenReturn(usuarioDtoEsperado);

	    UsuarioDto resultado = usuarioServiceImp.criarUsuario(dto);

	    assertNotNull(resultado);
	    assertEquals(usuarioDtoEsperado.id(), resultado.id());
	    assertEquals(usuarioDtoEsperado.nome(), resultado.nome());
	    assertEquals(usuarioDtoEsperado.email(), resultado.email());
	    assertEquals(usuarioDtoEsperado.telefone(), resultado.telefone());
	    assertEquals(usuarioDtoEsperado.dataCadastro(), resultado.dataCadastro());

	    verify(usuarioValidador).validarEmailNaoCadastrado(dto.email());
	    verify(criarUsuarioMapper).toEntity(dto);
	    verify(usuarioRepository).save(entity);
	    verify(usuarioMapper).toUsuarioDto(entity);
	}

	
	@DisplayName("POST - Deve lançar exception ao validar o email.")
	@Test
	void deveLancarExceptionAoValidarEmail() {
		
		CriarUsuarioDto dto = criarUsuarioDtoAux("Luan Brito", "teste@teste.com", "(11) 91234-5678");
		
		doThrow(new EmailJaExisteException()).when(usuarioValidador).validarEmailNaoCadastrado(dto.email());
		
		EmailJaExisteException exception = assertThrows(EmailJaExisteException.class, () -> {
			usuarioServiceImp.criarUsuario(dto);
		});
		
		assertEquals("O email ja existe!", exception.getMessage());
		
		verify(criarUsuarioMapper, never()).toEntity(any());
		verify(usuarioRepository, never()).save(any());
		
	}
	
	@DisplayName("GET - Deve pesquisar usuario pelo email com sucesso.")
	@Test
	void devePesquisarUsuarioPeloEmailComSucesso() {
		
		String email = "teste@teste.com";

		Usuario usuario = criarUsuarioAux("Luan Brito", "teste@teste.com", "(11) 91234-5678");
		UsuarioDto usuarioDtoEsperado = criarDtoEsperadoAux(usuario.getId(), "Luan Brito", "teste@teste.com", usuario.getDataCadastro(), "(11) 91234-5678");

		when(usuarioValidador.buscarPorEmailOuLancarEmailInexistente(email)).thenReturn(usuario);
		when(usuarioMapper.toUsuarioDto(usuario)).thenReturn(usuarioDtoEsperado);

		UsuarioDto resultado = usuarioServiceImp.buscarUsuarioPorEmail(email);

		assertNotNull(resultado);
		assertEquals(usuarioDtoEsperado.id(), resultado.id());
		assertEquals(usuarioDtoEsperado.nome(), resultado.nome());
		assertEquals(usuarioDtoEsperado.email(), resultado.email());
		assertEquals(usuarioDtoEsperado.telefone(), resultado.telefone());
		assertEquals(usuarioDtoEsperado.dataCadastro(), resultado.dataCadastro());

		verify(usuarioValidador).buscarPorEmailOuLancarEmailInexistente(email);
		verify(usuarioMapper).toUsuarioDto(usuario);
	}

	
	@DisplayName("GET - Deve lançar exceção ao não localizar o email")
	@Test
	void deveLancarExceptionAoNaoLocalizarOEmail() {
		
	    String email = "inexistente@teste.com";

	    when(usuarioValidador.buscarPorEmailOuLancarEmailInexistente(email))
	        .thenThrow(new EmailInexistenteException());

	    EmailInexistenteException exception = assertThrows(EmailInexistenteException.class, () -> {
	        usuarioServiceImp.buscarUsuarioPorEmail(email);
	    });
	    
	    assertEquals("O email não existe!", exception.getMessage());

	    verify(usuarioValidador).buscarPorEmailOuLancarEmailInexistente(email);
	}


	@DisplayName("DELETE - Deve deletar o usuario pelo email com sucesso.")
	@Test
	void deveDeletarUsuarioPeloEmail() {
		
		String email = "teste@teste.com";
		Usuario usuario = criarUsuarioAux("Luan Brito", "teste@teste.com", "(11) 91234-5678");
	    
		when(usuarioValidador.buscarPorEmailOuLancarEmailInexistente(email)).thenReturn(usuario);
	    
	    usuarioServiceImp.deletaUsuarioPeloEmail(email);
	    
	    verify(usuarioRepository).delete(usuario);
	}
	
	@DisplayName("DELETE - Deve lançar exceção ao tentar deletar um usuario com o email inexistente")
	@Test
	void deveLancarExceptionAoDeletarUsuarioComEmailInexistente() {
		
		String email = "teste@teste.com";
		
	    when(usuarioValidador.buscarPorEmailOuLancarEmailInexistente(email))
        .thenThrow(new EmailInexistenteException());
		
		EmailInexistenteException exception = assertThrows(EmailInexistenteException.class, ()->{
			usuarioServiceImp.deletaUsuarioPeloEmail(email);
		});
		
		assertEquals("O email não existe!", exception.getMessage());
		
		verify(usuarioRepository, never()).delete(any());
	}
	
	@DisplayName("PUT - Deve atualizar um usuario pelo email com sucesso.")
	@Test
	void deveAtualizarUsuarioComSucesso() {
		
		String email = "teste@teste.com";
		AtualizaUsuarioDto dto = criarUsuarioDtoAtualizadoAux("Luan Brito", "(11) 91234-5678");
		Usuario usuarioExistente = criarUsuarioAux("Nome Antigo", "teste@teste.com", "(11) 91234-5678");
		UsuarioDto dtoEsperado = criarDtoEsperadoAux(1L, "Luan Brito", "teste@teste.com", LocalDateTime.now(), "(11) 91234-5678");

		when(usuarioValidador.buscarPorEmailOuLancarEmailInexistente(email)).thenReturn(usuarioExistente);
		doNothing().when(usuarioMapper).atualizaDto(dto, usuarioExistente);
		when(usuarioRepository.save(usuarioExistente)).thenReturn(usuarioExistente);
		when(usuarioMapper.toUsuarioDto(usuarioExistente)).thenReturn(dtoEsperado);

		UsuarioDto resultado = usuarioServiceImp.atualizaUsuarioPeloEmail(email, dto);

		assertNotNull(resultado);
		assertEquals(dtoEsperado.id(), resultado.id());
		assertEquals(dtoEsperado.nome(), resultado.nome());
		assertEquals(dtoEsperado.email(), resultado.email());
		assertEquals(dtoEsperado.telefone(), resultado.telefone());
		assertEquals(dtoEsperado.dataCadastro(), resultado.dataCadastro());

		verify(usuarioValidador).buscarPorEmailOuLancarEmailInexistente(email);
		verify(usuarioMapper).atualizaDto(dto, usuarioExistente);
		verify(usuarioRepository).save(usuarioExistente);
		verify(usuarioMapper).toUsuarioDto(usuarioExistente);

	}
	
	@DisplayName("PUT - Deve lançar exception quando o email nao existir.")
	@Test
	void deveLancarExceptionQuandoEmailDaUrlDiferenteDoCorpo() {

	    String email = "";
	    AtualizaUsuarioDto dto = criarUsuarioDtoAtualizadoAux("Luan Brito", "(11) 91234-5678");

		when(usuarioValidador.buscarPorEmailOuLancarEmailInexistente(email)).thenThrow(new EmailInexistenteException());

	    EmailInexistenteException exception = assertThrows(
	    		EmailInexistenteException.class,
	        () -> usuarioServiceImp.atualizaUsuarioPeloEmail(email, dto)
	    );

	    assertEquals("O email não existe!", exception.getMessage());

	    verify(usuarioValidador).buscarPorEmailOuLancarEmailInexistente(email);
	    verify(usuarioMapper, never()).atualizaDto(any(), any());
	    verify(usuarioRepository, never()).save(any());
	}

}








