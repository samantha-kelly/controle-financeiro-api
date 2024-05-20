package br.com.controle.financeiro.testes.tdd;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

public class CalculadoraTDDTeste {

	@Test
	    void deveRetornarSomaCorreta_QuandoSomarDoisNumeros() {
	        
		    //Arrange
		    CalculadoraTDD calculadora = new CalculadoraTDD();
	        int = num1
	        int = num2
	        
	        
		    int resultado = calculadora.soma(2, 3);
	        assertEquals(5, resultado);  // Esperado 2 + 3 = 5
	    }
}

            //Arrange
			int num1 = 2;
			int num2 = 3;
			Calculadora calculadoraTDD = new CalculadoraTDD();
			
            // Act
			int resultado = calculadora.soma(num1, num2);
			
		   //Assert
			assertEquals(5, resultado);

