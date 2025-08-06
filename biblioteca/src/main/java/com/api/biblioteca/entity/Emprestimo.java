package com.api.biblioteca.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "emprestimos")
public class Emprestimo {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(optional = false)//relacionamento obrigatorio.
	@JoinColumn(name = "usuario_id")
	private Usuario usuario;
	
	@ManyToOne(optional = false)//relacionamento obrigatorio.
	@JoinColumn(name = "livro_id")
	private Livro livro;
	
	@CreationTimestamp
	@Column(name = "data_emprestimo", nullable = false)
	private LocalDateTime dataEmprestimo;
	
	@Column(name = "data_devolucao", nullable = false)
	private LocalDate dataDevolucao;
	
	@Column(nullable = false)
	private boolean status;

}
