package com.api.biblioteca.service.validador;

import org.springframework.stereotype.Component;

import com.api.biblioteca.entity.Emprestimo;
import com.api.biblioteca.exception.IdEmprestimoNaoExisteException;
import com.api.biblioteca.exception.LivroIndisponivelException;
import com.api.biblioteca.repository.EmprestimoRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class EmprestimoValidador {

	private final EmprestimoRepository emprestimoRepository;

	public Emprestimo buscaIdOuLancaException(Long id) {
		return emprestimoRepository.findById(id).orElseThrow(IdEmprestimoNaoExisteException::new);
	}
	
	public void validaSeLivroEstaEmprestado(Long id) {
		if(emprestimoRepository.existsByLivroIdAndStatusTrue(id)) {
			throw new LivroIndisponivelException();
		}
	}
}
