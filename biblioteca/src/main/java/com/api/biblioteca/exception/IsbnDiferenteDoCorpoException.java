package com.api.biblioteca.exception;

public class IsbnDiferenteDoCorpoException extends RuntimeException{
	
	private static final String ISBN_DIFERENTE_DO_CORPO = "O isbn no corpo da requisição não pode ser diferente do isbn da URL.";
	
	public IsbnDiferenteDoCorpoException() {
		super(ISBN_DIFERENTE_DO_CORPO);
	}

}
