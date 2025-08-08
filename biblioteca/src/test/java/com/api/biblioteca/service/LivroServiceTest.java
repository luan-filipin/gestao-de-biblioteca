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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.api.biblioteca.dto.AtualizarLivroDto;
import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.LivroDto;
import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.exception.IsbnInexistenteException;
import com.api.biblioteca.exception.IsbnJaExisteException;
import com.api.biblioteca.mapper.CriarLivroMapper;
import com.api.biblioteca.mapper.LivroMapper;
import com.api.biblioteca.repository.LivroRepository;
import com.api.biblioteca.service.validador.LivroValidador;

@ExtendWith(MockitoExtension.class)
class LivroServiceTest {

	@Mock
	private LivroRepository livroRepository;
	@Mock
	private LivroValidador livroValidador;
	@Mock
	private CriarLivroMapper criarLivroMapper;
	@Mock
	private LivroMapper livroMapper;
	@InjectMocks
	private LivroServiceImp livroServiceImp;
	
	@DisplayName("POST - Deve criar o livro com sucesso")
	@Test
	void deveCriarOLivroComSucesso() {
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);

		CriarLivroDto dtoEntrada = new CriarLivroDto("Clean Code", "Robert C. Martin", "9780132350884", "Programação", dataFixa);
		
		Livro entitySalvo = new Livro();
		entitySalvo.setId(1L);
		entitySalvo.setTitulo("Clean Code");
		entitySalvo.setAutor("Robert C. Martin");
		entitySalvo.setIsbn("9780132350884");
		entitySalvo.setDataPublicacao(dataFixa);
		entitySalvo.setCategoria("Programação");
		
		LivroDto dtoEsperado = new LivroDto(1L, "Clean Code", "Robert C. Martin", "9780132350884", dataFixa, "Programação");
		
		doNothing().when(livroValidador).validaSeOLivroExiste(dtoEntrada.isbn());
		when(criarLivroMapper.toEntity(dtoEntrada)).thenReturn(entitySalvo);
		when(livroRepository.save(entitySalvo)).thenReturn(entitySalvo);
		when(livroMapper.toDto(entitySalvo)).thenReturn(dtoEsperado);
		
		LivroDto resultado = livroServiceImp.criarLivro(dtoEntrada);
		
		assertNotNull(resultado);
		assertEquals(dtoEsperado.id(), resultado.id());
		assertEquals(dtoEsperado.titulo(), resultado.titulo());
		assertEquals(dtoEsperado.autor(), resultado.autor());
		assertEquals(dtoEsperado.isbn(), resultado.isbn());
		assertEquals(dtoEsperado.categoria(), resultado.categoria());
		
