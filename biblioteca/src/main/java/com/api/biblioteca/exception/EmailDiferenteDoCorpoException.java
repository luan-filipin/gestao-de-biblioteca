package com.api.biblioteca.exception;

public class EmailDiferenteDoCorpoException extends RuntimeException{
	
	private static final String EMAIL_DIFERENTE_DO_CORPO = "O email no corpo da requisição não pode ser diferente do email da URL.";

	public EmailDiferenteDoCorpoException() {
		super(EMAIL_DIFERENTE_DO_CORPO);
	}
}
