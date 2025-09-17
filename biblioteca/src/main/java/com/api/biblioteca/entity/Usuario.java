package com.api.biblioteca.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;


@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

	public Usuario(String nome, String email, LocalDateTime dataCadastro, String telefone) {
		super();
		this.nome = nome;
		this.email = email;
		this.dataCadastro = dataCadastro;
		this.telefone = telefone;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String nome;
	
	@Column(nullable = false, unique = true)
	private String email;
	
	@CreationTimestamp
	@Column(name = "data_cadastro", nullable = false)
	private LocalDateTime dataCadastro;
	
	@Column(nullable = false)
	private String telefone;
	
	@OneToMany(mappedBy = "usuario")
	private List<Emprestimo> emprestimos;
}
