package com.api.biblioteca.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import com.api.biblioteca.dto.AtualizarLivroDto;
import com.api.biblioteca.dto.response.LivroDto;
import com.api.biblioteca.entity.Livro;

@Mapper(componentModel = "spring")
public interface LivroMapper {

	LivroDto toDto(Livro entity);
	
	Livro toEntity(LivroDto dto);
	
	void atualizaDto(AtualizarLivroDto dto, @MappingTarget Livro entity);
	
	List<LivroDto> toListDto(List<Livro> entity);
}
