package com.api.biblioteca.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.LivroDto;
import com.api.biblioteca.service.LivroService;

import lombok.RequiredArgsConstructor;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/livros")
public class LivroController {

	private LivroService livroService;
	
	@PostMapping
	public ResponseEntity<LivroDto> criarLivro(@RequestBody CriarLivroDto dto){
		LivroDto livro = livroService.criarLivro(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(livro);
	}
}
