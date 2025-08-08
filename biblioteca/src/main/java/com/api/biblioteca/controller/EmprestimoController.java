package com.api.biblioteca.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.biblioteca.dto.AtualizarEmprestimoDto;
import com.api.biblioteca.dto.CriarEmprestimoDto;
import com.api.biblioteca.dto.response.AtualizarEmprestimoRespostaDto;
import com.api.biblioteca.dto.response.EmprestimoDto;
import com.api.biblioteca.service.EmprestimoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/emprestimos")
public class EmprestimoController {

	private final EmprestimoService emprestimoService;

	@PostMapping
	public ResponseEntity<EmprestimoDto> criaEmprestimo(@RequestBody @Valid CriarEmprestimoDto dto) {
		EmprestimoDto emprestimo = emprestimoService.criarEmprestimo(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(emprestimo);
	}

	@PutMapping
	public ResponseEntity<AtualizarEmprestimoRespostaDto> atualizaEmprestimoPeloId(
			@RequestParam @NotNull(message = "O id é obrigatorio!") Long id,
			@RequestBody @Valid AtualizarEmprestimoDto dto) {
		AtualizarEmprestimoRespostaDto emprestimoAtualizado = emprestimoService.atualizarEmprestimoPeloId(id, dto);
		return ResponseEntity.ok(emprestimoAtualizado);
	}
}
