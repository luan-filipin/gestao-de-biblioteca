package com.api.biblioteca.dto;

import jakarta.validation.constraints.NotBlank;

public record CriarLivroDto(
		@NotBlank(message = "O titulo é obrigatorio!")
		String titulo,
		@NotBlank(message = "O autor é obrigatorio!")
		String autor,
		@NotBlank(message = "O isbn é obrigatorio!")
		String isbn,
		@NotBlank(message = "O categoria é obrigatorio!")
		String categoria) {
}
