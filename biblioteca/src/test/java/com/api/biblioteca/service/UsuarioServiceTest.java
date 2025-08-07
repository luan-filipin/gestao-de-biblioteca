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
import com.api.biblioteca.exception.EmailDiferenteDoCorpoException;
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
	
	@DisplayName("POST - Deve criar um usuario com sucesso.")
	@Test
	void deveCriarUsuarioComSucesso() {

	    CriarUsuarioDto dto = new CriarUsuarioDto("Luan Brito", "teste@teste.com", "(11) 91234-5678");

	    Usuario entity = new Usuario();
	    entity.setId(1L); // ID simulado após persistência
	    entity.setNome(dto.nome());
	    entity.setEmail(dto.email());
	    entity.setTelefone(dto.telefone());
	    entity.setDataCadastro(LocalDateTime.of(2025, 8, 6, 12, 0)); // data simulada

	    UsuarioDto usuarioDtoEsperado = new UsuarioDto(
	        entity.getId(),
	        entity.getNome(),
	        entity.getEmail(),
	        entity.getDataCadastro(),
	        entity.getTelefone()
	    );

	    when(criarUsuarioMapper.toEntity(dto)).thenReturn(entity);
	    when(usuarioRepository.save(entity)).thenReturn(entity); // simula o retorno do save
	    when(usuarioMapper.toDto(entity)).thenReturn(usuarioDtoEsperado);

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
	    verify(usuarioMapper).toDto(entity);
	}

	
	@DisplayName("POST - Deve lançar exception ao validar o email.")
	@Test
	void deveLancarExceptionAoValidarEmail() {
		
		CriarUsuarioDto dto = new CriarUsuarioDto("Luan Brito", "teste@teste.com", "(11) 91234-5678");

		
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

		Usuario usuario = new Usuario();
		usuario.setId(1L);
		usuario.setNome("Luan Brito");
		usuario.setEmail(email);
		usuario.setTelefone("(11) 91234-5678");
		usuario.setDataCadastro(LocalDateTime.now());

		UsuarioDto usuarioDtoEsperado = new UsuarioDto(
		        usuario.getId(),
		        usuario.getNome(),
		        usuario.getEmail(),
		        usuario.getDataCadastro(),
		        usuario.getTelefone()
		);		

		// Mocks corretos
		when(usuarioValidador.buscarPorEmailOuLancarEmailInexistente(email)).thenReturn(usuario);
		when(usuarioMapper.toDto(usuario)).thenReturn(usuarioDtoEsperado);

		UsuarioDto resultado = usuarioServiceImp.buscarUsuarioPorEmail(email);

		// Verificações
		assertNotNull(resultado);
		assertEquals(usuarioDtoEsperado.id(), resultado.id());
		assertEquals(usuarioDtoEsperado.nome(), resultado.nome());
		assertEquals(usuarioDtoEsperado.email(), resultado.email());
		assertEquals(usuarioDtoEsperado.telefone(), resultado.telefone());
		assertEquals(usuarioDtoEsperado.dataCadastro(), resultado.dataCadastro());

		verify(usuarioValidador).buscarPorEmailOuLancarEmailInexistente(email);
		verify(usuarioMapper).toDto(usuario);
	}

	
	@DisplayName("GET - Deve lançar exceção ao não localizar o email")
	@Test
	void deveLancarExceptionAoNaoLocalizarOEmail() {
	    String email = "inexistente@teste.com";

	    // Mocka o comportamento do validador para lançar exceção
	    when(usuarioValidador.buscarPorEmailOuLancarEmailInexistente(email))
	        .thenThrow(new EmailInexistenteException());

	    // Verifica se a exceção é lançada corretamente
	    EmailInexistenteException exception = assertThrows(EmailInexistenteException.class, () -> {
	        usuarioServiceImp.buscarUsuarioPorEmail(email);
	    });
	    
	    assertEquals("O email não existe!", exception.getMessage());

	    // Verifica se o validador foi chamado
	    verify(usuarioValidador).buscarPorEmailOuLancarEmailInexistente(email);
	}


	@DisplayName("DELETE - Deve deletar o usuario pelo email com sucesso.")
	@Test
	void deveDeletarUsuarioPeloEmail() {
		
		String email = "teste@teste.com";
		
		Usuario usuario = new Usuario();
		usuario.setId(1L);
		usuario.setNome("Luan Brito");
		usuario.setEmail(email);
	    usuario.setTelefone("(11) 91234-5678");
	    usuario.setDataCadastro(LocalDateTime.now());
	    
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
		
		// Arrange
		String email = "teste@teste.com";
		AtualizaUsuarioDto dto = new AtualizaUsuarioDto("Luan Brito", "teste@teste.com", "(11) 91234-5678");

		Usuario usuarioExistente = new Usuario();
		usuarioExistente.setId(1L);
		usuarioExistente.setNome("Nome antigo");
		usuarioExistente.setEmail(email);
		usuarioExistente.setTelefone("(11) 91234-5678");
		usuarioExistente.setDataCadastro(LocalDateTime.now());

		UsuarioDto dtoEsperado = new UsuarioDto(
		        usuarioExistente.getId(),
		        dto.nome(),
		        dto.email(),
		        usuarioExistente.getDataCadastro(),
		        dto.telefone()
		);

		// Mock correto
		when(usuarioValidador.buscarPorEmailOuLancarEmailInexistente(email)).thenReturn(usuarioExistente);
		doNothing().when(usuarioMapper).atualizaDto(dto, usuarioExistente);
		when(usuarioRepository.save(usuarioExistente)).thenReturn(usuarioExistente);
		when(usuarioMapper.toDto(usuarioExistente)).thenReturn(dtoEsperado);

		// Act
		UsuarioDto resultado = usuarioServiceImp.atualizaUsuarioPeloEmail(email, dto);

		// Assert
		assertNotNull(resultado);
		assertEquals(dtoEsperado.id(), resultado.id());
		assertEquals(dtoEsperado.nome(), resultado.nome());
		assertEquals(dtoEsperado.email(), resultado.email());
		assertEquals(dtoEsperado.telefone(), resultado.telefone());
		assertEquals(dtoEsperado.dataCadastro(), resultado.dataCadastro());

		// Verify
		verify(usuarioValidador).buscarPorEmailOuLancarEmailInexistente(email);
		verify(usuarioValidador).validaEmailDaUrlDiferenteDoCorpo(email, dto.email());
		verify(usuarioMapper).atualizaDto(dto, usuarioExistente);
		verify(usuarioRepository).save(usuarioExistente);
		verify(usuarioMapper).toDto(usuarioExistente);

	}
	
	@DisplayName("PUT - Deve lançar exception quando o email da URL for diferente do email do corpo")
	@Test
	void deveLancarExceptionQuandoEmailDaUrlDiferenteDoCorpo() {

	    String emailUrl = "url@teste.com";
	    String emailCorpo = "corpo@teste.com";

	    AtualizaUsuarioDto dto = new AtualizaUsuarioDto("Luan Brito", emailCorpo, "(11) 91234-5678");

	    Usuario usuarioExistente = new Usuario();
	    usuarioExistente.setId(1L);
	    usuarioExistente.setNome("Nome Antigo");
	    usuarioExistente.setEmail(emailCorpo);
	    usuarioExistente.setTelefone("(11) 90000-0000");
	    usuarioExistente.setDataCadastro(LocalDateTime.now());

		when(usuarioValidador.buscarPorEmailOuLancarEmailInexistente(emailUrl)).thenReturn(usuarioExistente);
	    doThrow(new EmailDiferenteDoCorpoException()).when(usuarioValidador)
	        .validaEmailDaUrlDiferenteDoCorpo(emailUrl, emailCorpo);

	    EmailDiferenteDoCorpoException exception = assertThrows(
	        EmailDiferenteDoCorpoException.class,
	        () -> usuarioServiceImp.atualizaUsuarioPeloEmail(emailUrl, dto)
	    );

	    assertEquals("O email no corpo da requisição não pode ser diferente do email da URL.", exception.getMessage());

	    verify(usuarioValidador).buscarPorEmailOuLancarEmailInexistente(emailUrl);
	    verify(usuarioValidador).validaEmailDaUrlDiferenteDoCorpo(emailUrl, emailCorpo);
	    verify(usuarioMapper, never()).atualizaDto(any(), any());
	    verify(usuarioRepository, never()).save(any());
	}

}








