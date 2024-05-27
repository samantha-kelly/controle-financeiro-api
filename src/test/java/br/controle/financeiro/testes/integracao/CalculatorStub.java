package br.controle.financeiro.testes.integracao;

public class CalculatorStub extends Calculator {
	@Override
	public int add(int a, int b) {
		// Stub que retorna um valor predefinido
		return 8;
	}

	@Override
	public int subtract(int a, int b) {
		// Stub que retorna um valor predefinido
		return 6;
	}
}
