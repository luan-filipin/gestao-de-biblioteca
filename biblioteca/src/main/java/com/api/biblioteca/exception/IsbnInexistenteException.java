package com.api.biblioteca.exception;

public class IsbnInexistenteException extends RuntimeException{
	
	private static final String ISBN_INEXISTENTE = "O livro nao existe";
	
	public IsbnInexistenteException() {
		super(ISBN_INEXISTENTE);
	}

}
