package com.api.biblioteca.exception;

public class NenhumLivroEncontradoParaRecomendacaoException extends RuntimeException{
	
	private static final String NENHUM_LIVRO_ENTRONCADO = "Nenhum livro encontrado para recomendação.";
	
	public NenhumLivroEncontradoParaRecomendacaoException() {
		super(NENHUM_LIVRO_ENTRONCADO);
	}

}
