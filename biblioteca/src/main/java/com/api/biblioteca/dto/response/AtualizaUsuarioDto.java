package com.api.biblioteca.dto.response;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record AtualizaUsuarioDto(
		@NotBlank(message = "O nome é obrigatorio!") 
		String nome, 
		
		@NotBlank(message = "O telefone é obrigatorio!") 
		@Pattern(regexp = "\\(?\\d{2}\\)?\\s?9?\\d{4}-?\\d{4}", message = "Telefone inválido. Formato esperado: (11)91234-5678")
		String telefone) {
}
