package com.api.biblioteca.service;

import com.api.biblioteca.dto.CriarUsuarioDto;
import com.api.biblioteca.dto.response.AtualizaUsuarioDto;
import com.api.biblioteca.dto.response.UsuarioDto;

public interface UsuarioService {

	void criaUsuario(CriarUsuarioDto dto);
	
	UsuarioDto procuraUsuarioPeloEmail(String email);
	
	void deletaUsuarioPeloEmail(String email);
	
	UsuarioDto atualizaUsuarioPeloEmail(String email, AtualizaUsuarioDto dto);
}
