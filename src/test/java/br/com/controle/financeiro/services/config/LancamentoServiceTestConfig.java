package br.com.controle.financeiro.services.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;


@TestConfiguration
public class LancamentoServiceTestConfig {

    @Bean(name = "valorMaximoLancamento")
    public BigDecimal valorMaximoLancamento() {
        // Altera o valor máximo para Um Milhão
        return BigDecimal.valueOf(1000000);
    }
}
