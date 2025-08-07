package com.api.biblioteca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.api.biblioteca.dto.response.AtualizaUsuarioDto;
import com.api.biblioteca.dto.response.UsuarioDto;
import com.api.biblioteca.entity.Usuario;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

	UsuarioDto toUsuarioDto(Usuario entity);
	
	Usuario toUsuarioEntity(UsuarioDto dto);
	
	void atualizaDto(AtualizaUsuarioDto dto, @MappingTarget Usuario entity);
	
}
