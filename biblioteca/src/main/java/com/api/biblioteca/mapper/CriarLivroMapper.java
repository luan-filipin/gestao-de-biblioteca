package com.api.biblioteca.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.entity.Livro;

@Mapper(componentModel = "spring")
public interface CriarLivroMapper {

	CriarLivroDto toDto(Livro entity);
	
	Livro toEntity(CriarLivroDto dto);
	
	List<Livro> toListEntity(List<CriarLivroDto> list);
}
