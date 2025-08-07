package com.api.biblioteca.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.api.biblioteca.entity.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long>{

	boolean existsByIsbn(String isbn);
	
	
}
