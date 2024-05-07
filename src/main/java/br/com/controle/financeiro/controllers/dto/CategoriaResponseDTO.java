package br.com.controle.financeiro.controllers.dto;

import br.com.controle.financeiro.domain.Categoria;

public record CategoriaResponseDTO(String id, String nome) {
    public CategoriaResponseDTO(Categoria categoria) {
        this(categoria.getId(), categoria.getNome());
    }
}
