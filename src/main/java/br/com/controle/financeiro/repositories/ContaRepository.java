package br.com.controle.financeiro.repositories;

import br.com.controle.financeiro.domain.Conta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContaRepository extends JpaRepository<Conta, String> {

    List<Conta> findAllContasByUsuarioLogin(String userId);
}
