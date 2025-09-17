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

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.biblioteca.dto.AtualizarEmprestimoDto;
import com.api.biblioteca.dto.CriarEmprestimoDto;
import com.api.biblioteca.dto.response.AtualizarEmprestimoRespostaDto;
import com.api.biblioteca.dto.response.EmprestimoDto;
import com.api.biblioteca.entity.Emprestimo;
import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.entity.Usuario;
import com.api.biblioteca.exception.IdEmprestimoNaoExisteException;
import com.api.biblioteca.exception.IdLivroNaoExisteException;
import com.api.biblioteca.exception.IdUsuarioNaoExisteException;
import com.api.biblioteca.exception.LivroIndisponivelException;
import com.api.biblioteca.mapper.EmprestimoMapper;
import com.api.biblioteca.repository.EmprestimoRepository;
import com.api.biblioteca.service.validador.EmprestimoValidador;
import com.api.biblioteca.service.validador.LivroValidador;
import com.api.biblioteca.service.validador.UsuarioValidador;

@ExtendWith(MockitoExtension.class)
class EmprestimoServiceTest {

	@Mock
	private EmprestimoRepository emprestimoRepository;
	
	@Mock
	private EmprestimoValidador emprestimoValidador;
	
	@Mock
	private UsuarioValidador usuarioValidador;
	
	@Mock
	private LivroValidador livroValidador;
	
	@Mock
	private EmprestimoMapper emprestimoMapper;
	
	@InjectMocks
	private EmprestimoServiceImp emprestimoServiceImp;
	
	// Metodos auxiliares para não repetir a criação dos objetos
	private Usuario criarUsuarioAux(String nome, String email, String telefone) {
		return new Usuario(nome, email, LocalDateTime.of(2025, 8, 7, 10, 0), telefone);
	}
	
	private Livro criarLivroAux(String titulo, String autor, String isbn, String categoria) {	
		return new Livro(titulo, autor, isbn, LocalDate.of(2025, 8, 7), categoria);
	}
	
	private Emprestimo criarEmprestimoAux(Usuario usuario, Livro livro) {
		return new Emprestimo(usuario, livro, LocalDateTime.of(2025, 8, 7, 10, 0), LocalDate.of(2025, 8, 7), true);
	}
	
	private CriarEmprestimoDto criarEmprestimoDtoAux(Long idLivro, Long idUsuario) {
		return new CriarEmprestimoDto(idLivro, idUsuario, LocalDate.of(2025, 8, 7));
	}
	
	@DisplayName("POST - Deve criar um emprestimo com sucesso")
	@Test
	void deveCriarEmprestimoComSucesso() {
		
		LocalDateTime dataEmprestimoFixa = LocalDateTime.of(2025, 8, 7, 10, 0);
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		CriarEmprestimoDto emprestimoEntrada = criarEmprestimoDtoAux(1L, 1L);
		Usuario usuario = criarUsuarioAux("Luan Brito", "teste@teste.com", "(11) 91234-5678");
		Livro livro = criarLivroAux("Clean Code", "Robert C. Martin", "9780132350884", "Programação");
		Emprestimo emprestimo = criarEmprestimoAux(usuario, livro);
		
		EmprestimoDto emprestimoDtoEsperado = new EmprestimoDto(1L, "Luan Brito", "Clean Code", dataEmprestimoFixa, dataFixa, true);
		
		
		doNothing().when(emprestimoValidador).validaSeLivroEstaEmprestado(emprestimoEntrada.livroId());
		when(usuarioValidador.validaSeIDDoUsuarioExiste(emprestimoEntrada.usuarioId())).thenReturn(usuario);
		when(livroValidador.validaSeIdDoLivroExiste(emprestimoEntrada.livroId())).thenReturn(livro);
		when(emprestimoMapper.toEntity(emprestimoEntrada, usuario, livro)).thenReturn(emprestimo);
		when(emprestimoRepository.save(emprestimo)).thenReturn(emprestimo);
		when(emprestimoMapper.toDto(emprestimo)).thenReturn(emprestimoDtoEsperado);
		
		EmprestimoDto resultado = emprestimoServiceImp.criarEmprestimo(emprestimoEntrada);
		
		assertNotNull(resultado);
		assertEquals(emprestimoDtoEsperado.id(), resultado.id());
		assertEquals(emprestimoDtoEsperado.nomeUsuario(), resultado.nomeUsuario());
		assertEquals(emprestimoDtoEsperado.tituloLivro(), resultado.tituloLivro());
		assertEquals(emprestimoDtoEsperado.dataEmprestimo(), resultado.dataEmprestimo());
		assertEquals(emprestimoDtoEsperado.dataDevolucao(), resultado.dataDevolucao());
		
		verify(emprestimoValidador).validaSeLivroEstaEmprestado(emprestimoEntrada.livroId());
		verify(usuarioValidador).validaSeIDDoUsuarioExiste(emprestimoEntrada.usuarioId());
		verify(livroValidador).validaSeIdDoLivroExiste(emprestimoEntrada.livroId());
		verify(emprestimoMapper).toEntity(emprestimoEntrada, usuario, livro);
		verify(emprestimoRepository).save(emprestimo);
		verify(emprestimoMapper).toDto(emprestimo);
	}
	
