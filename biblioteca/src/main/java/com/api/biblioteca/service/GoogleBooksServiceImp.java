package com.api.biblioteca.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.GoogleLivrosRespostaDto;
import com.api.biblioteca.dto.response.LivroDto;
import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.mapper.CriarLivroMapper;
import com.api.biblioteca.mapper.GoogleLivroMapper;
import com.api.biblioteca.mapper.LivroMapper;
import com.api.biblioteca.repository.LivroRepository;
import com.api.biblioteca.service.validador.LivroValidador;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class GoogleBooksServiceImp implements GoogleBooksService {

	private final RestTemplate restTemplate = new RestTemplate();
	private static final String GOOGLE_BOOKS_API_URL = "https://www.googleapis.com/books/v1/volumes?q=intitle:%s";
	private final GoogleLivroMapper googleLivroMapper;
	private final CriarLivroMapper criarLivroMapper;
	private final LivroRepository livroRepository;
	private final LivroMapper livroMapper;
	private final LivroValidador livroValidador;

	@Override
	public List<CriarLivroDto> buscarLivrosPorTitulo(String titulo) {
		String url = String.format(GOOGLE_BOOKS_API_URL, titulo);
		GoogleLivrosRespostaDto respostaDto = restTemplate.getForObject(url, GoogleLivrosRespostaDto.class);
		return googleLivroMapper.responseDtoToCriarLivroDtoList(respostaDto);
	}

	@Override
	public List<LivroDto> buscarESalvarLivros(String titulo) {
		List<CriarLivroDto> criarLivroDtos = buscarLivrosPorTitulo(titulo);
		List<CriarLivroDto> criarLivroDtosValidos = livroValidador.filtrarValidos(criarLivroDtos);
		List<Livro> livros = criarLivroMapper.toListEntity(criarLivroDtosValidos);
		List<Livro> livrosSalvos = livroRepository.saveAll(livros);
		return livroMapper.toListDto(livrosSalvos);
	}

}
