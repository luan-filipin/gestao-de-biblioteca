package com.api.biblioteca.service;

import com.api.biblioteca.dto.CriarEmprestimoDto;
import com.api.biblioteca.dto.response.EmprestimoDto;

public interface EmprestimoService {

	EmprestimoDto criarEmprestimo(CriarEmprestimoDto dto);
	
}
