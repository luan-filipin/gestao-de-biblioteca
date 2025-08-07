package com.api.biblioteca.dto.response;

import java.time.LocalDate;

public record LivroDto(
		Long id,
		String titulo,
		String autor,
		String isbn,
		LocalDate dataPublicacao,
		String categoria) {

}
