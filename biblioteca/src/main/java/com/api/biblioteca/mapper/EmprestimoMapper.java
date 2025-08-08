package com.api.biblioteca.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import com.api.biblioteca.dto.AtualizarEmprestimoDto;
import com.api.biblioteca.dto.CriarEmprestimoDto;
import com.api.biblioteca.dto.response.AtualizarEmprestimoRespostaDto;
import com.api.biblioteca.dto.response.EmprestimoDto;
import com.api.biblioteca.entity.Emprestimo;
import com.api.biblioteca.entity.Livro;
import com.api.biblioteca.entity.Usuario;

@Mapper(componentModel = "spring")
public interface EmprestimoMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dataEmprestimo", ignore = true)
    @Mapping(target = "status", constant = "true")
    @Mapping(target = "usuario", source = "usuario")
    @Mapping(target = "livro", source = "livro")
	Emprestimo toEntity(CriarEmprestimoDto dto, Usuario usuario, Livro livro);
	
    @Mapping(target = "nomeUsuario", source = "usuario.nome")
    @Mapping(target = "tituloLivro", source = "livro.titulo")
	EmprestimoDto toDto(Emprestimo entity);
    
    @Mapping(target = "nomeUsuario", source = "usuario.nome")
    @Mapping(target = "tituloLivro", source = "livro.titulo")
    AtualizarEmprestimoRespostaDto toAtualizarDto(Emprestimo entity);
    
    void atualizaDto(AtualizarEmprestimoDto dto, @MappingTarget Emprestimo entity);
    
}
