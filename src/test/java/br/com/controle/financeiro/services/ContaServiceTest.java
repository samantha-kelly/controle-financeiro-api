package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.ContaRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.Lancamento;
import br.com.controle.financeiro.domain.user.Usuario;
import br.com.controle.financeiro.repositories.ContaRepository;
import br.com.controle.financeiro.repositories.UsuarioRepository;
import br.com.controle.financeiro.services.exception.NegocioException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SpringBootTest
class ContaServiceTest {

    @MockBean
    private ContaRepository contaRepositoryMock;
    @MockBean
    private UsuarioRepository usuarioRepositoryMock;
    @MockBean
    private ValidacaoDadosUsuarioService validacaoDadosUsuarioServiceMock;

    @Autowired
    private ContaService contaService;

    @Test
    void deveObterTodasContas() {

        //Arrange
        String loginUsuario = "user@login.com";

        List<Conta> contasEsperadas = List.of(
                Conta.builder().nome("Conta Corrente").build(),
                Conta.builder().nome("Cartão Crédito").build());

        Mockito.when(contaRepositoryMock.findAllContasByUsuarioLogin(loginUsuario)).thenReturn(contasEsperadas);

        //Act
        List<Conta> contasObtidas = contaService.obterTodasContas(loginUsuario);

        //Assert
        Assertions.assertEquals(contasEsperadas, contasObtidas);
    }

    @Test
    void deveObterContaPorId() {

        //Arrange
        String idConta = "123";
        String loginUsuario = "user@login.com";

        Conta contaEsperada = Conta.builder().nome("Cartão Crédito").build();
        Optional<Conta> contaEsperadaOpt = Optional.of(contaEsperada);
        Mockito.when(contaRepositoryMock.findById(idConta)).thenReturn(contaEsperadaOpt);

        //Act
        Conta contaResultado = contaService.obterContaPorId(idConta, loginUsuario);

        //Assert
        Assertions.assertEquals(contaEsperada, contaResultado);

        Mockito.verify(validacaoDadosUsuarioServiceMock)
                .validarContaDoUsuarioLogado(idConta, loginUsuario);
        Mockito.verify(contaRepositoryMock).findById(idConta);
    }

    @Test
    void deveCriarConta() {

        //Arrange
        String loginUsuario = "user@login.com";
        String nomeNovaConta = "Conta Corrente";

        ContaRequestDTO novaContaDto = new ContaRequestDTO(null, nomeNovaConta);

        List<Conta> contasExistentes = List.of(Conta.builder().nome("Cartão Crédito").build());
        Mockito.when(contaRepositoryMock.findAllContasByUsuarioLogin(loginUsuario)).thenReturn(contasExistentes);

        Usuario usuario = Usuario.builder().login(loginUsuario).id("1234").build();
        Mockito.when(usuarioRepositoryMock.findByLogin(loginUsuario)).thenReturn(usuario);

        Conta contaEsperada = Conta.builder().nome(nomeNovaConta).usuario(usuario).build();
        Mockito.when(contaRepositoryMock.save(Mockito.any(Conta.class))).thenReturn(contaEsperada);

        //Act
        Conta contaResultado = contaService.criarConta(novaContaDto, loginUsuario);

        //Assert
        Assertions.assertEquals(contaEsperada, contaResultado);
    }

    @Test
    void deveAtualizarConta() {

        //Arrange
        String loginUsuario = "user@login.com";
        String nomeNovaConta = "Conta Conjunta";

        ContaRequestDTO novaContaDto = new ContaRequestDTO(null, nomeNovaConta);

        Conta cartaoCredito = Conta.builder().nome("Cartão Crédito").build();
        Conta contaCorrente = Conta.builder().nome("Conta Corrente").build();
        List<Conta> contasExistentes = List.of(cartaoCredito, contaCorrente);
        Mockito.when(contaRepositoryMock.findAllContasByUsuarioLogin(loginUsuario)).thenReturn(contasExistentes);

        Usuario usuario = Usuario.builder().login(loginUsuario).id("1234").build();
        String idConta = "id_Conta";
        Mockito.when(contaRepositoryMock.findById(idConta)).thenReturn(Optional.of(contaCorrente));

        Conta contaEsperada = Conta.builder().nome(nomeNovaConta).usuario(usuario).build();
        Mockito.when(contaRepositoryMock.save(Mockito.any(Conta.class))).thenReturn(contaEsperada);

        //Act
        Conta contaResultado = contaService.atualizarConta(idConta, novaContaDto, loginUsuario);

        //Assert
        Assertions.assertEquals(contaEsperada, contaResultado);
    }

