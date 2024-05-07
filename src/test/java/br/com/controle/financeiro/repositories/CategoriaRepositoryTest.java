package br.com.controle.financeiro.repositories;

import br.com.controle.financeiro.domain.Categoria;
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
class CategoriaRepositoryTest {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Test
    public void deveObterAsCategoriasDoUsuario() {

        // Arrange
        String userLogin = "joao@teste.com";
        String userPassword = "senha_do_joao";
        var user = Usuario.builder().login(userLogin).password(userPassword).role(UserRole.ADMIN).build();

        String nomeCategoriaCorrente = "Categoria Corrente";
        Categoria categoriaCorrente = Categoria.builder().nome(nomeCategoriaCorrente).usuario(user).build();
        String nomeCartaoCredito = "Cartão Crédito";
        Categoria categoriaCartaoCredito = Categoria.builder().nome(nomeCartaoCredito).usuario(user).build();

        usuarioRepository.save(user);
        categoriaRepository.save(categoriaCorrente);
        categoriaRepository.save(categoriaCartaoCredito);

        // Act
        List<Categoria> categoriasUsuario = categoriaRepository.findAllCategoriasByUsuarioLogin(userLogin);

        // Assert
        Assertions.assertEquals(2, categoriasUsuario.size());
    }

}