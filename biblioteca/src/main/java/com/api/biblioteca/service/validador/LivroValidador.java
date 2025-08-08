package com.api.biblioteca.service.validador;

import java.util.List;

import org.springframework.stereotype.Component;

import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.exception.IdLivroNaoExisteException;
import com.api.biblioteca.exception.IsbnInexistenteException;
import com.api.biblioteca.exception.IsbnJaExisteException;
import com.api.biblioteca.exception.NenhumLivroEncontradoParaRecomendacaoException;
import com.api.biblioteca.repository.LivroRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LivroValidador {

	private final LivroRepository livroRepository;
	
	public void validaSeOLivroExiste(String isbn) {
		if(livroRepository.existsByIsbn(isbn)) {
			throw new IsbnJaExisteException();
		}
	}
	
	public Livro buscaPorIsbnOuLancaException(String isbn) {
		return livroRepository.findByIsbn(isbn).orElseThrow(IsbnInexistenteException::new);
	}
	
	public Livro validaSeIdDoLivroExiste(Long id) {
		return livroRepository.findById(id).orElseThrow(IdLivroNaoExisteException::new);
	}
	
	public void validaSeAListaEstaVazia(List<Livro> livros) {
	    if (livros.isEmpty()) {
	        throw new NenhumLivroEncontradoParaRecomendacaoException();
	    }
	}
}
