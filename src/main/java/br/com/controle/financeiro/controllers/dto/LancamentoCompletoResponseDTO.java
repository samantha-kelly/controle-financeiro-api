package br.com.controle.financeiro.controllers.dto;

import br.com.controle.financeiro.repositories.dto.LancamentoCompletoDTO;

import java.math.BigDecimal;

public record LancamentoCompletoResponseDTO(String id, String nome, String nomeConta,
                                            String nomeCategoria, String data, BigDecimal valor, boolean pago) {
    public LancamentoCompletoResponseDTO(LancamentoCompletoDTO lancamentoCompletoDTO) {

        this(lancamentoCompletoDTO.getId(),
                lancamentoCompletoDTO.getNome(),
                lancamentoCompletoDTO.getNomeConta(),
                lancamentoCompletoDTO.getNomeCategoria(),
                lancamentoCompletoDTO.getData().format(java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")),
                lancamentoCompletoDTO.getValor(),
                lancamentoCompletoDTO.isPago());

    }
}
