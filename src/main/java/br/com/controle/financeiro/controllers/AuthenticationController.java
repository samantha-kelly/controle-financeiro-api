package br.com.controle.financeiro.controllers;

import br.com.controle.financeiro.domain.user.AuthenticationDTO;
import br.com.controle.financeiro.domain.user.LoginResponseDTO;
import br.com.controle.financeiro.domain.user.RegisterDTO;
import br.com.controle.financeiro.domain.user.Usuario;
import br.com.controle.financeiro.infra.security.TokenService;
import br.com.controle.financeiro.repositories.UsuarioRepository;
import br.com.controle.financeiro.services.exception.NegocioException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("auth")
@Tag(name = "Endpoints de Controle de Autenticação")
public class AuthenticationController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository repository;
    @Autowired
    private TokenService tokenService;

    @Operation(
            summary = "Permite o cadastramento de um usuário na aplicação informando um login, uma senha e o tipo do papel do usuário (USER ou ADMIN).",
            description = "Permite o cadastramento de um usuário na aplicação informando um login, uma senha e o tipo do papel do usuário (USER ou ADMIN).",
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Não Autorizado / Token Inválido",
                            responseCode = "403"
                    ),
            }
    )
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data) {
        if (this.repository.findByLogin(data.login()) != null) {
        	throw new NegocioException("Usuário já existente.");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(data.password());
        Usuario newUsuario = new Usuario(data.login(), encryptedPassword, data.role());

        this.repository.save(newUsuario);

        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Permite o usuário se logar na aplicação informando um login e senha.",
            description = "Permite o usuário se logar na aplicação informando um login e senha. Ao se logar com sucesso o sistema retorna uma token JWT que pode ser utilizado para chamar as demais funções da API.",
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Não Autorizado / Token Inválido",
                            responseCode = "403"
                    ),
            }
    )
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
        var auth = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((Usuario) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }


}
