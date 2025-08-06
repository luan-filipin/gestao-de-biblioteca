package com.api.biblioteca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.api.biblioteca.dto.EmprestimoDto;
import com.api.biblioteca.entity.Emprestimo;

@Mapper(componentModel = "spring")
public interface EmprestimoMapper {

    @Mapping(source = "usuario.id", target = "usuarioId")
    @Mapping(source = "livro.id", target = "livroId")
	EmprestimoDto toDto(Emprestimo entity);
	
    @Mapping(target = "usuario.id", source = "usuarioId")
    @Mapping(target = "livro.id", source = "livroId")
	Emprestimo toEntity(EmprestimoDto dto);
}
