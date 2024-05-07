package br.com.controle.financeiro.controllers;

import br.com.controle.financeiro.controllers.dto.LancamentoCompletoResponseDTO;
import br.com.controle.financeiro.controllers.dto.LancamentoRequestDTO;
import br.com.controle.financeiro.controllers.dto.LancamentoResponseDTO;
import br.com.controle.financeiro.domain.Lancamento;
import br.com.controle.financeiro.services.LancamentoService;
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
@RequestMapping("/api/lancamentos")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Lançamentos")
public class LancamentoController {

    private final LancamentoService lancamentoService;

    public LancamentoController(LancamentoService lancamentoService) {
        this.lancamentoService = lancamentoService;
    }

    @Operation(
            summary = "Retorna todos os lancamentos.",
            description = "Retorna todos os lancamentos.",
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
    public ResponseEntity<List<LancamentoResponseDTO>> obterTodosLancamentos(Authentication authentication) {
        return ResponseEntity.ok(lancamentoService.obterTodosLancamentos(authentication.getName())
                .stream().map(LancamentoResponseDTO::new).toList());
    }

    @Operation(
            summary = "Retorna todos os lancamentos, de forma mais detalhada.",
            description = "Retorna todos os lancamentos, de forma mais detalhada.",
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Não Autorizado / Token Inválido",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = Void.class))
                    )
            }
    )
    @GetMapping("/completos")
    public ResponseEntity<List<LancamentoCompletoResponseDTO>> obterTodosLancamentosCompletos(Authentication authentication) {
        return ResponseEntity.ok(lancamentoService.obterTodosLancamentosCompletos(authentication.getName())
                .stream().map(LancamentoCompletoResponseDTO::new).toList()
        );
    }

    @Operation(
            summary = "Retorna um lançamento específico do usuário.",
            description = "Retorna um lançamento específico do usuário.",
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
                    @ApiResponse(
                            description = "Parâmetros da requisição inválidos",
                            responseCode = "400"
                    )
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<LancamentoResponseDTO> obterLancamentoPorId(@PathVariable String id, Authentication authentication) {
        return ResponseEntity.ok(new LancamentoResponseDTO(lancamentoService.obterLancamentoPorId(id, authentication.getName())));
    }

    @Operation(
            summary = "Retorna os lancamentos da competência informada.",
            description = "Retorna os lancamentos da competência informada (formato AAAAMM). Ex.: 202405",
            responses = {
                    @ApiResponse(
                            description = "Sucesso",
                            responseCode = "200"
                    ),
                    @ApiResponse(
                            description = "Não Autorizado / Token Inválido",
                            responseCode = "403",
                            content = @Content(schema = @Schema(implementation = Void.class))
                    )
            }
    )
    @GetMapping("/competencia/{competencia}")
    public ResponseEntity<List<LancamentoResponseDTO>> obterLancamentoPorCompetencia(@PathVariable Integer competencia, Authentication authentication) {
        return ResponseEntity.ok(
                lancamentoService.obterLancamentosPorCompetencia(competencia, authentication.getName())
                        .stream().map(LancamentoResponseDTO::new).toList()
        );
    }


    @Operation(
            summary = "Cadastra um lancamento.",
            description = "Cadastra um lancamento.",
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
    public ResponseEntity<LancamentoResponseDTO> createLancamento(@RequestBody @Valid LancamentoRequestDTO lancamento, Authentication authentication) {

        Lancamento lancamentoCriada = lancamentoService.criarLancamento(lancamento, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(new LancamentoResponseDTO(lancamentoCriada));
    }

    @Operation(
            summary = "Atualiza dados de um lancamento.",
            description = "Atualiza dados de um lancamento.",
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
    public ResponseEntity<LancamentoResponseDTO> updateLancamento(@PathVariable String id, @RequestBody @Valid LancamentoRequestDTO lancamentoRequestDTO, Authentication authentication) {
        Lancamento lancamentoAtualizada = lancamentoService.atualizarLancamento(id, lancamentoRequestDTO, authentication.getName());
        return ResponseEntity.ok(new LancamentoResponseDTO(lancamentoAtualizada));
    }

    @Operation(
            summary = "Atualiza um lancamento como PAGO.",
            description = "Atualiza um lancamento como PAGO.",
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
                    @ApiResponse(
                            description = "Erro caso tente pagar um lançamento que já está pago.",
                            responseCode = "400",
                            content = @Content(schema = @Schema(implementation = Void.class))
                    )
            }
    )
    @PutMapping("/{id}/pago")
    public ResponseEntity<LancamentoResponseDTO> atualizarLancamentoComoPago(@PathVariable String id, Authentication authentication) {
        Lancamento lancamentoAtualizada = lancamentoService.atualizarLancamentoComoPago(id, authentication.getName());
        return ResponseEntity.ok(new LancamentoResponseDTO(lancamentoAtualizada));
    }

    @Operation(
            summary = "Atualiza um lancamento como NÃO PAGO.",
            description = "Atualiza um lancamento como NÃO PAGO.",
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
                    @ApiResponse(
                            description = "Erro caso tente não pagar um lançamento que já está como não pago.",
                            responseCode = "400",
                            content = @Content(schema = @Schema(implementation = Void.class))
                    )
            }
    )
    @PutMapping("/{id}/nao-pago")
    public ResponseEntity<LancamentoResponseDTO> atualizarLancamentoComoNaoPago(@PathVariable String id, Authentication authentication) {
        Lancamento lancamentoAtualizada = lancamentoService.atualizarLancamentoComoNaoPago(id, authentication.getName());
        return ResponseEntity.ok(new LancamentoResponseDTO(lancamentoAtualizada));
    }

    @Operation(
            summary = "Remove um lancamento.",
            description = "Remove um lancamento.",
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
    public ResponseEntity<Void> deleteLancamento(@PathVariable String id, Authentication authentication) {
        lancamentoService.deletarLancamento(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
