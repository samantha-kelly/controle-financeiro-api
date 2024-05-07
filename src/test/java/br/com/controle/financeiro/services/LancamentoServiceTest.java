package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.LancamentoRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.Lancamento;
import br.com.controle.financeiro.domain.user.Usuario;
import br.com.controle.financeiro.repositories.LancamentoRepository;
import br.com.controle.financeiro.repositories.dto.LancamentoCompletoDTO;
import br.com.controle.financeiro.services.config.LancamentoServiceTestConfig;
import br.com.controle.financeiro.services.exception.NegocioException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@ContextConfiguration(classes = { LancamentoServiceTestConfig.class })
@SpringBootTest
@ExtendWith(SpringExtension.class)
class LancamentoServiceTest {

	@MockBean
	LancamentoRepository lancamentoRepositoryMock;

	@MockBean
	ValidacaoDadosUsuarioService validacaoDadosUsuarioServiceMock;

	@Autowired
	BigDecimal valorMaximoLancamento;

	@Autowired
	LancamentoService lancamentoService;

	@Test
	void deveObterTodosLancamentos() {

		// Arrange
		String loginUsuario = "user@login.com";

		List<Lancamento> lancamentosEsperados = List.of(Lancamento.builder().nome("Pizza").build(),
				Lancamento.builder().nome("Taxi").build());

		Mockito.when(lancamentoRepositoryMock.findLancamentosByUsuario(loginUsuario)).thenReturn(lancamentosEsperados);

		// Act
		List<Lancamento> lancamentosObtidos = lancamentoService.obterTodosLancamentos(loginUsuario);

		// Assert
		Assertions.assertEquals(lancamentosEsperados, lancamentosObtidos);
	}

	@Test
	void deveObterTodosLancamentosCompletos() {

		// Arrange
		String loginUsuario = "user@login.com";

		List<LancamentoCompletoDTO> lancamentosEsperados = List.of(
				LancamentoCompletoDTO.builder().nome("Pizza").build(),
				LancamentoCompletoDTO.builder().nome("Taxi").build());

		Mockito.when(lancamentoRepositoryMock.findLancamentosCompletosByUsuario(loginUsuario))
				.thenReturn(lancamentosEsperados);

		// Act
		List<LancamentoCompletoDTO> lancamentosObtidos = lancamentoService.obterTodosLancamentosCompletos(loginUsuario);

		// Assert
		Assertions.assertEquals(lancamentosEsperados, lancamentosObtidos);
	}

	@Test
	void deveObterLancamentoPorId() {

		// Arrange
		String idLancamento = "123";
		String loginUsuario = "user@login.com";

		Lancamento lancamentoEsperada = Lancamento.builder().nome("Pizza").build();
		Optional<Lancamento> lancamentoEsperadaOpt = Optional.of(lancamentoEsperada);
		Mockito.when(lancamentoRepositoryMock.findById(idLancamento)).thenReturn(lancamentoEsperadaOpt);

		// Act
		Lancamento lancamentoResultado = lancamentoService.obterLancamentoPorId(idLancamento, loginUsuario);

		// Assert
		Assertions.assertEquals(lancamentoEsperada, lancamentoResultado);

		Mockito.verify(validacaoDadosUsuarioServiceMock).validarLancamentoDoUsuarioLogado(idLancamento, loginUsuario);
		Mockito.verify(lancamentoRepositoryMock).findById(idLancamento);
	}

	@Test
	void deveObterLancamentosPorCompetencia() {

		// Arrange
		String loginUsuario = "user@login.com";

		List<Lancamento> todosLancamentos = List.of(
				Lancamento.builder().nome("Pizza").data(LocalDate.of(2024, Month.JANUARY, 4)).build(),
				Lancamento.builder().nome("Viagem").data(LocalDate.of(2024, Month.APRIL, 30)).build(),
				Lancamento.builder().nome("Cinema").data(LocalDate.of(2024, Month.APRIL, 15)).build(),
				Lancamento.builder().nome("Taxi").data(LocalDate.of(2025, Month.APRIL, 20)).build());

		Mockito.when(lancamentoRepositoryMock.findLancamentosByUsuario(loginUsuario)).thenReturn(todosLancamentos);

		Integer competencia = 202404;

		// Act
		List<Lancamento> lancamentosObtidos = lancamentoService.obterLancamentosPorCompetencia(competencia,
				loginUsuario);

		// Assert
		Assertions.assertEquals(2, lancamentosObtidos.size());

		Assertions.assertTrue(lancamentosObtidos.stream().anyMatch(l -> l.getNome().equals("Viagem")));
		Assertions.assertTrue(lancamentosObtidos.stream().anyMatch(l -> l.getNome().equals("Cinema")));
	}

