package com.api.biblioteca.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.api.biblioteca.dto.response.LivroDto;
import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.mapper.LivroMapper;
import com.api.biblioteca.repository.LivroRepository;
import com.api.biblioteca.service.validador.LivroValidador;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RecomendaServiceImp implements RecomendaService{

	private final LivroRepository livroRepository;
	private final LivroMapper livroMapper;
	private final LivroValidador livroValidador;
	
	@Override
	public List<LivroDto> recomendarLivrosPorUsuario(String email) {
		List<Livro> livros = livroRepository.recomendarLivrosPorCategoriaQueUsuarioAindaNaoLeu(email);
		livroValidador.validaSeAListaEstaVazia(livros);
		return livroMapper.toListDto(livros);
	}

}
