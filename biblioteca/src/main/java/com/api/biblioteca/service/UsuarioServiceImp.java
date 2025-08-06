package com.api.biblioteca.service;


import org.springframework.stereotype.Service;

import com.api.biblioteca.dto.CriarUsuarioDto;
import com.api.biblioteca.dto.response.AtualizaUsuarioDto;
import com.api.biblioteca.dto.response.UsuarioDto;
import com.api.biblioteca.entity.Usuario;
import com.api.biblioteca.exception.EmailInexistenteException;
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
	public void criaUsuario(CriarUsuarioDto dto) {
		usuarioValidador.validarEmailNaoCadastrado(dto.email());
		usuarioRepository.save(criarUsuarioMapper.toEntity(dto));
	}

	@Override
	public UsuarioDto procuraUsuarioPeloEmail(String email) {
		Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(EmailInexistenteException::new);
		return usuarioMapper.toDto(usuario);
	}

	@Override
	public void deletaUsuarioPeloEmail(String email) {
		Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(EmailInexistenteException::new);
		usuarioRepository.delete(usuario);		
	}

	@Override
	public UsuarioDto atualizaUsuarioPeloEmail(String email, AtualizaUsuarioDto dto) {
		Usuario usuario = usuarioRepository.findByEmail(email).orElseThrow(EmailInexistenteException::new);
		usuarioValidador.validaEmailDaUrlDiferenteDoCorpo(email, dto.email());
		usuarioMapper.atualizaDto(dto, usuario);
		Usuario usuarioSalvo = usuarioRepository.save(usuario);
		return usuarioMapper.toDto(usuarioSalvo);
	}
	
	

}
