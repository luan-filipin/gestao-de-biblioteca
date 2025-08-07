package com.api.biblioteca.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.api.biblioteca.dto.CriarUsuarioDto;
import com.api.biblioteca.dto.response.AtualizaUsuarioDto;
import com.api.biblioteca.dto.response.UsuarioDto;
import com.api.biblioteca.service.UsuarioService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("api/usuarios")
public class UsuarioController {

	private final UsuarioService usuarioService;
	
	@PostMapping
	public ResponseEntity<UsuarioDto> criarUsuario(@RequestBody @Valid CriarUsuarioDto dto){
		UsuarioDto usuarioSalvo = usuarioService.criarUsuario(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(usuarioSalvo);
	}
	
	@GetMapping
	public ResponseEntity<UsuarioDto> buscarPorEmail(@RequestParam @Email(message = "Deve ser um endereço valido!") String email){
		UsuarioDto usuario = usuarioService.buscarUsuarioPorEmail(email);
		return ResponseEntity.ok(usuario);
	}
	
	@DeleteMapping
	public ResponseEntity<Void> deletaPeloEmail(@RequestParam @Email(message = "Deve ser um endereço valido!") String email){
		usuarioService.deletaUsuarioPeloEmail(email);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}
	
	@PutMapping
	public ResponseEntity<UsuarioDto> atualizaPeloEmail(@RequestParam @Email(message = "Deve ser um endereço valido!") String email, @RequestBody @Valid AtualizaUsuarioDto dto){
		UsuarioDto usuarioAtualizado = usuarioService.atualizaUsuarioPeloEmail(email, dto);
		return ResponseEntity.ok(usuarioAtualizado);
	}
	
}
