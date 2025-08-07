package com.api.biblioteca.dto;

import java.time.LocalDate;

public record AtualizarLivroDto(
		String titulo,
		String autor,
		String isbn,
		String categoria,
		LocalDate dataPublicacao) {

}
