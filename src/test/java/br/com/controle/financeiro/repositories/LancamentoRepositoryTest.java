package br.com.controle.financeiro.repositories;

import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.Lancamento;
import br.com.controle.financeiro.domain.user.UserRole;
import br.com.controle.financeiro.domain.user.Usuario;
import br.com.controle.financeiro.repositories.dto.LancamentoCompletoDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class LancamentoRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private LancamentoRepository lancamentoRepository;

    @Test
    public void deveCriarLancamento() {

        // Arrange
        var usuario = Usuario.builder().login("usuario").password("123456").role(UserRole.ADMIN).build();
        var contaCorrente = Conta.builder().nome("Conta Corrente").usuario(usuario).build();
        var categoriaAlimentacao = Categoria.builder().nome("Alimentação").usuario(usuario).build();
        var lancamento = Lancamento.builder()
                .conta(contaCorrente).nome("Pipoca")
                .categoria(categoriaAlimentacao)
                .valor(BigDecimal.valueOf(123.45))
                .data(LocalDate.now())
                .build();

        usuarioRepository.save(usuario);
        contaRepository.save(contaCorrente);
        categoriaRepository.save(categoriaAlimentacao);
        lancamentoRepository.save(lancamento);

        // Act
        lancamentoRepository.save(lancamento);

        // Assert
        List<Lancamento> lancamentos = lancamentoRepository.findAll();
        Assertions.assertEquals(1, lancamentos.size());
    }

    @Test
    public void deveObterLancamentosDoUsuario() {

        // Arrange
        var usuario = Usuario.builder().login("usuario").password("123456").role(UserRole.ADMIN).build();

        var contaCorrente = Conta.builder().nome("Conta Corrente").usuario(usuario).build();
        var contaCartao = Conta.builder().nome("Cartão Crédito").usuario(usuario).build();

        var categoriaAlimentacao = Categoria.builder().nome("Alimentação").usuario(usuario).build();
        var categoriaTranporte = Categoria.builder().nome("Transporte").usuario(usuario).build();

        var lancamento1 = Lancamento.builder()
                .conta(contaCorrente).nome("Pipoca")
                .categoria(categoriaAlimentacao)
                .valor(BigDecimal.valueOf(123.45))
                .data(LocalDate.now())
                .build();
        var lancamento2 = Lancamento.builder()
                .conta(contaCorrente).nome("Uber")
                .categoria(categoriaTranporte)
                .valor(BigDecimal.valueOf(321.54))
                .data(LocalDate.now())
                .build();

        usuarioRepository.save(usuario);
        contaRepository.save(contaCorrente);
        contaRepository.save(contaCartao);
        categoriaRepository.save(categoriaAlimentacao);
        categoriaRepository.save(categoriaTranporte);
        lancamentoRepository.save(lancamento1);
        lancamentoRepository.save(lancamento2);

        // Act
        List<Lancamento> lancamentosUsuario = lancamentoRepository.findLancamentosByUsuario(usuario.getLogin());

        // Assert
        Assertions.assertEquals(2, lancamentosUsuario.size());
    }

    @Test
    public void deveObterLancamentosCompletosDoUsuario() {

        // Arrange
        var usuario = Usuario.builder().login("usuario").password("123456").role(UserRole.ADMIN).build();

        var contaCorrente = Conta.builder().nome("Conta Corrente").usuario(usuario).build();
        var categoriaAlimentacao = Categoria.builder().nome("Alimentação").usuario(usuario).build();

        LocalDate dataLancamento = LocalDate.now();
        var lancamento1 = Lancamento.builder()
                .conta(contaCorrente).nome("Pipoca")
                .categoria(categoriaAlimentacao)
                .valor(BigDecimal.valueOf(123.45))
                .data(dataLancamento)
                .pago(true)
                .build();

        usuarioRepository.save(usuario);
        contaRepository.save(contaCorrente);
        categoriaRepository.save(categoriaAlimentacao);
        lancamentoRepository.save(lancamento1);

        // Act
        List<LancamentoCompletoDTO> lancamentosCompletosUsuario = lancamentoRepository.findLancamentosCompletosByUsuario(usuario.getLogin());

        // Assert
        Assertions.assertEquals(1, lancamentosCompletosUsuario.size());

        Assertions.assertNotNull(lancamentosCompletosUsuario.get(0).getId());
        Assertions.assertEquals("Conta Corrente", lancamentosCompletosUsuario.get(0).getNomeConta());
        Assertions.assertEquals("Alimentação", lancamentosCompletosUsuario.get(0).getNomeCategoria());
        Assertions.assertEquals(BigDecimal.valueOf(123.45), lancamentosCompletosUsuario.get(0).getValor());
        Assertions.assertEquals(dataLancamento, lancamentosCompletosUsuario.get(0).getData());
        Assertions.assertTrue(lancamentosCompletosUsuario.get(0).isPago());
    }
}