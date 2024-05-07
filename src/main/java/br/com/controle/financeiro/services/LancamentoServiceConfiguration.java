package br.com.controle.financeiro.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class LancamentoServiceConfiguration {

    @Value("${app.limite-valor-lancamento}")
    private BigDecimal limiteValorLancamento;


    @Bean(name = "valorMaximoLancamento")
    public BigDecimal valorMaximoLancamento() {

        return limiteValorLancamento;
    }

}
