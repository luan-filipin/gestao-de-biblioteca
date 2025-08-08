package com.api.biblioteca.dto;

import java.time.LocalDate;

public record EmprestimoAtualizarDto(
		boolean status,
		LocalDate dataDevolucao) {

}
