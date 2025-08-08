package com.api.biblioteca.exception;

public class IdEmprestimoNaoExisteException extends RuntimeException{
	
	private static final String ID_EMPRESTIMO_NAO_EXISTE = "O emprestimo não existe!";
	
	public IdEmprestimoNaoExisteException() {
		super(ID_EMPRESTIMO_NAO_EXISTE);
	}

}
