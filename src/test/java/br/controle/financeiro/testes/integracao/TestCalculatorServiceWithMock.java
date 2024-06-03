package br.controle.financeiro.testes.integracao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TestCalculatorServiceWithMock {

	@Mock
	private Calculator calculator;

	@InjectMocks
	private CalculatorService calculatorService;

	@SuppressWarnings("deprecation")
	@BeforeEach
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
    public void testAddAndSubtract() {
        // Configura o mock para o método add
        when(calculator.add(3, 3)).thenReturn(8);

        // Configura o mock para o método subtract
        when(calculator.subtract(8, 2)).thenReturn(6);

        // Verifica se o método addAndSubtract retorna o valor esperado
        int result = calculatorService.addAndSubtract(5, 3, 2);
        assertEquals(6, result);

        // Verifica se os métodos foram chamados com os parâmetros corretos
        verify(calculator).add(5, 3);
        verify(calculator).subtract(8, 2);
    }
}
