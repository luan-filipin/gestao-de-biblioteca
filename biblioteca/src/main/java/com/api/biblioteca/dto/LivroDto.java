package com.api.biblioteca.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record LivroDto(
		@NotBlank(message = "O titulo é obrigatorio!") String titulo,
		@NotBlank(message = "O auto é obrigatorio!") String autor,
		@NotBlank(message = "O isbn é obrigatorio!") String isbn,
		@NotNull(message = "A data da publicacao é obrigatoria") LocalDate dataPublicacao,
		@NotBlank(message = "A categoria é obrigatoria!") String categoria) {
}
