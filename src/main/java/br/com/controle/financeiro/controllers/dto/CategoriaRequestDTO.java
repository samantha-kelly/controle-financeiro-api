package br.com.controle.financeiro.controllers.dto;

import jakarta.validation.constraints.NotBlank;

public record CategoriaRequestDTO(String id,
                                  @NotBlank(message = "Campo obrigatório não informado.")
                                  String nome) {

}
