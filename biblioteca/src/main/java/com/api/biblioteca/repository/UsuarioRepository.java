package com.api.biblioteca.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.api.biblioteca.entity.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long>{

}
