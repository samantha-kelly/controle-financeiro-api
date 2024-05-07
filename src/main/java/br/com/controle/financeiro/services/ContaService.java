package br.com.controle.financeiro.services;

import br.com.controle.financeiro.controllers.dto.ContaRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.user.Usuario;
import br.com.controle.financeiro.repositories.ContaRepository;
import br.com.controle.financeiro.repositories.UsuarioRepository;
import br.com.controle.financeiro.services.exception.NegocioException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContaService {

    private final ContaRepository contaRepository;

    private final UsuarioRepository usuarioRepository;

    private final ValidacaoDadosUsuarioService validacaoDadosUsuarioService;

    public ContaService(ContaRepository contaRepository, UsuarioRepository usuarioRepository, ValidacaoDadosUsuarioService validacaoDadosUsuarioService) {

        this.contaRepository = contaRepository;
        this.usuarioRepository = usuarioRepository;
        this.validacaoDadosUsuarioService = validacaoDadosUsuarioService;
    }

    public List<Conta> obterTodasContas(String userLogin) {
        return contaRepository.findAllContasByUsuarioLogin(userLogin);
    }

    public Conta obterContaPorId(String idConta, String userLogin) {

        validacaoDadosUsuarioService.validarContaDoUsuarioLogado(idConta, userLogin);
        return contaRepository.findById(idConta).orElseThrow();
    }

    @Transactional
    public Conta criarConta(ContaRequestDTO contaDTO, String userLogin) {

        List<Conta> contas = contaRepository.findAllContasByUsuarioLogin(userLogin);
        validarContaComMesmoNome(contaDTO.nome(), contas);

        UserDetails usuario = usuarioRepository.findByLogin(userLogin);

        Conta c = new Conta();
        c.setNome(contaDTO.nome());
        c.setUsuario((Usuario) usuario);

        return contaRepository.save(c);
    }

    @Transactional
    public Conta atualizarConta(String idConta, ContaRequestDTO contaDTO, String userLogin) {

        validacaoDadosUsuarioService.validarContaDoUsuarioLogado(idConta, userLogin);

        List<Conta> contas = contaRepository.findAllContasByUsuarioLogin(userLogin);
        validarContaComMesmoNome(contaDTO.nome(), contas);

        Conta conta = contaRepository.findById(idConta).orElseThrow();
        conta.setNome(contaDTO.nome());

        return contaRepository.save(conta);
    }

    protected void validarContaComMesmoNome(String nomeConta, List<Conta> contas) {

        boolean possuiContaComMesmoNome = contas.stream()
                .anyMatch(c -> c.getNome().equals(nomeConta));
        if (possuiContaComMesmoNome) {
            throw new NegocioException("Conta com nome informado já existente.");
        }
    }

    @Transactional
    public void deletarConta(String idConta, String userLogin) {

        validacaoDadosUsuarioService.validarContaDoUsuarioLogado(idConta, userLogin);

        Conta conta = contaRepository.findById(idConta).orElseThrow();
        if (conta.getLancamentos() != null && !conta.getLancamentos().isEmpty()) {
            throw new NegocioException("Não é possível remover essa conta, pois ela possui lançamentos associados.");
        }
        contaRepository.deleteById(idConta);
    }
}