	@DisplayName("POST - Deve lançar Exception se o livro ja estiver emprestado.")
	@Test
	void deveLancarExceptionSeLivroEstiverEmprestado() {
		
		CriarEmprestimoDto emprestimoEntrada = criarEmprestimoDtoAux(1L, 1L);
		
		doThrow(new LivroIndisponivelException()).when(emprestimoValidador).validaSeLivroEstaEmprestado(emprestimoEntrada.livroId());
		
		LivroIndisponivelException exception = assertThrows(LivroIndisponivelException.class, ()->{
			emprestimoServiceImp.criarEmprestimo(emprestimoEntrada);
		});
		
		assertEquals("O livro já está emprestado no momento.", exception.getMessage());
		
		verify(usuarioValidador, never()).validaSeIDDoUsuarioExiste(any());
		verify(livroValidador, never()).validaSeIdDoLivroExiste(any());
		verify(emprestimoMapper, never()).toEntity(any(), any(), any());
		verify(emprestimoRepository, never()).save(any());
		verify(emprestimoMapper, never()).toDto(any());		
		
	}
	
	@DisplayName("POST - Deve lançar Exception se o usuario não existir.")
	@Test
	void deveLancarExceptionSeUsuarioNaoExistir() {
		
		CriarEmprestimoDto emprestimoEntrada = criarEmprestimoDtoAux(1L, 1L);		
		
		doNothing().when(emprestimoValidador).validaSeLivroEstaEmprestado(emprestimoEntrada.livroId());
		
		when(usuarioValidador.validaSeIDDoUsuarioExiste(emprestimoEntrada.usuarioId())).thenThrow(new IdUsuarioNaoExisteException());
		
		IdUsuarioNaoExisteException exception = assertThrows(IdUsuarioNaoExisteException.class, ()->{
			emprestimoServiceImp.criarEmprestimo(emprestimoEntrada);
		});
		
		assertEquals("O id do usuario não existe", exception.getMessage());
		
		verify(livroValidador, never()).validaSeIdDoLivroExiste(any());
		verify(emprestimoMapper, never()).toEntity(any(), any(), any());
		verify(emprestimoRepository, never()).save(any());
		verify(emprestimoMapper, never()).toDto(any());	
	}
	
	@DisplayName("POST - Deve lançar Exception se o livro não existir.")
	@Test
	void deveLancarExceptionSeLivroNaoExistir() {
		
		CriarEmprestimoDto emprestimoEntrada = criarEmprestimoDtoAux(1L, 1L);
		Usuario usuario = criarUsuarioAux("Luan Brito", "teste@teste.com", "(11) 91234-5678");		
		
		doNothing().when(emprestimoValidador).validaSeLivroEstaEmprestado(emprestimoEntrada.livroId());
		when(usuarioValidador.validaSeIDDoUsuarioExiste(emprestimoEntrada.usuarioId())).thenReturn(usuario);
		when(livroValidador.validaSeIdDoLivroExiste(emprestimoEntrada.livroId())).thenThrow(new IdLivroNaoExisteException());
		
		IdLivroNaoExisteException exception = assertThrows(IdLivroNaoExisteException.class, ()->{
			emprestimoServiceImp.criarEmprestimo(emprestimoEntrada);
		});
		
		assertEquals("O id do livro nao existe!", exception.getMessage());
		
		verify(emprestimoMapper, never()).toEntity(any(), any(), any());
		verify(emprestimoRepository, never()).save(any());
		verify(emprestimoMapper, never()).toDto(any());	
	}
	
