package com.api.biblioteca.exception;

public class IsbnJaExisteException extends RuntimeException{
	
	private static final String ISBN_JA_EXISTE = "O Livro ja existe";
	
	public IsbnJaExisteException() {
		super(ISBN_JA_EXISTE);
	}

}
