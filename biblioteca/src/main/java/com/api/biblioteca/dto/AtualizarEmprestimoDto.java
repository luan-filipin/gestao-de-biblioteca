package com.api.biblioteca.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;

public record AtualizarEmprestimoDto(
	    @NotNull(message = "A data de devolução é obrigatória.") LocalDate dataDevolucao,
	    @NotNull(message = "O status do empréstimo é obrigatório.") Boolean status) {

}
