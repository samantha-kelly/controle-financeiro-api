package br.com.controle.financeiro.repositories.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@Builder
public class LancamentoCompletoDTO {

    private String id;

    private String nome;

    private String nomeConta;

    private String nomeCategoria;

    private LocalDate data;

    private BigDecimal valor;

    private boolean pago;
}
