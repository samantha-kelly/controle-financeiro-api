package br.com.controle.financeiro.repositories;

import br.com.controle.financeiro.domain.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaRepository extends JpaRepository<Categoria, String> {

    List<Categoria> findAllCategoriasByUsuarioLogin(String userId);
}
