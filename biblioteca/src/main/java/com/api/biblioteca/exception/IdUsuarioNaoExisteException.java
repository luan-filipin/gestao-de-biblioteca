package com.api.biblioteca.exception;

public class IdUsuarioNaoExisteException extends RuntimeException{
	
	private static final String ID_USUARIO_NAO_EXISTE= "O id do usuario não existe";
	
	public IdUsuarioNaoExisteException() {
		super(ID_USUARIO_NAO_EXISTE);
	}

}
