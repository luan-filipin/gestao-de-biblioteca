package com.api.biblioteca.service.validador;

import org.springframework.stereotype.Component;

import com.api.biblioteca.exception.LivroJaExisteException;
import com.api.biblioteca.repository.LivroRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class LivroValidador {

	private final LivroRepository livroRepository;
	
	public void validaSeOLivroExiste(String isbn) {
		if(livroRepository.existsByIsbn(isbn)) {
			throw new LivroJaExisteException();
		}
	}
	
}
