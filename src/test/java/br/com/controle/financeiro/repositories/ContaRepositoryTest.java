package br.com.controle.financeiro.repositories;

import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.user.UserRole;
import br.com.controle.financeiro.domain.user.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ContaRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Test
    public void deveObterAsContasDoUsuario() {

        // Arrange
        String userLogin = "joao@teste.com";
        String userPassword = "senha_do_joao";
        var user = Usuario.builder().login(userLogin).password(userPassword).role(UserRole.ADMIN).build();

        String nomeContaCorrente = "Conta Corrente";
        Conta contaCorrente = Conta.builder().nome(nomeContaCorrente).usuario(user).build();
        String nomeCartaoCredito = "Cartão Crédito";
        Conta contaCartaoCredito = Conta.builder().nome(nomeCartaoCredito).usuario(user).build();

        usuarioRepository.save(user);
        contaRepository.save(contaCorrente);
        contaRepository.save(contaCartaoCredito);

        // Act
        List<Conta> contasUsuario = contaRepository.findAllContasByUsuarioLogin(userLogin);

        // Assert
        Assertions.assertEquals(2, contasUsuario.size());
    }
}