package br.com.controle.financeiro.services;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.repositories.ContaRepository;
import br.com.controle.financeiro.repositories.UsuarioRepository;
import br.com.controle.financeiro.services.stub.ContaRepositoryStub;

class ContaServiceTestComStub {

	// ContaRepository instanciado com stub
	private ContaRepository contaRepositoryStub = new ContaRepositoryStub();
	// Outras dependências inicializadas com null, pois não serão utilizadas nesse teste
	private UsuarioRepository usuarioRepository = null;
	private ValidacaoDadosUsuarioService validacaoDadosUsuarioService = null;

	private ContaService contaService;

	@Test
	void deveObterTodasContas() {
		
		// Arrange
		
		contaService = new ContaService(
				contaRepositoryStub, 
				usuarioRepository, 
				validacaoDadosUsuarioService);

		
		String loginUsuario = "user@login.com";
		
		List<Conta> contasEsperadas = contaRepositoryStub.findAllContasByUsuarioLogin(loginUsuario);

		// Act
		List<Conta> contasObtidas = contaService.obterTodasContas(loginUsuario);

		// Assert
		Assertions.assertEquals(contasEsperadas, contasObtidas);
	}

}