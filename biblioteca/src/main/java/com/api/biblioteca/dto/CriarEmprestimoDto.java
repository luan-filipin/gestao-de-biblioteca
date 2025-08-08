package com.api.biblioteca.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;

public record CriarEmprestimoDto(
	    @NotNull(message = "O ID do livro é obrigatório.") 
	    Long livroId,
	    
	    @NotNull(message = "O ID do usuário é obrigatório.") 
	    Long usuarioId,
	    
	    @FutureOrPresent(message = "A data não pode ser no passado.")
	    @NotNull(message = "A data de devolução é obrigatória.")
	    LocalDate dataDevolucao
	    ) {
}
