package br.controle.financeiro.testes.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class TestCalculatorServiceWithStub {
	
	@Test
	public void testAddAndSubtract() {
		CalculatorStub calculatorStub = new CalculatorStub();
		CalculatorService calculatorService = new CalculatorService(calculatorStub);

		int result = calculatorService.addAndSubtract(7, 3, 2);
		assertEquals(6, result);
	}
}
