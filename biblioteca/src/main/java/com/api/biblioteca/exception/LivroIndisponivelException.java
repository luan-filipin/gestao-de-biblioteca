package com.api.biblioteca.exception;

public class LivroIndisponivelException extends RuntimeException{
	
	private static final String LIVRO_INDISPONIVEL = "O livro já está emprestado no momento.";
	
	public LivroIndisponivelException() {
		super(LIVRO_INDISPONIVEL);
	}

}
