package com.api.biblioteca.service;

import org.springframework.stereotype.Service;

import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.LivroDto;
import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.mapper.CriarLivroMapper;
import com.api.biblioteca.mapper.LivroMapper;
import com.api.biblioteca.repository.LivroRepository;
import com.api.biblioteca.service.validador.LivroValidador;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LivroServiceImp implements LivroService{
	
	private final LivroValidador livroValidador;
	private final LivroRepository livroRepository;
	private final CriarLivroMapper criarLivroMapper;
	private final LivroMapper livroMapper;

	@Override
	public LivroDto criarLivro(CriarLivroDto dto) {
		livroValidador.validaSeOLivroExiste(dto.isbn());
		Livro livroSalvo = livroRepository.save(criarLivroMapper.toEntity(dto));
		return livroMapper.toDto(livroSalvo);
	}

	@Override
	public LivroDto buscaLivroPeloIsbn(String isbn) {
		Livro livro = livroValidador.buscaPorIsbnOuLancaException(isbn);
		return livroMapper.toDto(livro);
	}


	
	

}
