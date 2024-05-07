package br.com.controle.financeiro.controllers;

import br.com.controle.financeiro.controllers.dto.CategoriaRequestDTO;
import br.com.controle.financeiro.controllers.dto.CategoriaResponseDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.services.CategoriaService;
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
@RequestMapping("/api/categorias")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Categorias")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    @Operation(
            summary = "Retorna todas as categorias.",
            description = "Retorna todas as categorias.",
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
    public ResponseEntity<List<CategoriaResponseDTO>> obterTodasCategorias(Authentication authentication) {
        return ResponseEntity.ok(categoriaService.obterTodasCategorias(authentication.getName())
                .stream().map(CategoriaResponseDTO::new).toList());
    }

    @Operation(
            summary = "Retorna uma categoria específica do usuário.",
            description = "Retorna uma categoria específica do usuário.",
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
    public ResponseEntity<Categoria> obterCategoriaPorId(@PathVariable String id, Authentication authentication) {
        return ResponseEntity.ok(categoriaService.obterCategoriaPorId(id, authentication.getName()));
    }

    @Operation(
            summary = "Cadastra uma categoria.",
            description = "Cadastra uma categoria.",
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
    public ResponseEntity<CategoriaResponseDTO> createCategoria(@RequestBody @Valid CategoriaRequestDTO categoria, Authentication authentication) {

        Categoria categoriaCriada = categoriaService.criarCategoria(categoria, authentication.getName());

        return ResponseEntity.status(HttpStatus.CREATED).body(new CategoriaResponseDTO(categoriaCriada));
    }

    @Operation(
            summary = "Atualiza dados de uma categoria.",
            description = "Atualiza dados de uma categoria.",
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
    public ResponseEntity<CategoriaResponseDTO> updateCategoria(@PathVariable String id, @RequestBody @Valid CategoriaRequestDTO categoriaRequestDTO, Authentication authentication) {
        Categoria categoriaAtualizada = categoriaService.atualizarCategoria(id, categoriaRequestDTO, authentication.getName());
        return ResponseEntity.ok(new CategoriaResponseDTO(categoriaAtualizada));
    }


    @Operation(
            summary = "Remove uma categoria.",
            description = "Remove uma categoria.",
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
    public ResponseEntity<Void> deleteCategoria(@PathVariable String id, Authentication authentication) {
        categoriaService.deletarCategoria(id, authentication.getName());
        return ResponseEntity.noContent().build();
    }
}
