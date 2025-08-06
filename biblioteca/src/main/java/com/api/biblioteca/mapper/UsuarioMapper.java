package com.api.biblioteca.mapper;

import org.mapstruct.Mapper;

import com.api.biblioteca.dto.UsuarioDto;
import com.api.biblioteca.entity.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

	UsuarioDto toDto(Usuario entity);
	
	Usuario toEntity(UsuarioDto dto);
}
