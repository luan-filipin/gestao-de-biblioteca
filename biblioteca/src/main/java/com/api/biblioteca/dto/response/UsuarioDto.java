package com.api.biblioteca.dto.response;

import java.time.LocalDateTime;

public record UsuarioDto(
		Long id,
		String nome,
		String email,
		LocalDateTime dataCadastro,
		String telefone) {
}
