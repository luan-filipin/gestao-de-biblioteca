package com.api.biblioteca.service;

import com.api.biblioteca.dto.AtualizarLivroDto;
import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.LivroDto;

public interface LivroService {
	
	LivroDto criarLivro(CriarLivroDto dto);

	LivroDto buscaLivroPeloIsbn(String isbn);
	
	void deletaLivroPeloIsbn(String isbn);
	
	LivroDto atualizaLivroPeloIsBn(String isbn, AtualizarLivroDto dto);
	
}
