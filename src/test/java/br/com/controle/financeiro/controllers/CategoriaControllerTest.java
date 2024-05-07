package br.com.controle.financeiro.controllers;

import br.com.controle.financeiro.controllers.config.ControllerUserTestConfig;
import br.com.controle.financeiro.controllers.config.CustomUserDetailsService;
import br.com.controle.financeiro.controllers.dto.CategoriaRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.services.CategoriaService;
import br.com.controle.financeiro.services.exception.NegocioException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(CategoriaController.class)
@Import(CategoriaController.class)
@ContextConfiguration(classes = {GlobalExceptionHandler.class, NegocioException.class, ControllerUserTestConfig.class, CustomUserDetailsService.class})
class CategoriaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CategoriaService categoriaService;

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveObterTodasAsCategorias() throws Exception {

        // Arrange
        Categoria categoriaTransporte = Categoria.builder().id("1234").nome("Transporte").build();
        Categoria categoriaAlimentacao = Categoria.builder().id("5678").nome("Alimentação").build();
        Mockito.when(categoriaService.obterTodasCategorias("usuarioTeste"))
                .thenReturn(List.of(categoriaTransporte, categoriaAlimentacao));


        mockMvc.perform(
                        // Act
                        get("/api/categorias")
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray()) // Verifica se o retorno é uma lista
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1234"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nome").value("Transporte"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("5678"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].nome").value("Alimentação"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveObterCategoriaPeloId() throws Exception {

        // Arrange
        String idCategoria = "1234";
        Categoria categoriaTransporte = Categoria.builder().id(idCategoria).nome("Transporte").build();
        Mockito.when(categoriaService.obterCategoriaPorId(idCategoria, "usuarioTeste"))
                .thenReturn(categoriaTransporte);

        mockMvc.perform(
                        // Act
                        get("/api/categorias/" + idCategoria)
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idCategoria))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Transporte"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveInserirUmaCategoria() throws Exception {

        // Arrange
        CategoriaRequestDTO categoriaRequestDTO = new CategoriaRequestDTO(null, "Transporte");

        Categoria categoria = Categoria.builder().id("1234").nome("Transporte").build();
        Mockito.when(categoriaService.criarCategoria(ArgumentMatchers.any(CategoriaRequestDTO.class), ArgumentMatchers.any(String.class)))
                .thenReturn(categoria);

        // Converte o objeto para JSON
        String jsonContent = objectMapper.writeValueAsString(categoriaRequestDTO);

        mockMvc.perform(
                        // Act
                        post("/api/categorias")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent)
                )
                // Assert
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1234"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Transporte"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void naoDeveInserirUmaCategoriaComMesmoNome() throws Exception {

        // Arrange
        Mockito.when(categoriaService.criarCategoria(ArgumentMatchers.any(CategoriaRequestDTO.class), ArgumentMatchers.any(String.class)))
                .thenThrow(new NegocioException("Categoria com nome informado já existente."));

        CategoriaRequestDTO categoriaRequestDTO = new CategoriaRequestDTO(null, "Transporte");
        // Converte o objeto para JSON
        String jsonContent = objectMapper.writeValueAsString(categoriaRequestDTO);

        String resposta = mockMvc.perform(
                        // Act
                        post("/api/categorias")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent)
                )
                // Assert
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertTrue(result.getResolvedException() instanceof NegocioException))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(resposta, "Categoria com nome informado já existente.");
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveAtualizarUmaCategoria() throws Exception {

        // Arrange
        String idCategoria = "1234";
        CategoriaRequestDTO categoriaRequestDTO = new CategoriaRequestDTO(idCategoria, "Nova Categoria");

        Categoria categoriaAtualizada = Categoria.builder().id("1234").nome("Nova Categoria").build();
        Mockito.when(categoriaService.atualizarCategoria(
                        idCategoria,
                        categoriaRequestDTO,
                        "usuarioTeste"))
                .thenReturn(categoriaAtualizada);

        // Converte o objeto para JSON
        String jsonContent = objectMapper.writeValueAsString(categoriaRequestDTO);

        mockMvc.perform(
                        // Act
                        put("/api/categorias/" + idCategoria)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent)
                )
                // Assert
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1234"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Nova Categoria"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void naoDeveAtualizarUmaCategoriaComMesmoNome() throws Exception {

        // Arrange
        String idCategoria = "1234";
        CategoriaRequestDTO categoriaRequestDTO = new CategoriaRequestDTO(idCategoria, "Nova Categoria");

        Mockito.when(categoriaService.atualizarCategoria(
                        idCategoria,
                        categoriaRequestDTO,
                        "usuarioTeste"))
                .thenThrow(new NegocioException("Categoria com nome informado já existente."));

        // Converte o objeto para JSON
        String jsonContent = objectMapper.writeValueAsString(categoriaRequestDTO);

        String resposta = mockMvc.perform(
                        // Act
                        put("/api/categorias/" + idCategoria)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .accept(MediaType.APPLICATION_JSON_UTF8_VALUE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent)
                )
                // Assert
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> Assertions.assertInstanceOf(NegocioException.class, result.getResolvedException()))
                .andReturn()
                .getResponse()
                .getContentAsString();

        Assertions.assertEquals(resposta, "Categoria com nome informado já existente.");
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveDeletarUmaCategoria() throws Exception {

        // Arrange
        String idCategoria = "1234";
        Mockito.doNothing().when(categoriaService).deletarCategoria(idCategoria, "usuarioTeste");

        mockMvc.perform(
                        // Act
                        delete("/api/categorias/" + idCategoria)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                )
                // Assert
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}