	@DisplayName("PUT - Deve atualizar um emprestimo com sucesso")
	@Test
	void deveAtualizarEmprestimoComSucesso() {
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		LocalDateTime dataEmprestimoFixa = LocalDateTime.of(2025, 8, 7, 10, 0);
		Long id = 1L;
		
		AtualizarEmprestimoDto emprestimoEntrada = new AtualizarEmprestimoDto(dataFixa, true);
		Usuario usuario = criarUsuarioAux("Luan Brito", "teste@teste.com", "(11) 91234-5678");
		Livro livro = criarLivroAux("Clean Code", "Robert C. Martin", "9780132350884", "Programação");
		Emprestimo emprestimo = criarEmprestimoAux(usuario, livro);
		
		AtualizarEmprestimoRespostaDto emprestimoDtoEsperado = new AtualizarEmprestimoRespostaDto(1L, "Luan Brito", "Clean Code", dataEmprestimoFixa, dataFixa, true);

		
		when(emprestimoValidador.buscaIdOuLancaException(id)).thenReturn(emprestimo);
		doNothing().when(emprestimoMapper).atualizaDto(emprestimoEntrada, emprestimo);
		when(emprestimoRepository.save(emprestimo)).thenReturn(emprestimo);
		when(emprestimoMapper.toAtualizarDto(emprestimo)).thenReturn(emprestimoDtoEsperado);
		
		AtualizarEmprestimoRespostaDto resultado = emprestimoServiceImp.atualizarEmprestimoPeloId(id, emprestimoEntrada);
		
		assertNotNull(resultado);
		assertEquals(emprestimoDtoEsperado.id(), resultado.id());
		assertEquals(emprestimoDtoEsperado.nomeUsuario(), resultado.nomeUsuario());
		assertEquals(emprestimoDtoEsperado.tituloLivro(), resultado.tituloLivro());
		assertEquals(emprestimoDtoEsperado.dataEmprestimo(), resultado.dataEmprestimo());
		assertEquals(emprestimoDtoEsperado.dataDevolucao(), resultado.dataDevolucao());
		assertEquals(emprestimoDtoEsperado.status(), resultado.status());
		
		verify(emprestimoValidador).buscaIdOuLancaException(id);
		verify(emprestimoMapper).atualizaDto(emprestimoEntrada, emprestimo);
		verify(emprestimoRepository).save(emprestimo);
		verify(emprestimoMapper).toAtualizarDto(emprestimo);
	}
	
	@DisplayName("PUT - Deve lancar exception se o emprestimo nao existir")
	@Test
	void deveLancarExceptionSeEmprestimoNaoExistir() {
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		Long id = 1L;
		
		AtualizarEmprestimoDto emprestimoEntrada = new AtualizarEmprestimoDto(dataFixa, true);
		
		when(emprestimoValidador.buscaIdOuLancaException(id)).thenThrow(new IdEmprestimoNaoExisteException());
		
		IdEmprestimoNaoExisteException exception = assertThrows(IdEmprestimoNaoExisteException.class, ()->{
			emprestimoServiceImp.atualizarEmprestimoPeloId(id, emprestimoEntrada);
		});
		
		assertEquals("O emprestimo não existe!", exception.getMessage());
		
		verify(emprestimoMapper, never()).atualizaDto(any(), any());
		verify(emprestimoRepository, never()).save(any());
		verify(emprestimoMapper, never()).toAtualizarDto(any());
	}
}	
