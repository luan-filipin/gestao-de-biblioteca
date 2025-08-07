package com.api.biblioteca.mapper;

import org.mapstruct.Mapper;

import com.api.biblioteca.dto.response.LivroDto;
import com.api.biblioteca.entity.Livro;

@Mapper(componentModel = "spring")
public interface LivroMapper {

	LivroDto toDto(Livro entity);
	
	Livro toEntity(LivroDto dto);
}
