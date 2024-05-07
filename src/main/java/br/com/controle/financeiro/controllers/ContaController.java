package br.com.controle.financeiro.controllers;

import br.com.controle.financeiro.controllers.dto.ContaRequestDTO;
import br.com.controle.financeiro.controllers.dto.ContaResponseDTO;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.services.ContaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contas")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Contas")
public class ContaController {

    private final ContaService contaService;

    public ContaController(ContaService contaService) {
        this.contaService = contaService;
    }

    @Operation(
            summary = "Retorna todas as contas.",
            description = "Retorna todas as contas.",
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Não Autorizado / Token Inválido",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = Void.class))
                    ),
            }
    )
    @GetMapping
    public ResponseEntity<List<ContaResponseDTO>> obterTodasContas(Authentication authentication) {
        return ResponseEntity.ok(contaService.obterTodasContas(authentication.getName())
                .stream().map(ContaResponseDTO::new).toList());
    }

    @Operation(
            summary = "Retorna uma conta específica do usuário.",
            description = "Retorna uma conta específica do usuário.",
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Não Autorizado / Token Inválido",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = Void.class))
                    ),
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<Conta> obterContaPorId(@PathVariable String id, Authentication authentication) {
        return ResponseEntity.ok(contaService.obterContaPorId(id, authentication.getName()));
    }


    @Operation(
            summary = "Cadastra uma conta.",
            description = "Cadastra uma conta.",
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "201"
                    ),
                    @ApiResponse(
                            description = "Não Autorizado / Token Inválido",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = Void.class))
                    ),
            }
    )
    @PostMapping
    public ResponseEntity<ContaResponseDTO> createConta(@RequestBody @Valid ContaRequestDTO conta, Authentication authentication) {

        Conta contaCriada = contaService.criarConta(conta, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ContaResponseDTO(contaCriada));
    }


    @Operation(
            summary = "Atualiza dados de uma conta.",
            description = "Atualiza dados de uma conta.",
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Não Autorizado / Token Inválido",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = Void.class))
                    ),
            }
    )
    @PutMapping("/{id}")
    public ResponseEntity<ContaResponseDTO> updateConta(@PathVariable String id, @RequestBody @Valid ContaRequestDTO contaRequestDTO, Authentication authentication) {
        Conta contaAtualizada = contaService.atualizarConta(id, contaRequestDTO, authentication.getName());
        return ResponseEntity.ok(new ContaResponseDTO(contaAtualizada));
    }


    @Operation(
            summary = "Remove uma conta.",
            description = "Remove uma conta.",
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "204"
                    ),
                    @ApiResponse(
                            description = "Não Autorizado / Token Inválido",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = Void.class))
                    ),
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConta(@PathVariable String id, Authentication authentication) {
        contaService.deletarConta(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
