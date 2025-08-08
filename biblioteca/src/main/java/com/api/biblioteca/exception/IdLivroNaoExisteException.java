package com.api.biblioteca.exception;

public class IdLivroNaoExisteException extends RuntimeException{
	
	private static final String ID_LIVRO_NAO_EXISTE = "O id do livro nao existe!";
	
	public IdLivroNaoExisteException() {
		super(ID_LIVRO_NAO_EXISTE);
	}

}
