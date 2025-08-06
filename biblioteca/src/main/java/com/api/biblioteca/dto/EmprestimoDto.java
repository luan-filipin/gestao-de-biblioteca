package com.api.biblioteca.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record EmprestimoDto(
		@NotBlank(message = "O usuarioId é obrigatorio!") String usuarioId,
		@NotBlank(message = "O livroId é obrigatorio!") String livroId,
		@NotNull(message = "A data de devolucao é obrigatoria!") LocalDate dataDevolucao) {

}
