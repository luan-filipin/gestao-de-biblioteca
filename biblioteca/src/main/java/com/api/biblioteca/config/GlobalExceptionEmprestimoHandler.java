package com.api.biblioteca.config;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.api.biblioteca.dto.response.ErroCampoDto;
import com.api.biblioteca.dto.response.ErroRespostaDto;
import com.api.biblioteca.exception.IdEmprestimoNaoExisteException;
import com.api.biblioteca.exception.LivroIndisponivelException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionEmprestimoHandler {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErroRespostaDto> handlerGenerico(RuntimeException ex, HttpServletRequest request){
		ErroRespostaDto erro = new ErroRespostaDto(
				ex.getMessage(),
				HttpStatus.INTERNAL_SERVER_ERROR.value(),
				request.getRequestURI());
	return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
	}
	
	@ExceptionHandler(IdEmprestimoNaoExisteException.class)
	public ResponseEntity<ErroRespostaDto> handlerIdEmprestimoNaoExiste(IdEmprestimoNaoExisteException ex, HttpServletRequest request){
		ErroRespostaDto erro = new ErroRespostaDto(
				ex.getMessage(),
				HttpStatus.NOT_FOUND.value(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
	}
	
	@ExceptionHandler(LivroIndisponivelException.class)
	public ResponseEntity<ErroRespostaDto> handlerIsbnJaExiste(LivroIndisponivelException ex, HttpServletRequest request){
		ErroRespostaDto erro = new ErroRespostaDto(
				ex.getMessage(), 
				HttpStatus.CONFLICT.value(), 
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
	}
	
	//Esse handler é disparado quando a falha de validação em objetos do corpo da requisição.
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroRespostaDto> handlerMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request){
		
		List<ErroCampoDto> erros = ex.getBindingResult()
				.getFieldErrors()
				.stream()
				.map(erro -> new ErroCampoDto(erro.getField(), erro.getDefaultMessage()))
				.toList();
		
		ErroRespostaDto resposta = new ErroRespostaDto(
				"Campos inválidos",
				HttpStatus.BAD_REQUEST.value(), 
				request.getRequestURI(),
				erros);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
	}
	
	//Esse handler é disparado quando a falha de validação no parametro de entrada.
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErroRespostaDto> handerconstraintViolation(ConstraintViolationException ex, HttpServletRequest request){
		
		List<ErroCampoDto> erros = ex.getConstraintViolations()
				.stream()
				.map(violation -> new ErroCampoDto(
						violation.getPropertyPath().toString(), 
						violation.getMessage()))
				.toList();
		
		ErroRespostaDto resposta = new ErroRespostaDto(
				"Campos inválidos",
				HttpStatus.BAD_REQUEST.value(),
				request.getRequestURI(),
				erros);
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
	}
}
