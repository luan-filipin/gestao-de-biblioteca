package com.api.biblioteca.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record CriarEmprestimoDto(
	    @NotNull(message = "O ID do livro é obrigatório.") Long livroId,
	    @NotNull(message = "O ID do usuário é obrigatório.") Long usuarioId,
	    @NotNull(message = "A data de devolução é obrigatória.") LocalDate dataDevolucao
	    ) {
}
