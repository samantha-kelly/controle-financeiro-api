package br.controle.financeiro.testes.integracao;

public class CalculatorService {
	private Calculator calculator;

	public CalculatorService(Calculator calculator) {
		this.calculator = calculator;
	}

	public int addAndSubtract(int a, int b, int subtractValue) {
		int sum = calculator.add(a, b);
		return calculator.subtract(sum, subtractValue);
	}
}
