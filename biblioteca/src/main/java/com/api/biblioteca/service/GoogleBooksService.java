package com.api.biblioteca.service;

import java.util.List;

import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.LivroDto;

public interface GoogleBooksService {

	List<CriarLivroDto> buscarLivrosPorTitulo(String titulo);
	
	List<LivroDto> buscarESalvarLivros(String titulo);
}
