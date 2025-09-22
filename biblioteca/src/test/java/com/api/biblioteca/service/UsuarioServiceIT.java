package com.api.biblioteca.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.api.biblioteca.dto.CriarUsuarioDto;
import com.api.biblioteca.dto.response.AtualizaUsuarioDto;
import com.api.biblioteca.dto.response.UsuarioDto;
import com.api.biblioteca.entity.Usuario;
import com.api.biblioteca.exception.EmailInexistenteException;
import com.api.biblioteca.exception.EmailJaExisteException;
import com.api.biblioteca.repository.UsuarioRepository;

@SpringBootTest
@Testcontainers
@Transactional
class UsuarioServiceIT {
	
	@Autowired
	private UsuarioRepository usuarioRepository;
	
	@Autowired
	private UsuarioServiceImp usuarioServiceImp;

	//Configuração do Testcontainers
	@SuppressWarnings("resource")//Apenas para nao mostrar o warning.
    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("livro_teste")
            .withUsername("admin")
            .withPassword("admin");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }
    
	private CriarUsuarioDto criarUsuarioDtoAux() {
		return new CriarUsuarioDto("Teste_nome", "Teste@teste.com", "(99) 99999-9999");
	}
	
	private AtualizaUsuarioDto criarUsuarioDtoAtualizadoAux() {
		return new AtualizaUsuarioDto("Teste_nomeAtualizado", "(88) 88888-8888");
	}
    
    @DisplayName("Deve criar usuario com sucesso")
	@Test
	void deveCriarUsuarioComSucesso() {
    	
    	CriarUsuarioDto usuarioDtoEntrada = criarUsuarioDtoAux();
    	
    	// Valida Dto de retorno.
    	UsuarioDto usuarioSalvo = usuarioServiceImp.criarUsuario(usuarioDtoEntrada);
    	
    	assertThat(usuarioSalvo).isNotNull();
    	assertThat(usuarioSalvo.nome()).isEqualTo(usuarioDtoEntrada.nome());
    	assertThat(usuarioSalvo.email()).isEqualTo(usuarioDtoEntrada.email());
    	assertThat(usuarioSalvo.telefone()).isEqualTo(usuarioDtoEntrada.telefone());
    	
    	//Valida persistência no banco.
    	Usuario usuarioNoBanco = usuarioRepository.findByEmail(usuarioSalvo.email()).orElseThrow(EmailJaExisteException::new);
    	
    	assertThat(usuarioNoBanco).isNotNull();
    	assertThat(usuarioNoBanco.getId()).isEqualTo(usuarioSalvo.id());
    	assertThat(usuarioNoBanco.getNome()).isEqualTo(usuarioSalvo.nome());
    	assertThat(usuarioNoBanco.getEmail()).isEqualTo(usuarioSalvo.email());
    	assertThat(usuarioNoBanco.getTelefone()).isEqualTo(usuarioSalvo.telefone());
    }
    
    @DisplayName("Deve lançar exception se email ja existir.")
	@Test
	void deveLancarExceptionSeEmailJaExistir() {
    	
    	CriarUsuarioDto usuarioDtoEntrada = criarUsuarioDtoAux();
    	usuarioServiceImp.criarUsuario(usuarioDtoEntrada);
    	
    	assertThatThrownBy(()-> usuarioServiceImp.criarUsuario(usuarioDtoEntrada))
    	.isInstanceOf(EmailJaExisteException.class)
    	.hasMessage("O email ja existe!");
    }
    
    @DisplayName("Deve buscar o usuario pelo Email com sucesso")
	@Test
	void deveBuscarUsuarioPeloEmailComSucesso() {
    	
    	CriarUsuarioDto usuarioDtoEntrada = criarUsuarioDtoAux();
    	usuarioServiceImp.criarUsuario(usuarioDtoEntrada);
    	
    	UsuarioDto usuario = usuarioServiceImp.buscarUsuarioPorEmail("Teste@teste.com");
    	
    	assertThat(usuario).isNotNull();
    	assertThat(usuario.nome()).isEqualTo("Teste_nome");
    	assertThat(usuario.email()).isEqualTo("Teste@teste.com");
    	assertThat(usuario.telefone()).isEqualTo("(99) 99999-9999");
    }
    
    @DisplayName("Deve lanças exception se usuario nao existir")
	@Test
	void deveLancarExceptionSeUsuarioNaoExistir() {
    	
    	String email = "email@inexistente.com";
    	
    	assertThatThrownBy(()-> usuarioServiceImp.buscarUsuarioPorEmail(email))
    	.isInstanceOf(EmailInexistenteException.class)
    	.hasMessage("O email não existe!");
    }
    
    @DisplayName("Deve deletar usuario pelo email com sucesso")
	@Test
	void deveDeletarUsuarioPeloEmailComSucesso() {
    	
    	CriarUsuarioDto usuarioDtoEntrada = criarUsuarioDtoAux();
    	usuarioServiceImp.criarUsuario(usuarioDtoEntrada);
    	
    	usuarioServiceImp.deletaUsuarioPeloEmail("Teste@teste.com");
    	boolean existis = usuarioRepository.existsByEmail("Teste@teste.com");
    	
    	assertThat(existis).isFalse();
    }
    
    @DisplayName("Deve lançar expcetion se o email nao existir antes de deletar")
	@Test
	void deveLancarExceptionSeEmailNaoExistir() {
    	
    	String email = "email@inexistente.com";
    	
    	assertThatThrownBy(()-> usuarioServiceImp.deletaUsuarioPeloEmail(email))
    	.isInstanceOf(EmailInexistenteException.class)
    	.hasMessage("O email não existe!");
    }
    
    @DisplayName("Deve atualiza o usuario com sucesso.")
	@Test
	void deveAtualizarUsuarioComSucesso() {
    	
    	String email = "Teste@teste.com";
    	CriarUsuarioDto usuarioDtoEntrada = criarUsuarioDtoAux();
    	UsuarioDto usuarioSalvo = usuarioServiceImp.criarUsuario(usuarioDtoEntrada);
    	
    	AtualizaUsuarioDto usuarioAtualizado = criarUsuarioDtoAtualizadoAux();
    	
    	// Valida DTO retornado
    	UsuarioDto usuarioNovo = usuarioServiceImp.atualizaUsuarioPeloEmail(email, usuarioAtualizado);
    	
    	assertThat(usuarioNovo).isNotNull();
    	assertThat(usuarioNovo.nome()).isEqualTo(usuarioAtualizado.nome());
    	assertThat(usuarioNovo.telefone()).isEqualTo(usuarioAtualizado.telefone());
    	
    	// Valida persistência no banco
    	Usuario usuarioNoBanco = usuarioRepository.findByEmail(email).orElseThrow(EmailInexistenteException::new);
    	
    	assertThat(usuarioNoBanco).isNotNull();
    	assertThat(usuarioNoBanco.getId()).isEqualTo(usuarioNovo.id());
    	assertThat(usuarioNoBanco.getNome()).isEqualTo(usuarioNovo.nome());
    	assertThat(usuarioNoBanco.getEmail()).isEqualTo(usuarioSalvo.email());
    }
}
