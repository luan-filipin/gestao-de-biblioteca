package com.api.biblioteca.exception;

public class EmailInexistenteException extends RuntimeException{
	
	private static final String EMAIL_INEXISTENTE = "O email não existe!";
	
	public EmailInexistenteException() {
		super(EMAIL_INEXISTENTE);
	}

}
