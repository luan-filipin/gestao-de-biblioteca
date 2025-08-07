package com.api.biblioteca.exception;

public class LivroJaExisteException extends RuntimeException{
	
	private static final String LIVRO_JA_EXISTE = "O Livro ja existe";
	
	public LivroJaExisteException() {
		super(LIVRO_JA_EXISTE);
	}

}
