package com.api.biblioteca.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
class LivroRepositoryTest {


	@Autowired
	private LivroRepository livroRepository;

	@Autowired
	private UsuarioRepository usuarioRepository;

	@Autowired
	private EmprestimoRepository emprestimoRepository;

	//Configuração do Testcontainers
	@SuppressWarnings("resource")//Apenas para nao mostrar o warning.
	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15").withDatabaseName("livro_teste")
	.withUsername("admin").withPassword("admin");
	
	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}
	
	// Metodos auxiliares para não repetir a criação dos objetos.
	private Livro criarLivroAux(String titulo, String isbn) {
		Livro livro = new Livro();
		livro.setTitulo(titulo);
		livro.setAutor("autor_teste");
		livro.setIsbn(isbn);
		livro.setDataPublicacao(LocalDate.of(1992, 5, 2));
		livro.setCategoria("categoria_teste");
		return livroRepository.save(livro);
	}
	private Usuario criarUsuarioAux(String nome, String email, String telefone) {
		Usuario usuario = new Usuario(nome, email, LocalDateTime.of(2025, 8, 7, 10, 0), telefone);
		return usuarioRepository.save(usuario);
	}

	private Emprestimo criarEmprestimoAux(Usuario usuario, Livro livro) {
		Emprestimo emprestimo = new Emprestimo(usuario, livro, LocalDateTime.of(2025, 8, 7, 10, 0), LocalDate.now().plusDays(7), true);
		return emprestimoRepository.save(emprestimo);
	}

	@DisplayName("Deve verificar se o Isbn existe com sucesso.")
	@Test
	void deveVerificarSeIsbnExisteComSucesso() {

		criarLivroAux("titulo_teste", "123456");

		boolean exists = livroRepository.existsByIsbn("123456");

		assertTrue(exists);
	}

	@DisplayName("Deve falhar ao verificar se Isbn existe.")
	@Test
	void deveRetornarFalseQuandoIsbnNaoExiste() {

		criarLivroAux("titulo_teste", "123456");

		boolean exists = livroRepository.existsByIsbn("123456" + 1);

		assertFalse(exists);
	}

	@DisplayName("Deve buscar o livro pelo isbn com sucesso.")
	@Test
	void deveBuscarLivroPeloIsbnComSucesso() {

		criarLivroAux("titulo_teste", "123456");

		Optional<Livro> resultado = livroRepository.findByIsbn("123456");

		assertThat(resultado).isPresent();
		assertThat(resultado.get().getIsbn()).isEqualTo("123456");
		assertThat(resultado.get().getTitulo()).isEqualTo("titulo_teste");
	}

	@DisplayName("Deve falhar ao buscar livro pelo Isbn.")
	@Test
	void deveRetornarVazioQuandoIsbnNaoExiste() {

		criarLivroAux("titulo_teste", "123456");

		Optional<Livro> resultado = livroRepository.findByIsbn("123456" + 1);

		assertThat(resultado).isNotPresent();

	}

	@DisplayName("Deve buscar o livro pelo id com sucesso.")
	@Test
	void deveBuscarLivroPeloIdComSucesso() {

		Livro livro = criarLivroAux("titulo_teste", "123456");

		Optional<Livro> resultado = livroRepository.findById(livro.getId());

		assertThat(resultado).isPresent();
		assertThat(resultado.get().getIsbn()).isEqualTo("123456");
		assertThat(resultado.get().getTitulo()).isEqualTo("titulo_teste");
	}

	@DisplayName("Deve falhar ao buscar livro pelo id.")
	@Test
	void deveRetornarVazioQuandoIdNaoExiste() {

		Livro livro = criarLivroAux("titulo_teste", "123456");

		Optional<Livro> resultado = livroRepository.findById(livro.getId() + 1);

		assertThat(resultado).isNotPresent();
	}

	@DisplayName("Deve buscar livros que o usuario ainda nao leu.")
	@Test
	void deveBuscarLivroQueUsuarioAindaNaoLeu() {

		Usuario usuario = criarUsuarioAux("nome_teste", "email@teste.com", "(11) 91234-5678");
		Livro cleanCode = criarLivroAux("titulo_teste_cleanCode", "123456");
		criarLivroAux("titulo_teste_iaProgramer", "123457");

		criarEmprestimoAux(usuario, cleanCode);

		List<Livro> resultado = livroRepository.recomendarLivrosPorCategoriaQueUsuarioAindaNaoLeu("email@teste.com");

		assertThat(resultado).isNotEmpty().hasSize(1);
		assertThat(resultado.get(0).getTitulo()).isEqualTo("titulo_teste_iaProgramer");
	}

	@DisplayName("Deve falhar ao buscar livros para recomendar.")
	@Test
	void deveRetornarListaVaziaQuandoUsuarioJaLeuTodosDaCategoria() {

		Usuario usuario = criarUsuarioAux("nome_teste", "email@teste.com", "(11) 91234-5678");
		Livro cleanCode = criarLivroAux("titulo_teste_cleanCode", "123456");
		criarEmprestimoAux(usuario, cleanCode);

		List<Livro> resultado = livroRepository.recomendarLivrosPorCategoriaQueUsuarioAindaNaoLeu("email@teste.com");

		assertEquals(0, resultado.size());
	}
}
