package com.api.biblioteca.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
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

@ExtendWith(MockitoExtension.class)
class GoogleBooksServiceTest {
	
    @Mock
    private GoogleLivroMapper googleLivroMapper;

    @Mock
    private CriarLivroMapper criarLivroMapper;

    @Mock
    private LivroRepository livroRepository;

    @Mock
    private LivroMapper livroMapper;

    @Mock
    private LivroValidador livroValidador;
    
    @Spy
    @InjectMocks
    private GoogleBooksServiceImp service;
    
    @Mock
    private RestTemplate restTemplate;
 
    
	@DisplayName("GET - Deve busca uma lista de livros na API do Google Books com sucesso.")
	@Test
	void buscarLivrosPorTituloComSucesso() {
	    String titulo = "Java";
	    String urlEsperada = "https://www.googleapis.com/books/v1/volumes?q=intitle:Java";

	    GoogleLivrosRespostaDto fakeResposta = Mockito.mock(GoogleLivrosRespostaDto.class);
	    List<CriarLivroDto> fakeCriarLivroDtos = List.of(
	        new CriarLivroDto("Java 101", "Autor X", "123", "Programação", LocalDate.now())
	    );

	    when(restTemplate.getForObject(urlEsperada, GoogleLivrosRespostaDto.class))
	        .thenReturn(fakeResposta);
	    when(googleLivroMapper.responseDtoToCriarLivroDtoList(fakeResposta))
	        .thenReturn(fakeCriarLivroDtos);

	    List<CriarLivroDto> resultado = service.buscarLivrosPorTitulo(titulo);

	    assertNotNull(resultado);
	    assertEquals(1, resultado.size());
	    verify(restTemplate).getForObject(urlEsperada, GoogleLivrosRespostaDto.class);
	    verify(googleLivroMapper).responseDtoToCriarLivroDtoList(fakeResposta);
	}
	
	@DisplayName("GET - Deve retornar uma lista Null")
	@Test
	void buscarLivrosPorTituloApiRetornaNull() {
	    String titulo = "Java";
	    String urlEsperada = "https://www.googleapis.com/books/v1/volumes?q=intitle:Java";

	    when(restTemplate.getForObject(urlEsperada, GoogleLivrosRespostaDto.class))
	        .thenReturn(null);

	    // Mapper deve lidar com null e retornar lista vazia
	    when(googleLivroMapper.responseDtoToCriarLivroDtoList(null))
	        .thenReturn(List.of());

	    List<CriarLivroDto> resultado = service.buscarLivrosPorTitulo(titulo);

	    assertNotNull(resultado);
	    assertTrue(resultado.isEmpty());
	}
	
	@DisplayName("POST - Deve buscar e salva livros no banco com sucesso.")
	@Test
	void buscarESalvarLivrosComSucesso() {
	    String titulo = "Java";

	    List<CriarLivroDto> criarLivroDtos = List.of(
	        new CriarLivroDto("Java 101", "Autor X", "123", "Programação", LocalDate.now())
	    );

	    List<CriarLivroDto> criarLivroDtosValidos = criarLivroDtos;

	    List<Livro> livros = List.of(new Livro());
	    List<Livro> livrosSalvos = List.of(new Livro());

	    List<LivroDto> livroDtos = List.of(new LivroDto(1L, "Java 101", "Autor X", "123", LocalDate.now(), "Programação"));

	    doReturn(criarLivroDtos).when(service).buscarLivrosPorTitulo(titulo);
	    when(livroValidador.filtrarValidos(criarLivroDtos)).thenReturn(criarLivroDtosValidos);
	    when(criarLivroMapper.toListEntity(criarLivroDtosValidos)).thenReturn(livros);
	    when(livroRepository.saveAll(livros)).thenReturn(livrosSalvos);
	    when(livroMapper.toListDto(livrosSalvos)).thenReturn(livroDtos);

	    List<LivroDto> resultado = service.buscarESalvarLivros(titulo);

	    assertNotNull(resultado);
	    assertEquals(1, resultado.size());

	    verify(service).buscarLivrosPorTitulo(titulo);
	    verify(livroValidador).filtrarValidos(criarLivroDtos);
	    verify(criarLivroMapper).toListEntity(criarLivroDtosValidos);
	    verify(livroRepository).saveAll(livros);
	    verify(livroMapper).toListDto(livrosSalvos);
	}
	
	@DisplayName("POST - Deve remover o livro se algum campo estiver vazio.")
	@Test
	void buscarESalvarLivros_listaVaziaAposValidacao() {
	    String titulo = "Java";

	    List<CriarLivroDto> criarLivroDtos = List.of(
	        new CriarLivroDto("Livro Inválido", "", "", "", null)
	    );

	    List<CriarLivroDto> criarLivroDtosValidos = List.of(); 

	    doReturn(criarLivroDtos).when(service).buscarLivrosPorTitulo(titulo);
	    when(livroValidador.filtrarValidos(criarLivroDtos)).thenReturn(criarLivroDtosValidos);

	    List<LivroDto> resultado = service.buscarESalvarLivros(titulo);

	    assertNotNull(resultado);
	    assertTrue(resultado.isEmpty());

	    verify(service).buscarLivrosPorTitulo(titulo);
	    verify(livroValidador).filtrarValidos(criarLivroDtos);
	}



}
