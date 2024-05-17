package br.com.testes.basicos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class CalculadoraTest {

	@Test
	 void testSoma() {
	 Calculadora calculadora = new Calculadora();
	 assertEquals(5, calculadora.soma(3, 3));
	 assertEquals(-1, calculadora.soma(-3, 2));
	 assertEquals(0, calculadora.soma(0, 0));
	 }


	@Test
	void testSubtracao() {
		Calculadora calculadora = new Calculadora();
		assertEquals(1, calculadora.subtracao(4, 3));
		assertEquals(-5, calculadora.subtracao(0, 5));
		assertEquals(0, calculadora.subtracao(3, 3));
	}

	@Test
	void testMultiplicacao() {
		Calculadora calculadora = new Calculadora();
		assertEquals(6, calculadora.multiplicacao(2, 3));
		assertEquals(0, calculadora.multiplicacao(0, 5));
		assertEquals(-6, calculadora.multiplicacao(-2, 3));
	}

	@Test
	void testDivisao() {
		Calculadora calculadora = new Calculadora();
		assertEquals(2, calculadora.divisao(6, 3));
		assertEquals(0, calculadora.divisao(0, 5));
		assertEquals(-2, calculadora.divisao(-6, 3));

		// Teste para divisão por zero
		assertThrows(IllegalArgumentException.class, () -> {
			calculadora.divisao(6, 0);
		});
	}

}
