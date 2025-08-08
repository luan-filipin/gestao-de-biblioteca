package com.api.biblioteca.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AtualizarLivroDto(
		@NotBlank(message = "O campo titulo é obrigatorio!")
		String titulo,
		@NotBlank(message = "O campo autor é obrigatorio!")
		String autor,
		@NotBlank(message = "O campo categoria é obrigatorio!")
		String categoria,
		@NotNull(message = "O campo dataPublicacao é obrigatorio!")
		LocalDate dataPublicacao) {

}
