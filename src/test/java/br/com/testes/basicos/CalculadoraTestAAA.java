package br.com.testes.basicos;

	import static org.junit.jupiter.api.Assertions.assertEquals;
	import static org.junit.jupiter.api.Assertions.assertThrows;
	import org.junit.jupiter.api.Test;

public class CalculadoraTestAAA {

		@Test
		void deveSomarDoisInteirosPositivos() {
			// Arrange
			int num1 = 2;
			int num2 = 3;

			Calculadora calculadora = new Calculadora();

			// Act
			int valorObtido = calculadora.soma(num1, num2);
			
			//Assert
			assertEquals(5, valorObtido);
		}

		@Test
		void deveSomarComUmInteiroNegativo() {
			// Arrange
			int num1 = -3;
			int num2 = 2;

			Calculadora calculadora = new Calculadora();

			// Act
			int valorObtido = calculadora.soma(num1, num2);
			
			//Assert
			assertEquals(-1, valorObtido);
		}

		@Test
		void deveSomarComUmInteirosZero() {
			// Arrange
			int num1 = 1;
			int num2 = 0;

			Calculadora calculadora = new Calculadora();

			// Act
			int valorObtido = calculadora.soma(num1, num2);
			
			//Assert
			assertEquals(1,valorObtido);
		}

		@Test
		void deveSubtrairDoisInteirosPositivos() {
			
			//Arrange
			int num1 = 4;
			int num2 = 3;
			
			Calculadora calculadora = new Calculadora();
			
			//Act
			
			int valorObtido = calculadora.subtracao(num1,num2);
			
			//Assert
			assertEquals(1,valorObtido);
			//assertEquals(-5, calculadora.subtracao(0, 5));
			//assertEquals(0, calculadora.subtracao(3, 3));
		}

		@Test
		void deveSubtrairComResultadoNegativo() {
			
			//Arrange
			int num1 = 0;
			int num2 = 5;
			
			Calculadora calculadora = new Calculadora();
			
			//Act
			
			int valorObtido = calculadora.subtracao(num1,num2);
			
			//Assert
			assertEquals(-5,valorObtido);
			//assertEquals(0, calculadora.subtracao(3, 3));
		}

		@Test
		void deveSubtrairRetornandoZero() {
			
			//Arrange
			int num1 = 5;
			int num2 = 5;
			
			Calculadora calculadora = new Calculadora();
			
			//Act
			
			int valorObtido = calculadora.subtracao(num1,num2);
			
			//Assert
		    assertEquals(0,valorObtido);
		}
		
		@Test
		void deveMultiplicarDoisInteiros(){
		     
			//Arrange
			int num1 = 2;
			int num2 = 3;
								
			Calculadora calculadora = new Calculadora();
			
			//Act
			int valorObtido = calculadora.multiplicacao(num1,num2);
			
			//Assert
			assertEquals(6,valorObtido);
			
		}
		
		@Test
		void deveMultiplicarPorZero() {
		     
			//Arrange
			int num1 = 0;
			int num2 = 5;
								
			Calculadora calculadora = new Calculadora();
			
			//Act
			int valorObtido = calculadora.multiplicacao(num1,num2);
			
			//Assert
			assertEquals(0,valorObtido);
			
		}
		
		@Test
		void deveMultiplicarComNumeroNegativo(){
		     
			//Arrange
			int num1 = -2;
			int num2 = 3;
								
			Calculadora calculadora = new Calculadora();
			
			//Act
			int valorObtido = calculadora.multiplicacao(num1,num2);
			
			//Assert
			assertEquals(-6,valorObtido);
			
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

