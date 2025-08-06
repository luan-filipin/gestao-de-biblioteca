package com.api.biblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UsuarioDto(
		@NotBlank(message = "O nome é obrigatorio!") String nome,
		@Email(message = "Email invalido!") String email,
		@NotBlank(message = "O telefone é obrigatorio!") String telefone) {

}
