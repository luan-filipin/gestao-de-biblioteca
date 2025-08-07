package com.api.biblioteca.exception;

public class LivroInexistenteException extends RuntimeException{
	
	private static final String LIVRO_INEXISTENTE = "O livro nao existe";
	
	public LivroInexistenteException() {
		super(LIVRO_INEXISTENTE);
	}

}
