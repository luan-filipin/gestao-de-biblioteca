package com.api.biblioteca.service;

import org.springframework.stereotype.Service;

import com.api.biblioteca.dto.AtualizarEmprestimoDto;
import com.api.biblioteca.dto.CriarEmprestimoDto;
import com.api.biblioteca.dto.response.AtualizarEmprestimoRespostaDto;
import com.api.biblioteca.dto.response.EmprestimoDto;
import com.api.biblioteca.entity.Emprestimo;
import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.entity.Usuario;
import com.api.biblioteca.mapper.EmprestimoMapper;
import com.api.biblioteca.repository.EmprestimoRepository;
import com.api.biblioteca.service.validador.EmprestimoValidador;
import com.api.biblioteca.service.validador.LivroValidador;
import com.api.biblioteca.service.validador.UsuarioValidador;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class EmprestimoServiceImp implements EmprestimoService {

	private final EmprestimoRepository emprestimoRepository;
	private final UsuarioValidador usuarioValidador;
	private final LivroValidador livroValidador;
	private final EmprestimoMapper emprestimoMapper;
	private final EmprestimoValidador emprestimoValidador;

	@Transactional
	@Override
	public EmprestimoDto criarEmprestimo(CriarEmprestimoDto dto) {
		emprestimoValidador.validaSeLivroEstaEmprestado(dto.livroId());
		Usuario usuario = usuarioValidador.validaSeIDDoUsuarioExiste(dto.usuarioId());
		Livro livro = livroValidador.validaSeIdDoLivroExiste(dto.livroId());
		Emprestimo emprestimo = emprestimoMapper.toEntity(dto, usuario, livro);
		Emprestimo salvo = emprestimoRepository.save(emprestimo);
		return emprestimoMapper.toDto(salvo);
	}

	@Transactional
	@Override
	public AtualizarEmprestimoRespostaDto atualizarEmprestimoPeloId(Long id, AtualizarEmprestimoDto dto) {
		Emprestimo emprestimo = emprestimoValidador.buscaIdOuLancaException(id);
		emprestimoMapper.atualizaDto(dto, emprestimo);
		emprestimoRepository.save(emprestimo);
		return emprestimoMapper.toAtualizarDto(emprestimo);
	}

}
