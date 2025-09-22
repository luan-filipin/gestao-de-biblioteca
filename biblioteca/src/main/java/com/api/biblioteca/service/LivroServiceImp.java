package com.api.biblioteca.service;

import org.springframework.stereotype.Service;

import com.api.biblioteca.dto.AtualizarLivroDto;
import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.LivroDto;
import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.mapper.CriarLivroMapper;
import com.api.biblioteca.mapper.LivroMapper;
import com.api.biblioteca.repository.LivroRepository;
import com.api.biblioteca.service.validador.LivroValidador;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LivroServiceImp implements LivroService{
	
	private final LivroValidador livroValidador;
	private final LivroRepository livroRepository;
	private final CriarLivroMapper criarLivroMapper;
	private final LivroMapper livroMapper;

	@Transactional
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

	@Transactional
	@Override
	public void deletaLivroPeloIsbn(String isbn) {
		Livro livro = livroValidador.buscaPorIsbnOuLancaException(isbn);
		livroRepository.delete(livro);
	}

	@Transactional
	@Override
	public LivroDto atualizaLivroPeloIsBn(String isbn, AtualizarLivroDto dto) {
		Livro livro = livroValidador.buscaPorIsbnOuLancaException(isbn);
		livroMapper.atualizaDto(dto, livro);
		Livro livroSalvo = livroRepository.save(livro);
		return livroMapper.toDto(livroSalvo);
	}
}
