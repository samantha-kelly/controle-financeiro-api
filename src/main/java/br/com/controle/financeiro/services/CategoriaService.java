package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.CategoriaRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.user.Usuario;
import br.com.controle.financeiro.repositories.CategoriaRepository;
import br.com.controle.financeiro.repositories.UsuarioRepository;
import br.com.controle.financeiro.services.exception.NegocioException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    private final UsuarioRepository usuarioRepository;

    private final ValidacaoDadosUsuarioService validacaoDadosUsuarioService;

    public CategoriaService(CategoriaRepository categoriaRepository, UsuarioRepository usuarioRepository, ValidacaoDadosUsuarioService validacaoDadosUsuarioService) {

        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
        this.validacaoDadosUsuarioService = validacaoDadosUsuarioService;
    }

    public List<Categoria> obterTodasCategorias(String userLogin) {
        return categoriaRepository.findAllCategoriasByUsuarioLogin(userLogin);
    }

    public Categoria obterCategoriaPorId(String idCategoria, String userLogin) {

        validacaoDadosUsuarioService.validarCategoriaDoUsuarioLogado(idCategoria, userLogin);

        return categoriaRepository.findById(idCategoria).orElseThrow();
    }

    @Transactional
    public Categoria criarCategoria(CategoriaRequestDTO categoriaDTO, String userLogin) {

        List<Categoria> categorias = categoriaRepository.findAllCategoriasByUsuarioLogin(userLogin);
        validarCategoriaComMesmoNome(categoriaDTO.nome(), categorias);

        UserDetails usuario = usuarioRepository.findByLogin(userLogin);
        Categoria c = new Categoria();
        c.setNome(categoriaDTO.nome());
        c.setUsuario((Usuario) usuario);

        return categoriaRepository.save(c);
    }

    @Transactional
    public Categoria atualizarCategoria(String idCategoria, CategoriaRequestDTO categoriaDTO, String userLogin) {

        validacaoDadosUsuarioService.validarCategoriaDoUsuarioLogado(idCategoria, userLogin);

        List<Categoria> categorias = categoriaRepository.findAllCategoriasByUsuarioLogin(userLogin);
        validarCategoriaComMesmoNome(categoriaDTO.nome(), categorias);

        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow();
        categoria.setNome(categoriaDTO.nome());

        return categoriaRepository.save(categoria);
    }

    protected void validarCategoriaComMesmoNome(String nomeCategoria, List<Categoria> categorias) {

        boolean possuiCategoriaComMesmoNome = categorias.stream()
                .anyMatch(c -> c.getNome().equals(nomeCategoria));
        if (possuiCategoriaComMesmoNome) {
            throw new NegocioException("Categoria com nome informado já existente.");
        }
    }

    @Transactional
    public void deletarCategoria(String idCategoria, String userLogin) {

        validacaoDadosUsuarioService.validarCategoriaDoUsuarioLogado(idCategoria, userLogin);

        Categoria categoria = categoriaRepository.findById(idCategoria).orElseThrow();
        if (categoria.getLancamentos() != null && !categoria.getLancamentos().isEmpty()) {
            throw new NegocioException("Não é possível remover essa categoria, pois ela possui lançamentos associados.");
        }
        categoriaRepository.deleteById(idCategoria);
    }
}