	@Test
	void deveCriarLancamento() {

		// Arrange
		String loginUsuario = "user@login.com";
		String nomeNovoLancamento = "Pizza";

		String idContaFake = "id_conta_fake";
		String idCategoriaFake = "id_categoria_fake";
		String dataLancamento = "03-05-2024";
		BigDecimal valorLancamento = BigDecimal.valueOf(1000000);
		LancamentoRequestDTO novoLancamentoDto = new LancamentoRequestDTO(null, nomeNovoLancamento, idContaFake,
				idCategoriaFake, dataLancamento, valorLancamento, false);

		List<Lancamento> lancamentosExistentes = List.of(Lancamento.builder().id("id_lancamento").nome("Cartão Crédito").build());
		Mockito.when(lancamentoRepositoryMock.findLancamentosByUsuario(loginUsuario)).thenReturn(lancamentosExistentes);

		Usuario usuario = Usuario.builder().login(loginUsuario).id("1234").build();

		Lancamento lancamentoEsperado = Lancamento.builder().nome(nomeNovoLancamento).valor(valorLancamento)
				.data(LocalDate.parse(dataLancamento, java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
				.pago(false).conta(Conta.builder().nome("Cartão Crédito").usuario(usuario).build())
				.categoria(Categoria.builder().nome("Alimentação").usuario(usuario).build()).build();
		Mockito.when(lancamentoRepositoryMock.save(Mockito.any(Lancamento.class))).thenReturn(lancamentoEsperado);

		// Act
		Lancamento lancamentoResultado = lancamentoService.criarLancamento(novoLancamentoDto, loginUsuario);

		// Assert
		Assertions.assertEquals(lancamentoEsperado, lancamentoResultado);
	}

	@Test
	void naoDeveCriarLancamentoComValorIgualZero() {

		// Arrange

		// Usado no caso de utilizar Mockito
		// Alteração de configuração de valor máximo do lançamento somente para esse
		// teste
		BigDecimal valorConfigurado = BigDecimal.valueOf(1000000);
		ReflectionTestUtils.setField(lancamentoService, "valorMaximoLancamento", valorConfigurado);

		String loginUsuario = "user@login.com";
		String nomeNovoLancamento = "Pizza";

		String idContaFake = "id_conta_fake";
		String idCategoriaFake = "id_categoria_fake";
		String dataLancamento = "03-05-2024";
		BigDecimal valorLancamento = BigDecimal.ZERO;
		LancamentoRequestDTO novoLancamentoDto = new LancamentoRequestDTO(null, nomeNovoLancamento, idContaFake,
				idCategoriaFake, dataLancamento, valorLancamento, false);

		// Assert
		try {
			lancamentoService.criarLancamento(novoLancamentoDto, loginUsuario);
			Assertions.fail();
		} catch (NegocioException e) {
			Assertions.assertEquals(e.getMessage(), "Valor do lançamento informado deve ser maior que 0!");
		}
	}

	@Test
	void naoDeveCriarLancamentoComValorMaiorQueO_ValorConfigurado() {

		// Arrange

		// Usado no caso de utilizar Mockito
		// Alteração de configuração de valor máximo do lançamento somente para esse
		// teste
		BigDecimal valorConfigurado = BigDecimal.valueOf(1000000);
		ReflectionTestUtils.setField(lancamentoService, "valorMaximoLancamento", valorConfigurado);

		String loginUsuario = "user@login.com";
		String nomeNovoLancamento = "Pizza";

		String idContaFake = "id_conta_fake";
		String idCategoriaFake = "id_categoria_fake";
		String dataLancamento = "03-05-2024";
		BigDecimal valorLancamento = BigDecimal.valueOf(1000000.01);
		LancamentoRequestDTO novoLancamentoDto = new LancamentoRequestDTO(null, nomeNovoLancamento, idContaFake,
				idCategoriaFake, dataLancamento, valorLancamento, false);

		// Assert
		try {
			lancamentoService.criarLancamento(novoLancamentoDto, loginUsuario);
			Assertions.fail();
		} catch (NegocioException e) {
			Assertions.assertEquals(e.getMessage(), "Valor do lançamento informado não deve ser superior a 1000000!");
		}
	}

	@Test
	void deveAtualizarLancamento() {

		// Arrange
		// Usado no caso de utilizar Mockito
		// Alteração de configuração de valor máximo do lançamento somente para esse
		// teste
		BigDecimal valorConfigurado = BigDecimal.valueOf(1000000);
		ReflectionTestUtils.setField(lancamentoService, "valorMaximoLancamento", valorConfigurado);

		String loginUsuario = "user@login.com";
		String nomeNovoLancamento = "Pizza";

		String idContaFake = "id_conta_fake";
		String idCategoriaFake = "id_categoria_fake";
		String dataLancamento = "03-05-2024";
		BigDecimal valorLancamento = BigDecimal.valueOf(1000000);
		LancamentoRequestDTO novoLancamentoDto = new LancamentoRequestDTO(null, nomeNovoLancamento, idContaFake,
				idCategoriaFake, dataLancamento, valorLancamento, false);

		Lancamento lancamentoPizza = Lancamento.builder().id("id_1").nome("Pizzaria")
				.data(LocalDate.parse("03-05-2024", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
				.build();
		Lancamento lancamentoTaxi = Lancamento.builder().id("id_2").nome("Pizza")
				.data(LocalDate.parse("04-05-2024", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
				.build();
		List<Lancamento> lancamentosExistentes = List.of(lancamentoPizza, lancamentoTaxi);
		Mockito.when(lancamentoRepositoryMock.findLancamentosByUsuario(loginUsuario)).thenReturn(lancamentosExistentes);

		String idLancamento = "id_Lancamento";
		Mockito.when(lancamentoRepositoryMock.findById(idLancamento)).thenReturn(Optional.of(lancamentoTaxi));

		Lancamento lancamentoEsperada = Lancamento.builder().nome(nomeNovoLancamento).build();
		Mockito.when(lancamentoRepositoryMock.save(Mockito.any(Lancamento.class))).thenReturn(lancamentoEsperada);

		// Act
		Lancamento lancamentoResultado = lancamentoService.atualizarLancamento(idLancamento, novoLancamentoDto,
				loginUsuario);

		// Assert
		Assertions.assertEquals(lancamentoEsperada, lancamentoResultado);
	}

	@Test
	void naoDeveAtualizarLancamentoComMesmoNome_E_Data() {

		// Arrange
		// Usado no caso de utilizar Mockito
		// Valor inicializado igual ao valor padrão da aplicação, neste caso
		BigDecimal valorConfigurado = BigDecimal.valueOf(100000);
		ReflectionTestUtils.setField(lancamentoService, "valorMaximoLancamento", valorConfigurado);

		String loginUsuario = "user@login.com";
		String nomeNovoLancamento = "Pizza";

		String idLancamento = "id_lancamento_fake";
		String idContaFake = "id_conta_fake";
		String idCategoriaFake = "id_categoria_fake";
		String dataLancamento = "03-05-2024";
		BigDecimal valorLancamento = BigDecimal.valueOf(100000);
		LancamentoRequestDTO novoLancamentoDto = new LancamentoRequestDTO(null, nomeNovoLancamento, idContaFake,
				idCategoriaFake, dataLancamento, valorLancamento, false);

		Lancamento lancamentoPizza = Lancamento.builder().id("id_1").nome("Pizzaria")
				.data(LocalDate.parse("03-05-2024", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
				.build();
		Lancamento lancamentoTaxi = Lancamento.builder().id("id_2").nome("Pizza")
				.data(LocalDate.parse("03-05-2024", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
				.build();
		List<Lancamento> lancamentosExistentes = List.of(lancamentoPizza, lancamentoTaxi);
		Mockito.when(lancamentoRepositoryMock.findLancamentosByUsuario(loginUsuario)).thenReturn(lancamentosExistentes);

		// Assert
		Assertions.assertThrows(
				// Assert
				NegocioException.class,
				// Act
				() -> lancamentoService.atualizarLancamento(idLancamento, novoLancamentoDto, loginUsuario));
	}

	@Test
	void deveAtualizarLancamentoComoPago() {

		// Arrange
		String loginUsuario = "user@login.com";

		Lancamento lancamentoPizzaNaoPago = Lancamento.builder().nome("Pizza").pago(false).build();

		String idLancamento = "id_Lancamento";
		Mockito.when(lancamentoRepositoryMock.findById(idLancamento)).thenReturn(Optional.of(lancamentoPizzaNaoPago));

		Lancamento lancamentoEsperadoPizzaPago = Lancamento.builder().nome("Pizza").pago(true).build();
		Mockito.when(lancamentoRepositoryMock.save(lancamentoPizzaNaoPago)).thenReturn(lancamentoEsperadoPizzaPago);

		// Act
		Lancamento lancamentoResultado = lancamentoService.atualizarLancamentoComoPago(idLancamento, loginUsuario);

		// Assert
		Assertions.assertEquals(lancamentoEsperadoPizzaPago, lancamentoResultado);
		Assertions.assertTrue(lancamentoResultado.isPago());
	}

	@Test
	void naoDeveAtualizarLancamentoQueJaEstaPagoComoPago() {

		// Arrange
		String loginUsuario = "user@login.com";

		Lancamento lancamentoPizzaJaPago = Lancamento.builder().nome("Pizza").pago(true).build();

		String idLancamento = "id_Lancamento";
		Mockito.when(lancamentoRepositoryMock.findById(idLancamento)).thenReturn(Optional.of(lancamentoPizzaJaPago));

		// Assert
		Assertions.assertThrows(
				// Assert
				NegocioException.class,
				// Act
				() -> lancamentoService.atualizarLancamentoComoPago(idLancamento, loginUsuario));
	}

	@Test
	void deveAtualizarLancamentoComoNaoPago() {

		// Arrange
		String loginUsuario = "user@login.com";

		Lancamento lancamentoPizzaPago = Lancamento.builder().nome("Pizza").pago(true).build();

		String idLancamento = "id_Lancamento";
		Mockito.when(lancamentoRepositoryMock.findById(idLancamento)).thenReturn(Optional.of(lancamentoPizzaPago));

		Lancamento lancamentoEsperadoPizzaNaoPago = Lancamento.builder().nome("Pizza").pago(false).build();
		Mockito.when(lancamentoRepositoryMock.save(lancamentoPizzaPago)).thenReturn(lancamentoEsperadoPizzaNaoPago);

		// Act
		Lancamento lancamentoResultado = lancamentoService.atualizarLancamentoComoNaoPago(idLancamento, loginUsuario);

		// Assert
		Assertions.assertEquals(lancamentoEsperadoPizzaNaoPago, lancamentoResultado);
		Assertions.assertFalse(lancamentoResultado.isPago());
	}

	@Test
	void naoDeveAtualizarLancamentoComoNaoPagoQueJaEstaPagoComoNaoPago() {

		// Arrange
		String loginUsuario = "user@login.com";

		Lancamento lancamentoPizzaJaNaoPago = Lancamento.builder().nome("Pizza").pago(false).build();

		String idLancamento = "id_Lancamento";
		Mockito.when(lancamentoRepositoryMock.findById(idLancamento)).thenReturn(Optional.of(lancamentoPizzaJaNaoPago));

		// Assert
		Assertions.assertThrows(
				// Assert
				NegocioException.class,
				// Act
				() -> lancamentoService.atualizarLancamentoComoNaoPago(idLancamento, loginUsuario));
	}

	@Test
	void deveDeletarLancamento() {

		// Arrange
		String loginUsuario = "user@login.com";
		String idLancamento = "id_Lancamento";
		Mockito.doNothing().when(lancamentoRepositoryMock).deleteById(idLancamento);

		// Act
		lancamentoService.deletarLancamento(idLancamento, loginUsuario);

		// Assert
		Mockito.verify(validacaoDadosUsuarioServiceMock).validarLancamentoDoUsuarioLogado(idLancamento, loginUsuario);
		Mockito.verify(lancamentoRepositoryMock).deleteById(idLancamento);
	}

	@ParameterizedTest
	@MethodSource("provedorParametrosNomesDatasLancamentos")
	void naoDeveLancarErroAoValidarContasComParNome_E_Data_Diferentes(String idLancamento,
			String nomeContaParametrizada, LocalDate dataLancamentoParametrizado,
			List<Lancamento> lancamentosParametrizados) {

		// Assume que a lista de lancamentos seja válida.
		// IGNORA o teste caso a lista de lancamentos seja null, mas não considera uma
		// FALHA.
		Assumptions.assumeTrue(lancamentosParametrizados != null);

		lancamentoService.validarLancamentoComMesmoNomeData(idLancamento, nomeContaParametrizada,
				dataLancamentoParametrizado, lancamentosParametrizados);
	}

	static Stream<Arguments> provedorParametrosNomesDatasLancamentos() {

		List<Lancamento> lancamentos = List.of(
				Lancamento.builder().id("ID_1").nome("Pizza").data(LocalDate.of(2024, Month.JANUARY, 20)).build(),
				Lancamento.builder().id("ID_2").nome("Cinema").data(LocalDate.of(2024, Month.FEBRUARY, 5)).build(),
				Lancamento.builder().id("ID_3").nome("Viagem").data(LocalDate.of(2024, Month.AUGUST, 14)).build());

		return Stream.of(Arguments.of("ID_1", "Pizza", LocalDate.of(2024, Month.JANUARY, 21), lancamentos),
				Arguments.of("ID_2", "Cinema", LocalDate.of(2024, Month.DECEMBER, 5), lancamentos),
				Arguments.of("ID_3", "Viagem", LocalDate.of(2024, Month.AUGUST, 14), null),
				Arguments.of("ID_4", "Viagem", LocalDate.of(2024, Month.JANUARY, 20), lancamentos));
	}
}
