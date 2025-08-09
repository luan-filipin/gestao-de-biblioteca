package com.api.biblioteca.util;

import java.time.LocalDate;
import java.util.List;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import com.api.biblioteca.dto.response.GoogleLivrosRespostaDto;

@Component
public class GoogleLivroMapperHelper {
	
	//Para caso tenha mais de um autor ou categoria, ele junta tudo em uma linha para adicionar no campo.
	@Named("joinList")
	public String joinList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return String.join(", ", list);
    }
	
	/* Pode vir mais de um ISBN na lista, então o método procura primeiro pelo ISBN-13,
	 * que é o padrão mais moderno e utilizado. 
	 * Se não encontrar, retorna o primeiro ISBN disponível na lista.
	 */
	@Named("extractIsbn")
    public String extractIsbn(List<GoogleLivrosRespostaDto.IndustryIdentifier> identifiers) {
        if (identifiers == null || identifiers.isEmpty()) return "";
        return identifiers.stream()
                .filter(i -> "ISBN_13".equalsIgnoreCase(i.type()))
                .findFirst()
                .map(GoogleLivrosRespostaDto.IndustryIdentifier::identifier)
                .orElseGet(() -> identifiers.get(0).identifier());
    }
	
	//Tenta converter o tipo da data retornado para o tipo LocalDate
	@Named("stringToLocalDate")
    public LocalDate stringToLocalDate(String dateString) {
        if (dateString == null || dateString.isEmpty()) return null;
        try {
        	// Se a string tem só 4 caracteres, assume que é só o ano
            // Acrescenta "-01-01" para formar uma data completa no formato ISO (ano-mês-dia)
            if (dateString.length() == 4) {
                return LocalDate.parse(dateString + "-01-01");
            }
            return LocalDate.parse(dateString);//Tenta converter
        } catch (Exception e) {
            try {
                // Se a conversão falhar, tenta pegar só os 4 primeiros caracteres (ano)
                return LocalDate.parse(dateString.substring(0, 4) + "-01-01");
            } catch (Exception ex) {
                return null;//Se nao conseguir o campo retorna Null
            }
        }
    }
}
