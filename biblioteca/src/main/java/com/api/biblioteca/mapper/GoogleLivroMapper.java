package com.api.biblioteca.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.GoogleLivrosRespostaDto;
import com.api.biblioteca.util.GoogleLivroMapperHelper;

@Mapper(componentModel = "spring", uses = GoogleLivroMapperHelper.class)
public interface GoogleLivroMapper {

    @Mapping(target = "titulo", source = "volumeInfo.title")
    @Mapping(target = "autor", source = "volumeInfo.authors", qualifiedByName = "joinList")
    @Mapping(target = "categoria", source = "volumeInfo.categories", qualifiedByName = "joinList")
    @Mapping(target = "dataPublicacao", source = "volumeInfo.publishedDate", qualifiedByName = "stringToLocalDate")
    @Mapping(target = "isbn", source = "volumeInfo.industryIdentifiers", qualifiedByName = "extractIsbn")
    CriarLivroDto toCriarLivroDto(GoogleLivrosRespostaDto.Item item);

    default List<CriarLivroDto> responseDtoToCriarLivroDtoList(GoogleLivrosRespostaDto responseDto) {
        if (responseDto == null || responseDto.items() == null) {
            return List.of();
        }
        return responseDto.items().stream().map(this::toCriarLivroDto).toList();
    }
}
