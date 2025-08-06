package com.api.biblioteca.mapper;

import org.mapstruct.Mapper;

import com.api.biblioteca.dto.CriarUsuarioDto;
import com.api.biblioteca.entity.Usuario;

@Mapper(componentModel = "spring")
public interface CriarUsuarioMapper {

	CriarUsuarioDto toDto(Usuario entity);
	
	Usuario toEntity(CriarUsuarioDto dto);
}
