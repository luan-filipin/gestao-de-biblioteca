package com.api.biblioteca.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CriarUsuarioDto(
		@NotBlank(message = "O nome é obrigatorio!") 
		String nome,
		
		@NotBlank(message = "O email é obrigatorio!")
		@Email(message = "Email invalido!") 
		String email,
		
		@NotBlank(message = "O telefone é obrigatorio!") 
		@Pattern(regexp = "\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}",message = "Telefone inválido. Formato esperado: (11)91234-5678")
		String telefone) {

}
