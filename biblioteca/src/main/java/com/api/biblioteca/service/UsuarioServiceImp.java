package com.api.biblioteca.service;

import org.springframework.stereotype.Service;

import com.api.biblioteca.dto.CriarUsuarioDto;
import com.api.biblioteca.dto.response.AtualizaUsuarioDto;
import com.api.biblioteca.dto.response.UsuarioDto;
import com.api.biblioteca.entity.Usuario;
import com.api.biblioteca.mapper.CriarUsuarioMapper;
import com.api.biblioteca.mapper.UsuarioMapper;
import com.api.biblioteca.repository.UsuarioRepository;
import com.api.biblioteca.service.validador.UsuarioValidador;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UsuarioServiceImp implements UsuarioService{

	private final UsuarioValidador usuarioValidador;
	private final CriarUsuarioMapper criarUsuarioMapper;
	private final UsuarioMapper usuarioMapper;
	private final UsuarioRepository usuarioRepository;
	
	@Override
	public UsuarioDto criarUsuario(CriarUsuarioDto dto) {
		usuarioValidador.validarEmailNaoCadastrado(dto.email());
		Usuario usuarioSalvo = usuarioRepository.save(criarUsuarioMapper.toEntity(dto));
		return usuarioMapper.toUsuarioDto(usuarioSalvo);
	}

	@Override
	public UsuarioDto buscarUsuarioPorEmail(String email) {
		Usuario usuario = usuarioValidador.buscarPorEmailOuLancarEmailInexistente(email);
		return usuarioMapper.toUsuarioDto(usuario);
	}

	@Override
	public void deletaUsuarioPeloEmail(String email) {
		Usuario usuario = usuarioValidador.buscarPorEmailOuLancarEmailInexistente(email);
		usuarioRepository.delete(usuario);		
	}

	@Override
	public UsuarioDto atualizaUsuarioPeloEmail(String email, AtualizaUsuarioDto dto) {
		Usuario usuario = usuarioValidador.buscarPorEmailOuLancarEmailInexistente(email);
		usuarioMapper.atualizaDto(dto, usuario);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		return usuarioMapper.toUsuarioDto(usuarioSalvo);
	}
	
	

}