    @Test
    void naoDeveAtualizarContaComMesmoNome() {

        //Arrange
        String loginUsuario = "user@login.com";
        String nomeNovaConta = "Conta Corrente";

        ContaRequestDTO novaContaDto = new ContaRequestDTO(null, nomeNovaConta);

        Conta cartaoCredito = Conta.builder().nome("Cartão Crédito").build();
        Conta contaCorrente = Conta.builder().nome("Conta Corrente").build();
        List<Conta> contasExistentes = List.of(cartaoCredito, contaCorrente);
        Mockito.when(contaRepositoryMock.findAllContasByUsuarioLogin(loginUsuario)).thenReturn(contasExistentes);

        String idConta = "id_Conta";

        //Assert
        Assertions.assertThrows(
                //Assert
                NegocioException.class,
                //Act
                () -> contaService.atualizarConta(idConta, novaContaDto, loginUsuario)
        );
    }

    @Test
    void deveDeletarConta() {

        //Arrange
        String loginUsuario = "user@login.com";
        String idConta = "id_Conta";

        Mockito.when(contaRepositoryMock.findById(idConta)).thenReturn(Optional.of(Conta.builder().build()));

        Mockito.doNothing().when(contaRepositoryMock).deleteById(idConta);

        //Act
        contaService.deletarConta(idConta, loginUsuario);

        //Assert
        Mockito.verify(validacaoDadosUsuarioServiceMock).validarContaDoUsuarioLogado(idConta, loginUsuario);
        Mockito.verify(contaRepositoryMock).deleteById(idConta);
    }

    @Test
    void naoDeveDeletarContaComLancamentoAssociado() {

        //Arrange
        String loginUsuario = "user@login.com";
        String idConta = "id_Conta";

        Conta contaCorrente = Conta.builder().nome("Conta Corrente").build();

        List<Lancamento> lancamentos = List.of(Lancamento.builder()
                .conta(contaCorrente).nome("Pipoca")
                .categoria(Categoria.builder().build())
                .valor(BigDecimal.valueOf(123.45))
                .data(LocalDate.now())
                .build());

        contaCorrente.setLancamentos(lancamentos);

        Mockito.when(contaRepositoryMock.findById(idConta)).thenReturn(Optional.of(contaCorrente));

        //Assert
        Assertions.assertThrows(
                //Assert
                NegocioException.class,
                //Act
                () -> contaService.deletarConta(idConta, loginUsuario)
        );
    }

    @Test
    void naoDeveLancarErroAoValidarContaNomeDiferente() {

        //Arrange
        List<Conta> contas = List.of(Conta.builder().nome("Cartão Crédito").build());

        //Act
        contaService.validarContaComMesmoNome("Conta Conjunta", contas);

        //Assert
        //Não lançar erro.
    }

    @Test
    void deveLancarErroAoValidarContaMesmoNome() {

        //Arrange
        List<Conta> contas = List.of(
                Conta.builder().nome("Conta Corrente").build(),
                Conta.builder().nome("Cartão Crédito").build());

        Assertions.assertThrows(
                //Assert
                NegocioException.class,
                //Act
                () -> contaService.validarContaComMesmoNome("Cartão Crédito", contas)
        );
    }

    @ParameterizedTest
    @MethodSource("provedorParametrosNomesContas")
    void naoDeveLancarErroAoValidarContasComNomesDiferentes(String nomeContaParametrizada, List<Conta> contasParametrizada) {

        // Assume que a lista de conta seja válida.
        // IGNORA o teste caso a lista de contas seja null, mas não considera uma FALHA.
        Assumptions.assumeTrue(contasParametrizada != null);

        contaService.validarContaComMesmoNome(nomeContaParametrizada, contasParametrizada);
    }

    static Stream<Arguments> provedorParametrosNomesContas() {

        List<Conta> contas = List.of(
                Conta.builder().nome("Conta Conjunta").build(),
                Conta.builder().nome("Cartão Crédito 1").build(),
                Conta.builder().nome("Cartão Crédito 2").build());

        return Stream.of(
                Arguments.of("Conta Conjunta 1", contas),
                Arguments.of("Cartão Crédito 3", contas),
                Arguments.of("Conta teste 3", null),
                Arguments.of("Cartão Crédito 4", contas)
        );
    }
}