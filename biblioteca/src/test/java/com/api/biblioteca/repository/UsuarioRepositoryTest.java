package com.api.biblioteca.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.time.LocalDateTime;
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

import com.api.biblioteca.entity.Usuario;

@Testcontainers
@DataJpaTest
class UsuarioRepositoryTest {
	
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@SuppressWarnings("resource")//Apenas para nao mostrar o warning.
	@Container
	static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15").withDatabaseName("usuario_teste")
	.withUsername("admin").withPassword("admin");
	
	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", postgres::getJdbcUrl);
		registry.add("spring.datasource.username", postgres::getUsername);
		registry.add("spring.datasource.password", postgres::getPassword);
	}
	
	// Metodos auxiliares para não repetir a criação dos objetos
	private Usuario criarUsuario(String nome, String email, String telefone) {
		Usuario usuario = new Usuario(nome, email, LocalDateTime.of(2025, 8, 7, 10, 0), telefone);
		return usuarioRepository.save(usuario);
	}
	
	@DisplayName("Deve verificar se email existe com sucesso.")
	@Test
	void DeveRetornarTrueSeEmailExistir() {
		
		criarUsuario("nome_teste","email@teste.com","(11) 91234-5678");
		
		boolean resultado = usuarioRepository.existsByEmail("email@teste.com");
		
		assertTrue(resultado);
	}
	
	@DisplayName("Deve retornar falso se o email nao existir.")
	@Test
	void deveRetornarFalsoSeEmailNaoExistir() {
		
		criarUsuario("nome_teste","email@teste.com","(11) 91234-5678");
		
		boolean resultado = usuarioRepository.existsByEmail("email@testefalha.com");
		
		assertFalse(resultado);
	}
	
	@DisplayName("Deve retornar um usuario pelo email com sucesso.")
	@Test
	void deveRetornarUsuarioPeloEmailComSucesso() {
		
		criarUsuario("nome_teste","email@teste.com","(11) 91234-5678");
		
		Optional<Usuario> resultado = usuarioRepository.findByEmail("email@teste.com");
		
		assertThat(resultado).isPresent();
		assertThat(resultado.get().getNome()).isEqualTo("nome_teste");
		assertThat(resultado.get().getEmail()).isEqualTo("email@teste.com");
	}
	
	@DisplayName("Deve retornar vazio se o email nao existir.")
	@Test
	void deveRetornarVazioSeEmailNaoExistir() {
		
		criarUsuario("nome_teste","email@teste.com","(11) 91234-5678");
		
		Optional<Usuario> resultado = usuarioRepository.findByEmail("email@testeFalha.com");
		
		assertThat(resultado).isNotPresent();

	}
	
	@DisplayName("Deve buscar usuario pelo Id com sucesso.")
	@Test
	void deveBuscarUsuarioPeloIdComSucesso() {
		
		Usuario usuario = criarUsuario("nome_teste","email@teste.com","(11) 91234-5678");
		
		Optional<Usuario> resultado = usuarioRepository.findById(usuario.getId());
		
		assertThat(resultado).isPresent();
		assertThat(resultado.get().getNome()).isEqualTo("nome_teste");
		assertThat(resultado.get().getEmail()).isEqualTo("email@teste.com");
	}
	
	@DisplayName("Deve retornar vazio se Id nao existir.")
	@Test
	void deveRetornarVazioSeIdNaoExistir() {
		
		Usuario usuario = criarUsuario("nome_teste","email@teste.com","(11) 91234-5678");

		Optional<Usuario> resultado = usuarioRepository.findById(usuario.getId() + 1);
		
		assertThat(resultado).isNotPresent();

	}
}
