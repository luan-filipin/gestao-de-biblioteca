package com.api.biblioteca.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.biblioteca.dto.CriarEmprestimoDto;
import com.api.biblioteca.dto.response.EmprestimoDto;
import com.api.biblioteca.service.EmprestimoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("api/emprestimos")
public class EmprestimoController {

	private final EmprestimoService emprestimoService;
	
	@PostMapping
	public ResponseEntity<EmprestimoDto> criaEmprestimo(@RequestBody @Valid CriarEmprestimoDto dto){
		EmprestimoDto emprestimo = emprestimoService.criarEmprestimo(dto);		
		return ResponseEntity.status(HttpStatus.CREATED).body(emprestimo);
	}
}
