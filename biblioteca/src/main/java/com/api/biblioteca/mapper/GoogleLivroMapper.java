package com.api.biblioteca.mapper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import com.api.biblioteca.dto.CriarLivroDto;
import com.api.biblioteca.dto.response.GoogleLivrosRespostaDto;

@Mapper(componentModel = "spring")
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
		return responseDto.items().stream().map(this::toCriarLivroDto).collect(Collectors.toList());
	}

	@Named("joinList")
	static String joinList(List<String> list) {
		if (list == null || list.isEmpty()) {
			return "";
		}
		return String.join(", ", list);
	}

	@Named("extractIsbn")
	static String extractIsbn(List<GoogleLivrosRespostaDto.IndustryIdentifier> identifiers) {
		if (identifiers == null || identifiers.isEmpty()) {
			return "";
		}
		return identifiers.stream().filter(i -> "ISBN_13".equalsIgnoreCase(i.type())).findFirst()
				.map(GoogleLivrosRespostaDto.IndustryIdentifier::identifier)
				.orElseGet(() -> identifiers.get(0).identifier());
	}

	@Named("stringToLocalDate")
	static LocalDate stringToLocalDate(String dateString) {
		if (dateString == null || dateString.isEmpty()) {
			return null;
		}

		try {
			if (dateString.length() == 4) {
				return LocalDate.parse(dateString + "-01-01", DateTimeFormatter.ISO_LOCAL_DATE);
			} else {
				return LocalDate.parse(dateString, DateTimeFormatter.ISO_LOCAL_DATE);
			}
		} catch (Exception e) {
			try {
				return LocalDate.parse(dateString.substring(0, 4) + "-01-01", DateTimeFormatter.ISO_LOCAL_DATE);
			} catch (Exception ex) {
				return null;
			}
		}
	}

}
