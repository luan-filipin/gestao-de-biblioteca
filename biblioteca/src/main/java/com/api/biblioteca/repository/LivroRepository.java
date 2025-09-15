package com.api.biblioteca.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.api.biblioteca.entity.Livro;

public interface LivroRepository extends JpaRepository<Livro, Long> {

	boolean existsByIsbn(String isbn);
	Optional<Livro> findByIsbn(String isbn);

	Optional<Livro> findById(Long id);

	//consulta JPQL
	@Query("""
			    SELECT l FROM Livro l
			    WHERE l.categoria IN (
			        SELECT DISTINCT e.livro.categoria FROM Emprestimo e
			        WHERE e.usuario.email = :email
			    )
			    AND l.id NOT IN (
			        SELECT e.livro.id FROM Emprestimo e
			        WHERE e.usuario.email = :email
			    )
			""")
	List<Livro> recomendarLivrosPorCategoriaQueUsuarioAindaNaoLeu(@Param("email") String email);
}
