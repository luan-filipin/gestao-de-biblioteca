package com.api.biblioteca.entity;

import java.time.LocalDate;
import java.util.List;

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
@Table(name = "livros")
public class Livro {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(nullable = false)
	private String titulo;
	
	@Column(nullable = false)
	private String autor;
	
	@Column(nullable = false, unique = true)
	private String isbn;
	
	@Column(name = "data_publicao", nullable = false)
	private LocalDate dataPublicacao;
	
	@Column(nullable = false)
	private String categoria;
	
	@OneToMany(mappedBy = "livro")
	private List<Emprestimo> emprestimos;

}
