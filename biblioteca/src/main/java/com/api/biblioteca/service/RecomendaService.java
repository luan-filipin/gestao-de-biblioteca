package com.api.biblioteca.service;

import java.util.List;

import com.api.biblioteca.dto.response.LivroDto;

public interface RecomendaService {

	List<LivroDto> recomendarLivrosPorUsuario(String email);
}
