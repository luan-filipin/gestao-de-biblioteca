package com.api.biblioteca.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.entity.Usuario;

public record EmprestimoDto(
		Long id,
		Usuario usuario,
		Livro livro,
		LocalDateTime dataEmprestimo,
		LocalDate dataDevolucao,
		boolean status) {

}
