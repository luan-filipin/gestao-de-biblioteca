package com.api.biblioteca.config;


import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.api.biblioteca.dto.response.ErroCampoDto;
import com.api.biblioteca.dto.response.ErroRespostaDto;
import com.api.biblioteca.exception.EmailDiferenteDoCorpoException;
import com.api.biblioteca.exception.EmailInexistenteException;
import com.api.biblioteca.exception.EmailJaExisteException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErroRespostaDto> handlerGeneric(RuntimeException ex, HttpServletRequest request){
		ErroRespostaDto erro = new ErroRespostaDto(
				ex.getMessage(), 
				HttpStatus.INTERNAL_SERVER_ERROR.value(), 
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(erro);
	}
	
	@ExceptionHandler(EmailDiferenteDoCorpoException.class)
	public ResponseEntity<ErroRespostaDto> handlerEmailDiferenteDoCorpo(EmailDiferenteDoCorpoException ex, HttpServletRequest request){
		ErroRespostaDto erro = new ErroRespostaDto(
				ex.getMessage(),
				HttpStatus.BAD_REQUEST.value(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(erro);
	}
	
	@ExceptionHandler(EmailInexistenteException.class)
	public ResponseEntity<ErroRespostaDto> handlerEmailInexistente(EmailInexistenteException ex, HttpServletRequest request){
		ErroRespostaDto erro = new ErroRespostaDto(
				ex.getMessage(),
				HttpStatus.NOT_FOUND.value(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(erro);
	}
	
	@ExceptionHandler(EmailJaExisteException.class)
	public ResponseEntity<ErroRespostaDto> handlerEmailJaExiste(EmailJaExisteException ex, HttpServletRequest request){
		ErroRespostaDto erro = new ErroRespostaDto(
				ex.getMessage(), 
				HttpStatus.CONFLICT.value(),
				request.getRequestURI());
		return ResponseEntity.status(HttpStatus.CONFLICT).body(erro);
	}
	
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErroRespostaDto> handlerMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
	    
	    List<ErroCampoDto> erros = ex.getBindingResult()
	        .getFieldErrors()
	        .stream()
	        .map(erro -> new ErroCampoDto(erro.getField(), erro.getDefaultMessage()))
	        .toList();
	    
	    ErroRespostaDto resposta = new ErroRespostaDto(
	        "Campos inválidos",
	        HttpStatus.BAD_REQUEST.value(),
	        request.getRequestURI(),
	        erros
	    );
	    
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resposta);
	}
}
