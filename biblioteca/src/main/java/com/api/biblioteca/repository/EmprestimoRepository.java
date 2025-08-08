package com.api.biblioteca.repository;


import org.springframework.data.jpa.repository.JpaRepository;

import com.api.biblioteca.entity.Emprestimo;

public interface EmprestimoRepository extends JpaRepository<Emprestimo, Long>{

}
