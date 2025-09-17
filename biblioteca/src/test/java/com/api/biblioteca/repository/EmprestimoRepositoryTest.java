package com.api.biblioteca.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.api.biblioteca.entity.Emprestimo;
import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.entity.Usuario;


@Testcontainers
@DataJpaTest
class EmprestimoRepositoryTest {

	@SuppressWarnings("resource")//Apenas para nao mostrar o warning.
	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
		.withDatabaseName("emprestimo_teste")
		.withUsername("admin")
		.withPassword("admin");
	
	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private EmprestimoRepository emprestimoRepository;
	
	@Autowired
	private LivroRepository livroRepository;
	
	// Metodos auxiliares para não repetir a criação dos objetos
	private Usuario criarUsuario(String nome, String email, String telefone) {
		Usuario usuario = new Usuario(nome, email, LocalDateTime.of(2025, 8, 7, 10, 0), telefone);
		return usuarioRepository.save(usuario);
	}
	private Livro criarLivroAux(String titulo, String autor, String isbn, String categoria) {	
		Livro livro = new Livro(titulo, autor, isbn, LocalDate.of(2025, 8, 7), categoria);
		return livroRepository.save(livro);
	}
	private Emprestimo criarEmprestimoAux(Usuario usuario, Livro livro) {
		Emprestimo emprestimo = new Emprestimo(usuario, livro, LocalDateTime.of(2025, 8, 7, 10, 0), LocalDate.of(2025, 8, 7), true);
		return emprestimoRepository.save(emprestimo);
	}
	
	@DisplayName("Deve verificar se o livro existe com sucesso.")
	@Test
	void deveVerificarSeLivroExisteComSucesso() {
		
		Usuario usuario = criarUsuario("nome_teste","email@teste.com","(11) 91234-5678");
		Livro livro = criarLivroAux("Clean Code", "Robert C. Martin", "9780132350884", "Programação");
		criarEmprestimoAux(usuario, livro);	
		
		boolean exists = emprestimoRepository.existsByLivroIdAndStatusTrue(livro.getId());
		
		assertTrue(exists);
	}
	
	@DisplayName("Deve falhar ao verificar a existencia do livro.")
	@Test
	void deveFalharAoVerificarLivroExistente() {
		
		Usuario usuario = criarUsuario("nome_teste","email@teste.com","(11) 91234-5678");
		Livro livro = criarLivroAux("Clean Code", "Robert C. Martin", "9780132350884", "Programação");
		criarEmprestimoAux(usuario, livro);	
		
		boolean exists = emprestimoRepository.existsByLivroIdAndStatusTrue(livro.getId() + 1);
		
		assertFalse(exists);
	}
	
}
