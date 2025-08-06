package com.api.biblioteca.service.validador;

import org.springframework.stereotype.Component;

import com.api.biblioteca.exception.EmailDiferenteDoCorpoException;
import com.api.biblioteca.exception.EmailJaExisteException;
import com.api.biblioteca.repository.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class UsuarioValidador {

	private final UsuarioRepository usuarioRepository;
	
	public void validarEmailNaoCadastrado(String email) {
		if(usuarioRepository.existsByEmail(email)) {
			throw new EmailJaExisteException();
		}
	}
	
	public void validaEmailDaUrlDiferenteDoCorpo(String emailUrl, String emailCorpo) {
		if(!emailUrl.equals(emailCorpo)) {
			throw new EmailDiferenteDoCorpoException();
		}
	}
}
