package com.api.biblioteca.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.biblioteca.dto.AtualizarLivroDto;
import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.LivroDto;
import com.api.biblioteca.service.GoogleBooksService;
import com.api.biblioteca.service.LivroService;
import com.api.biblioteca.service.RecomendaService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/livros")
public class LivroController {

	private final LivroService livroService;
	private final RecomendaService recomendaService;
	private final GoogleBooksService googleBooksService;

	@PostMapping
	public ResponseEntity<LivroDto> criarLivro(@RequestBody @Valid CriarLivroDto dto) {
		LivroDto livro = livroService.criarLivro(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(livro);
	}

	@GetMapping
	public ResponseEntity<LivroDto> buscarLivroPeloIsbn(
			@RequestParam @NotBlank(message = "O isbn é obrigatorio!") String isbn) {
		LivroDto livro = livroService.buscaLivroPeloIsbn(isbn);
		return ResponseEntity.ok(livro);
	}

	@DeleteMapping
	public ResponseEntity<Void> deletarLivroPeloIsbn(
			@RequestParam @NotBlank(message = "O isbn é obrigatorio!") String isbn) {
		livroService.deletaLivroPeloIsbn(isbn);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

	@PutMapping
	public ResponseEntity<LivroDto> atualizaLivroPeloIsbn(
			@RequestParam @NotBlank(message = "O isbn é obrigatorio!") String isbn,
			@RequestBody @Valid AtualizarLivroDto dto) {
		LivroDto livro = livroService.atualizaLivroPeloIsBn(isbn, dto);
		return ResponseEntity.ok(livro);
	}

	@GetMapping("/recomendacao")
	public ResponseEntity<List<LivroDto>> recomendarLivrosPorUsuario(
			@RequestParam @NotBlank(message = "O email é obrigatorio!") @Email(message = "Email invalido!") String email) {
		List<LivroDto> recomendacoes = recomendaService.recomendarLivrosPorUsuario(email);
		return ResponseEntity.ok(recomendacoes);
	}
	
	@GetMapping("/google/buscar")
	public ResponseEntity<List<CriarLivroDto>> buscarLivrosPorTitulo(@RequestParam String titulo) {
	    List<CriarLivroDto> livros = googleBooksService.buscarLivrosPorTitulo(titulo);
	    return ResponseEntity.ok(livros);
	}

	@PostMapping("/google/salvar")
	public ResponseEntity<List<LivroDto>> buscarESalvarLivros(@RequestParam String titulo) {
	    List<LivroDto> livrosSalvos = googleBooksService.buscarESalvarLivros(titulo);
	    return ResponseEntity.status(HttpStatus.CREATED).body(livrosSalvos);
	}
}
