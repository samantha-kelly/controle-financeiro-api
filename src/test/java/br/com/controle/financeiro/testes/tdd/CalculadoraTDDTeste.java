package br.com.controle.financeiro.testes.tdd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CalculadoraTDDTeste {

	@Test
	void deveRetornarSomaCorreta_QuandoSomarDoisNumeros() {

		// Arrange
		int num1 = 2;
		int num2 = 3;
		CalculadoraTDD calculadora = new CalculadoraTDD();

		//Act
		int resultado = calculadora.soma(num1,num2);
		
		//Assert		
		assertEquals(5, resultado); 
		
	}
	

	@Test
	void deveRetornarSubtracaoCorreta_QuandoSubtrairDoisNumeros() {

		// Arrange
		int num1 = 4;
		int num2 = -2;
		CalculadoraTDD calculadora = new CalculadoraTDD();

		//Act
		int resultado = calculadora.subtracao(num1,num2);
		
		//Assert		
		assertEquals(6, resultado); 
		
	}
	

}
