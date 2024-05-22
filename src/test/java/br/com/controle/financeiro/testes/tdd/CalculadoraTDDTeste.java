package br.com.controle.financeiro.testes.tdd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CalculadoraTDDTeste {

	@Test
	void deveRetornarSomaCorreta_QuandoSomarDoisNumeros() {

		// Arrange
		CalculadoraTDD calculadora = new CalculadoraTDD();

		int resultado = calculadora.soma(2, 3);
		assertEquals(5, resultado); // Esperado 2 + 3 = 5
	}
}
