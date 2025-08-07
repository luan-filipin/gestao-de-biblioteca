package com.api.biblioteca.mapper;

import org.mapstruct.Mapper;

import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.entity.Livro;

@Mapper(componentModel = "spring")
public interface CriarLivroMapper {

	CriarLivroDto toDto(Livro entity);
	
	Livro toEntity(CriarLivroDto dto);
}
