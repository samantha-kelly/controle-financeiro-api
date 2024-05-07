package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.LancamentoRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.Lancamento;
import br.com.controle.financeiro.domain.user.UserRole;
import br.com.controle.financeiro.domain.user.Usuario;
import br.com.controle.financeiro.repositories.CategoriaRepository;
import br.com.controle.financeiro.repositories.ContaRepository;
import br.com.controle.financeiro.repositories.LancamentoRepository;
import br.com.controle.financeiro.repositories.UsuarioRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class LancamentoServiceIntegrationTest {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private LancamentoService lancamentoService;

    private Usuario usuarioPadrao = null;

    @BeforeEach
    public void prepararMassaTeste() {
        this.limparBase();
        this.criarUsuarioPadrao();
    }

    private void limparBase() {
        lancamentoRepository.deleteAll();
        categoriaRepository.deleteAll();
        contaRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    private void criarUsuarioPadrao() {
        String userLogin = "joao@teste.com";
        String userPassword = "senha_do_joao";
        usuarioPadrao = Usuario.builder().login(userLogin).password(userPassword).role(UserRole.ADMIN).build();
        usuarioRepository.save(usuarioPadrao);
    }

    @Test
    void deveObterLancamentosDoUsuario() {

        //Arrange
        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).usuario(usuarioPadrao).build();

        String nomeCategoriaAlimentacao = "Alimentacao";
        Categoria categoriaAlimentacao = Categoria.builder().nome(nomeCategoriaAlimentacao).usuario(usuarioPadrao).build();

        contaRepository.save(contaCorrente);
        categoriaRepository.save(categoriaAlimentacao);

        BigDecimal valorLancamento = BigDecimal.TEN;

        Lancamento lancamentoPizza = Lancamento.builder()
                .nome("Pizza")
                .valor(valorLancamento)
                .data(LocalDate.parse("03-05-2024", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .pago(false)
                .conta(contaCorrente)
                .categoria(categoriaAlimentacao)
                .build();

        Lancamento lancamentoPastel = Lancamento.builder()
                .nome("Pastel")
                .valor(BigDecimal.TWO)
                .data(LocalDate.parse("03-05-2024", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .pago(true)
                .conta(contaCorrente)
                .categoria(categoriaAlimentacao)
                .build();

        lancamentoRepository.save(lancamentoPizza);
        lancamentoRepository.save(lancamentoPastel);

        // Act
        List<Lancamento> lancamentosObtidos = lancamentoService.obterTodosLancamentos(usuarioPadrao.getLogin());

        //Assert
        Assertions.assertEquals(2, lancamentosObtidos.size());
    }

    @Test
    void deveObterLancamentoPorId() {

        //Arrange
        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).usuario(usuarioPadrao).build();

        String nomeCategoriaAlimentacao = "Alimentacao";
        Categoria categoriaAlimentacao = Categoria.builder().nome(nomeCategoriaAlimentacao).usuario(usuarioPadrao).build();

        contaRepository.save(contaCorrente);
        categoriaRepository.save(categoriaAlimentacao);

        Lancamento lancamentoPizza = Lancamento.builder()
                .nome("Pizza")
                .valor(BigDecimal.TEN)
                .data(LocalDate.parse("03-05-2024", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .pago(false)
                .conta(contaCorrente)
                .categoria(categoriaAlimentacao)
                .build();

        lancamentoRepository.save(lancamentoPizza);

        List<Lancamento> lancamentos = lancamentoRepository.findAll();
        String idLancamento = lancamentos.getFirst().getId();

        // Act
        Lancamento lancamento = lancamentoService.obterLancamentoPorId(idLancamento, usuarioPadrao.getLogin());

        //Assert
        Assertions.assertEquals(idLancamento, lancamento.getId());
        Assertions.assertEquals("Pizza", lancamento.getNome());
    }

    @Test
    void deveCriarLancamento() {

        //Arrange
        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).usuario(usuarioPadrao).build();

        String nomeCategoriaAlimentacao = "Alimentacao";
        Categoria categoriaAlimentacao = Categoria.builder().nome(nomeCategoriaAlimentacao).usuario(usuarioPadrao).build();

        contaRepository.save(contaCorrente);
        categoriaRepository.save(categoriaAlimentacao);

        LancamentoRequestDTO novoLancamento = new LancamentoRequestDTO(null, "Pizza",
                contaCorrente.getId(), categoriaAlimentacao.getId(),
                "03-05-2024", BigDecimal.TEN, false);

        // Act
        Lancamento lancamentoCriado = lancamentoService.criarLancamento(novoLancamento, usuarioPadrao.getLogin());

        //Assert
        Assertions.assertNotNull(lancamentoCriado.getId());
        Assertions.assertEquals("Pizza", lancamentoCriado.getNome());
    }

    @Test
    void deveAtualizarLancamento() {

        //Arrange
        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).usuario(usuarioPadrao).build();

        String nomeCategoriaAlimentacao = "Alimentacao";
        Categoria categoriaAlimentacao = Categoria.builder().nome(nomeCategoriaAlimentacao).usuario(usuarioPadrao).build();

        contaRepository.save(contaCorrente);
        categoriaRepository.save(categoriaAlimentacao);

        Lancamento lancamentoPizza = Lancamento.builder()
                .nome("Pizza")
                .valor(BigDecimal.TEN)
                .data(LocalDate.parse("03-05-2024", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .pago(false)
                .conta(contaCorrente)
                .categoria(categoriaAlimentacao)
                .build();

        lancamentoRepository.save(lancamentoPizza);

        List<Lancamento> lancamentos = lancamentoRepository.findAll();
        String idLancamento = lancamentos.getFirst().getId();

        LancamentoRequestDTO lancamentoAlterado = new LancamentoRequestDTO(null,
                "Pastel",
                contaCorrente.getId(),
                categoriaAlimentacao.getId(),
                "23-10-2024", BigDecimal.TWO,
                true);

        // Act
        Lancamento lancamentoAtualizado = lancamentoService.atualizarLancamento(idLancamento, lancamentoAlterado, usuarioPadrao.getLogin());

        //Assert
        Assertions.assertEquals(idLancamento, lancamentoAtualizado.getId());
        Assertions.assertEquals("Pastel", lancamentoAtualizado.getNome());
        Assertions.assertEquals(BigDecimal.TWO, lancamentoAtualizado.getValor());
        Assertions.assertEquals(LocalDate.parse("23-10-2024", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")), lancamentoAtualizado.getData());
        Assertions.assertTrue(lancamentoAtualizado.isPago());
    }

    @Test
    void deveDeletarLancamento() {

        //Arrange
        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).usuario(usuarioPadrao).build();

        String nomeCategoriaAlimentacao = "Alimentacao";
        Categoria categoriaAlimentacao = Categoria.builder().nome(nomeCategoriaAlimentacao).usuario(usuarioPadrao).build();

        contaRepository.save(contaCorrente);
        categoriaRepository.save(categoriaAlimentacao);

        Lancamento lancamentoPizza = Lancamento.builder()
                .nome("Pizza")
                .valor(BigDecimal.TEN)
                .data(LocalDate.parse("03-05-2024", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .pago(false)
                .conta(contaCorrente)
                .categoria(categoriaAlimentacao)
                .build();

        lancamentoRepository.save(lancamentoPizza);

        List<Lancamento> lancamentosAntesExclusao = lancamentoRepository.findAll();
        String idLancamento = lancamentosAntesExclusao.getFirst().getId();

        //Assert
        //Verifica que a lancamento foi incluida
        Assertions.assertEquals(1, lancamentosAntesExclusao.size());

        // Act
        lancamentoService.deletarLancamento(idLancamento, usuarioPadrao.getLogin());

        //Assert
        List<Lancamento> lancamentosDepoisExclusao = lancamentoRepository.findAll();
        Assertions.assertEquals(0, lancamentosDepoisExclusao.size());
    }

    @Test
    void deveAtualizarLancamentoComoPago() {

        //Arrange
        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).usuario(usuarioPadrao).build();

        String nomeCategoriaAlimentacao = "Alimentacao";
        Categoria categoriaAlimentacao = Categoria.builder().nome(nomeCategoriaAlimentacao).usuario(usuarioPadrao).build();

        contaRepository.save(contaCorrente);
        categoriaRepository.save(categoriaAlimentacao);

        Lancamento lancamentoPizza = Lancamento.builder()
                .nome("Pizza")
                .valor(BigDecimal.TEN)
                .data(LocalDate.parse("03-05-2024", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .pago(false)
                .conta(contaCorrente)
                .categoria(categoriaAlimentacao)
                .build();

        lancamentoRepository.save(lancamentoPizza);

        List<Lancamento> lancamentos = lancamentoRepository.findAll();
        String idLancamento = lancamentos.getFirst().getId();

        // Act
        Lancamento lancamentoAtualizado = lancamentoService.atualizarLancamentoComoPago(idLancamento, usuarioPadrao.getLogin());

        //Assert
        Assertions.assertEquals(idLancamento, lancamentoAtualizado.getId());
        Assertions.assertTrue(lancamentoAtualizado.isPago());
    }

    @Test
    void deveAtualizarLancamentoComoNaoPago() {

        //Arrange
        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).usuario(usuarioPadrao).build();

        String nomeCategoriaAlimentacao = "Alimentacao";
        Categoria categoriaAlimentacao = Categoria.builder().nome(nomeCategoriaAlimentacao).usuario(usuarioPadrao).build();

        contaRepository.save(contaCorrente);
        categoriaRepository.save(categoriaAlimentacao);

        Lancamento lancamentoPizza = Lancamento.builder()
                .nome("Pizza")
                .valor(BigDecimal.TEN)
                .data(LocalDate.parse("03-05-2024", java.time.format.DateTimeFormatter.ofPattern("dd-MM-yyyy")))
                .pago(true)
                .conta(contaCorrente)
                .categoria(categoriaAlimentacao)
                .build();

        lancamentoRepository.save(lancamentoPizza);

        List<Lancamento> lancamentos = lancamentoRepository.findAll();
        String idLancamento = lancamentos.getFirst().getId();

        // Act
        Lancamento lancamentoAtualizado = lancamentoService.atualizarLancamentoComoNaoPago(idLancamento, usuarioPadrao.getLogin());

        //Assert
        Assertions.assertEquals(idLancamento, lancamentoAtualizado.getId());
        Assertions.assertFalse(lancamentoAtualizado.isPago());
    }
}