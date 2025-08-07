package com.api.biblioteca.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.biblioteca.entity.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long>{

	boolean existsByIsbn(String isbn);
	
	Optional<Livro> findByIsbn(String isbn);
}
