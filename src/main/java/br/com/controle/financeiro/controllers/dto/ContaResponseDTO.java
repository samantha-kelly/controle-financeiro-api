package br.com.controle.financeiro.controllers.dto;

import br.com.controle.financeiro.domain.Conta;

public record ContaResponseDTO(String id, String nome) {
    public ContaResponseDTO(Conta conta) {
        this(conta.getId(), conta.getNome());
    }
}
