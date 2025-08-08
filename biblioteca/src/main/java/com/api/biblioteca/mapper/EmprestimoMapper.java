package com.api.biblioteca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.api.biblioteca.dto.CriarEmprestimoDto;
import com.api.biblioteca.dto.response.EmprestimoDto;
import com.api.biblioteca.entity.Emprestimo;

@Mapper(componentModel = "spring")
public interface EmprestimoMapper {

	@Mapping(target = "usuario.id", source = "usuarioId")
	@Mapping(target = "livro.id", source = "livroId")
	Emprestimo toEntity(CriarEmprestimoDto dto);
	
	EmprestimoDto toDto(Emprestimo entity);
}
