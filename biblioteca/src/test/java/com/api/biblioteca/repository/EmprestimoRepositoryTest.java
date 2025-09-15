package com.api.biblioteca.repository;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

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
	
	
	@DisplayName("Deve verificar se o livro existe com sucesso.")
	@Test
	void deveVerificarSeLivroExisteComSucesso() {
		
		Usuario usuario = new Usuario();
		usuario.setNome("nome_teste");
		usuario.setEmail("email@teste.com");
		usuario.setTelefone("99999999");
		usuarioRepository.save(usuario);

		Livro livro = new Livro();
		livro.setTitulo("titulo_teste");
		livro.setAutor("autor_teste");
		livro.setIsbn("123456789");
		livro.setDataPublicacao(LocalDate.of(1992, 5, 2));
		livro.setCategoria("categoria_teste");
		livroRepository.save(livro);
		
		Emprestimo emprestimo = new Emprestimo();
		emprestimo.setUsuario(usuario);
		emprestimo.setLivro(livro);
		emprestimo.setDataDevolucao(LocalDate.now().plusDays(7));
		emprestimo.setStatus(true);
		emprestimoRepository.save(emprestimo);		
		
		boolean exists = emprestimoRepository.existsByLivroIdAndStatusTrue(livro.getId());
		
		assertTrue(exists);
	}
	
	@DisplayName("Deve falhar ao verificar a existencia do livro.")
	@Test
	void deveFalharAoVerificarLivroExistente() {
		
		Usuario usuario = new Usuario();
		usuario.setNome("nome_teste");
		usuario.setEmail("email@teste.com");
		usuario.setTelefone("99999999");
		usuarioRepository.save(usuario);

		Livro livro = new Livro();
		livro.setTitulo("titulo_teste");
		livro.setAutor("autor_teste");
		livro.setIsbn("123456789");
		livro.setDataPublicacao(LocalDate.of(1992, 5, 2));
		livro.setCategoria("categoria_teste");
		livroRepository.save(livro);
		
		Emprestimo emprestimo = new Emprestimo();
		emprestimo.setUsuario(usuario);
		emprestimo.setLivro(livro);
		emprestimo.setDataDevolucao(LocalDate.now().plusDays(7));
		emprestimo.setStatus(true);
		emprestimoRepository.save(emprestimo);		
		
		boolean exists = emprestimoRepository.existsByLivroIdAndStatusTrue(livro.getId() + 1);
		
		assertFalse(exists);
	}
	
}