		verify(livroValidador).validaSeOLivroExiste(dtoEntrada.isbn());
		verify(criarLivroMapper).toEntity(dtoEntrada);
		verify(livroRepository).save(entitySalvo);
		verify(livroMapper).toDto(entitySalvo);
	}
	
	@DisplayName("POST - Deve lançar exception se o isbn for existente")
	@Test
	void deveLancarExceptionSeOIsbnForExistente() {
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		String isbn = "9780132350884";
		CriarLivroDto dtoEntrada = new CriarLivroDto("Clean Code", "Robert C. Martin", "9780132350884", "Programação", dataFixa);

		doThrow(new IsbnJaExisteException()).when(livroValidador).validaSeOLivroExiste(isbn);
		
		IsbnJaExisteException exception = assertThrows(IsbnJaExisteException.class, ()->{
			livroServiceImp.criarLivro(dtoEntrada);
		});
		
		assertEquals("O Livro ja existe", exception.getMessage());
		
		verify(criarLivroMapper, never()).toEntity(any());
		verify(livroRepository, never()).save(any());
		verify(livroMapper, never()).toDto(any());
		
	}
	
	@DisplayName("GET - Deve busca livro pelo isbn com sucesso.")
	@Test
	void deveBuscarLivroPeloIsbnComSucesso() {
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		String isbn = "9780132350884";
		
		Livro entity = new Livro();
		entity.setId(1L);
		entity.setTitulo("Clean Code");
		entity.setAutor("Robert C. Martin");
		entity.setIsbn(isbn);
		entity.setDataPublicacao(dataFixa);
		entity.setCategoria("Programação");
		
		LivroDto dtoEsperado = new LivroDto(1L, "Clean Code", "Robert C. Martin", isbn, dataFixa, "Programação");
		
		when(livroValidador.buscaPorIsbnOuLancaException(isbn)).thenReturn(entity);
		when(livroMapper.toDto(entity)).thenReturn(dtoEsperado);
		
		LivroDto resultado = livroServiceImp.buscaLivroPeloIsbn(isbn);
		
		assertNotNull(resultado);
		assertEquals(dtoEsperado.id(), resultado.id());
		assertEquals(dtoEsperado.titulo(), resultado.titulo());
		assertEquals(dtoEsperado.autor(), resultado.autor());
		assertEquals(dtoEsperado.isbn(), resultado.isbn());
		assertEquals(dtoEsperado.categoria(), resultado.categoria());
		
		verify(livroValidador).buscaPorIsbnOuLancaException(isbn);
		verify(livroMapper).toDto(entity);

	}
	
	@DisplayName("GET - Deve lançar exception se o isbn nao for existente")
	@Test
	void deveLancarExceptionSeIsbnNaoForExistente() {
		
		String isbn = "invalido-isbn";
		
		doThrow(new IsbnInexistenteException()).when(livroValidador).buscaPorIsbnOuLancaException(isbn);
		
		IsbnInexistenteException exception = assertThrows(IsbnInexistenteException.class, ()->{
			livroServiceImp.buscaLivroPeloIsbn(isbn);
		});
		
		assertEquals("O livro nao existe", exception.getMessage());
		
		verify(livroMapper, never()).toDto(any());
	}
	
	@DisplayName("DELETE - Deve deletar um livro pelo isbn com sucesso")
	@Test
	void deveDeletarLivroPeloIsbnComSucesso() {
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		String isbn = "9780132350884";
		
		Livro entity = new Livro();
		entity.setId(1L);
		entity.setTitulo("Clean Code");
		entity.setAutor("Robert C. Martin");
		entity.setIsbn(isbn);
		entity.setDataPublicacao(dataFixa);
		entity.setCategoria("Programação");
		
		when(livroValidador.buscaPorIsbnOuLancaException(isbn)).thenReturn(entity);
		doNothing().when(livroRepository).delete(entity);
		
		livroServiceImp.deletaLivroPeloIsbn(isbn);
		
		verify(livroValidador).buscaPorIsbnOuLancaException(isbn);
		verify(livroRepository).delete(entity);
	}
	
	@DisplayName("DELETE - Deve lançar exception se o isbn for inexistente")
	@Test
	void deveLancarExceptionSeOIsbnForInexistente() {
		
		String isbn = "invalido-isbn";
		
		doThrow(new IsbnInexistenteException()).when(livroValidador).buscaPorIsbnOuLancaException(isbn);
		
		IsbnInexistenteException exception = assertThrows(IsbnInexistenteException.class, ()->{
			livroServiceImp.deletaLivroPeloIsbn(isbn);
		});
		
		assertEquals("O livro nao existe", exception.getMessage());
		
		verify(livroRepository, never()).delete(any());
	}
	
	@DisplayName("PUT - Deve atualizar o livro pelo isbn com sucesso.")
	@Test
	void deveAtualizarLivroPeloIsbnComSucesso() {
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		String isbn = "9780132350884";
		
		AtualizarLivroDto dtoEntrada = new AtualizarLivroDto("Clean Code", "Robert C. Martin", "Programação", dataFixa);
		
		Livro entity = new Livro();
		entity.setId(1L);
		entity.setTitulo("Clean Code");
		entity.setAutor("Robert C. Martin");
		entity.setIsbn(isbn);
		entity.setDataPublicacao(dataFixa);
		entity.setCategoria("Programação");
		
		LivroDto dtoEsperado = new LivroDto(1L, "Clean Code", "Robert C. Martin", isbn, dataFixa, "Programação");
		
		
		when(livroValidador.buscaPorIsbnOuLancaException(isbn)).thenReturn(entity);
		doNothing().when(livroMapper).atualizaDto(dtoEntrada, entity);
		when(livroRepository.save(entity)).thenReturn(entity);
		when(livroMapper.toDto(entity)).thenReturn(dtoEsperado);
		
		LivroDto resultado = livroServiceImp.atualizaLivroPeloIsBn(isbn, dtoEntrada);
		
		assertNotNull(resultado);
		assertEquals(dtoEsperado.id(), resultado.id());
		assertEquals(dtoEsperado.titulo(), resultado.titulo());
		assertEquals(dtoEsperado.autor(), resultado.autor());
		assertEquals(dtoEsperado.isbn(), resultado.isbn());
		assertEquals(dtoEsperado.categoria(), resultado.categoria());
		
		verify(livroValidador).buscaPorIsbnOuLancaException(isbn);
		verify(livroMapper).atualizaDto(dtoEntrada, entity);
		verify(livroRepository).save(entity);
		verify(livroMapper).toDto(entity);
	}
	
	@DisplayName("PUT - Deve lançar exception se o isbn nao existir")
	@Test
	void deveLancarExceptionSeOIsbnNaoExistir() {
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		String isbn = "9780132350884";
		
		AtualizarLivroDto dtoEntrada = new AtualizarLivroDto("Clean Code", "Robert C. Martin", "Programação", dataFixa);

		doThrow(new IsbnInexistenteException()).when(livroValidador).buscaPorIsbnOuLancaException(isbn);
		
		IsbnInexistenteException exception = assertThrows(IsbnInexistenteException.class, ()->{
			livroServiceImp.atualizaLivroPeloIsBn(isbn, dtoEntrada);
		});
		
		assertEquals("O livro nao existe", exception.getMessage());
		
		verify(livroMapper, never()).atualizaDto(any(), any());
		verify(livroRepository, never()).save(any());
		verify(livroMapper, never()).toDto(any());
		
	}
	
	@DisplayName("PUT - Deve lançar exception se o isbnda url for diferente do corpo.")
	@Test
	void deveLancarExceptionSeAIsbnUrlDiferenteDoCorpo() {
		
		LocalDate dataFixa = LocalDate.of(2025, 8, 7);
		String isbn = "9780132350884";
		
		AtualizarLivroDto dtoEntrada = new AtualizarLivroDto("Clean Code", "Robert C. Martin", "Programação", dataFixa);
		
		when(livroValidador.buscaPorIsbnOuLancaException(isbn)).thenThrow(new IsbnInexistenteException());
		
		IsbnInexistenteException exception = assertThrows(IsbnInexistenteException.class, ()->{
			livroServiceImp.atualizaLivroPeloIsBn(isbn, dtoEntrada);
		});
		
		assertEquals("O livro nao existe", exception.getMessage());
		
		verify(livroMapper, never()).atualizaDto(any(), any());
		verify(livroRepository, never()).save(any());
		verify(livroMapper, never()).toDto(any());		
	}
	
	
}
