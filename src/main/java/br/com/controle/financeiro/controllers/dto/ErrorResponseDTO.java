package br.com.controle.financeiro.controllers.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponseDTO {

    private String campo;
    private String validacao;
}
