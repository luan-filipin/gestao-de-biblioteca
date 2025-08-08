package com.api.biblioteca.controller;

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
import com.api.biblioteca.service.LivroService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/livros")
public class LivroController {

	private final LivroService livroService;

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
}
