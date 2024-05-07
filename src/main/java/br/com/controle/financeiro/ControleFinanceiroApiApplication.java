package br.com.controle.financeiro;

import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.Lancamento;
import br.com.controle.financeiro.domain.user.UserRole;
import br.com.controle.financeiro.domain.user.Usuario;
import br.com.controle.financeiro.repositories.CategoriaRepository;
import br.com.controle.financeiro.repositories.ContaRepository;
import br.com.controle.financeiro.repositories.LancamentoRepository;
import br.com.controle.financeiro.repositories.UsuarioRepository;
import br.com.controle.financeiro.services.ContaService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootApplication
public class ControleFinanceiroApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControleFinanceiroApiApplication.class, args);
    }

//    @Bean
    public CommandLineRunner commandLineRunner(UsuarioRepository usuarioRepository,
                                               ContaService contaService,
                                               ContaRepository contaRepository,
                                               CategoriaRepository categoriaRepository,
                                               LancamentoRepository lancamentoRepository) {
        return args -> {
            var user = Usuario.builder().login("usuario").password("123456").role(UserRole.ADMIN).build();

            var conta = Conta.builder().nome("Conta Conjunta").usuario(user).build();
            var contaCartao = Conta.builder().nome("Cartão Crédito").usuario(user).build();

            var categoriaAlimentacao = Categoria.builder().nome("Alimentação").usuario(user).build();
            var categoriaTranporte = Categoria.builder().nome("Transporte").usuario(user).build();

            var lancamento = Lancamento.builder()
                    .conta(conta).nome("Pipoca")
                    .categoria(categoriaAlimentacao)
                    .valor(BigDecimal.valueOf(123.45))
                    .data(LocalDate.now())
                    .build();
            var lancamento2 = Lancamento.builder()
                    .conta(conta).nome("Uber")
                    .categoria(categoriaTranporte)
                    .valor(BigDecimal.valueOf(321.54))
                    .data(LocalDate.now())
                    .build();

            usuarioRepository.save(user);
//            contaService.createConta(conta);
//            contaService.createConta(contaCartao);
            categoriaRepository.save(categoriaAlimentacao);
            categoriaRepository.save(categoriaTranporte);
            lancamentoRepository.save(lancamento);
            lancamentoRepository.save(lancamento2);

//            List<Conta> allContas = contaService.getAllContas();
//
//            String idContaConjunta = null;
//            String idContaCartao = null;
//
//            System.out.println("Contas Antes");
//            for (Conta c : allContas) {
//                System.out.println("Conta = " + c.getNome());
//
//                if (c.getNome().equals("Conta Conjunta")) {
//                    idContaConjunta = c.getId();
//                }
//
//                if (c.getNome().equals("Cartão Crédito")) {
//                    idContaCartao = c.getId();
//                }
//            }
//
//            contaService.deleteConta(idContaCartao);
//            contaService.updateConta(idContaConjunta, Conta.builder().nome("Conta Conjunta 2").user(user).build());
//
//            System.out.println("Contas Depois");
//            allContas = contaService.getAllContas();
//            for (Conta c : allContas) {
//                System.out.println("Conta = " + c.getNome());
//            }
        };
    }

}
