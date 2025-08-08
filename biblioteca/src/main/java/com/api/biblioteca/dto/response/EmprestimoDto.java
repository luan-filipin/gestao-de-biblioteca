package com.api.biblioteca.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record EmprestimoDto(
		Long id,
		String nomeUsuario,
		String tituloLivro,
		LocalDateTime dataEmprestimo,
		LocalDate dataDevolucao,
		boolean status) {

}
