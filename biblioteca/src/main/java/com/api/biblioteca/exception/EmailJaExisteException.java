package com.api.biblioteca.exception;

public class EmailJaExisteException extends RuntimeException{
	
	private static final String EMAIL_JA_EXISTE = "O email ja existe!";
	
	public EmailJaExisteException() {
		super(EMAIL_JA_EXISTE);
	}

}
