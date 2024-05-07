package br.com.controle.financeiro.controllers;

import br.com.controle.financeiro.controllers.config.ControllerUserTestConfig;
import br.com.controle.financeiro.controllers.config.CustomUserDetailsService;
import br.com.controle.financeiro.controllers.dto.ContaRequestDTO;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.services.ContaService;
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

@WebMvcTest(ContaController.class)
@Import(ContaController.class)
@ContextConfiguration(classes = {GlobalExceptionHandler.class, NegocioException.class, ControllerUserTestConfig.class, CustomUserDetailsService.class})
class ContaControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ContaService contaService;

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveObterTodasAsContas() throws Exception {

        // Arrange
        Conta contaConjunta = Conta.builder().id("1234").nome("Conta Conjunta").build();
        Conta cartaoCredito = Conta.builder().id("5678").nome("Cartão de Crédito").build();
        Mockito.when(contaService.obterTodasContas("usuarioTeste"))
                .thenReturn(List.of(contaConjunta, cartaoCredito));


        mockMvc.perform(
                        // Act
                        get("/api/contas")
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray()) // Verifica se o retorno é uma lista
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("1234"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nome").value("Conta Conjunta"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("5678"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].nome").value("Cartão de Crédito"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveObterContaPeloId() throws Exception {

        // Arrange
        String idConta = "1234";
        Conta contaConjunta = Conta.builder().id(idConta).nome("Conta Conjunta").build();
        Mockito.when(contaService.obterContaPorId(idConta, "usuarioTeste"))
                .thenReturn(contaConjunta);

        mockMvc.perform(
                        // Act
                        get("/api/contas/" + idConta)
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(idConta))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Conta Conjunta"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveInserirUmaConta() throws Exception {

        // Arrange
        ContaRequestDTO contaRequestDTO = new ContaRequestDTO(null, "Conta Corrente");

        Conta conta = Conta.builder().id("1234").nome("Conta Corrente").build();
        Mockito.when(contaService.criarConta(ArgumentMatchers.any(ContaRequestDTO.class), ArgumentMatchers.any(String.class)))
                .thenReturn(conta);

        // Converte o objeto para JSON
        String jsonContent = objectMapper.writeValueAsString(contaRequestDTO);

        mockMvc.perform(
                        // Act
                        post("/api/contas")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent)
                )
                // Assert
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1234"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Conta Corrente"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void naoDeveInserirUmaContaComMesmoNome() throws Exception {

        // Arrange
        Mockito.when(contaService.criarConta(ArgumentMatchers.any(ContaRequestDTO.class), ArgumentMatchers.any(String.class)))
                .thenThrow(new NegocioException("Conta com nome informado já existente."));

        ContaRequestDTO contaRequestDTO = new ContaRequestDTO(null, "Conta Corrente");
        // Converte o objeto para JSON
        String jsonContent = objectMapper.writeValueAsString(contaRequestDTO);

        String resposta = mockMvc.perform(
                        // Act
                        post("/api/contas")
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

        Assertions.assertEquals(resposta,"Conta com nome informado já existente.");
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveAtualizarUmaConta() throws Exception {

        // Arrange
        String idConta = "1234";
        ContaRequestDTO contaRequestDTO = new ContaRequestDTO(idConta, "Conta Atualizada");

        Conta contaAtualizada = Conta.builder().id("1234").nome("Conta Atualizada").build();
        Mockito.when(contaService.atualizarConta(
                        idConta,
                        contaRequestDTO,
                        "usuarioTeste"))
                .thenReturn(contaAtualizada);

        // Converte o objeto para JSON
        String jsonContent = objectMapper.writeValueAsString(contaRequestDTO);

        mockMvc.perform(
                        // Act
                        put("/api/contas/" + idConta)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent)
                )
                // Assert
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1234"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Conta Atualizada"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void naoDeveAtualizarUmaContaComMesmoNome() throws Exception {

        // Arrange
        String idConta = "1234";
        ContaRequestDTO contaRequestDTO = new ContaRequestDTO(idConta, "Conta Atualizada");

        Mockito.when(contaService.atualizarConta(
                        idConta,
                        contaRequestDTO,
                        "usuarioTeste"))
                .thenThrow(new NegocioException("Conta com nome informado já existente."));

        // Converte o objeto para JSON
        String jsonContent = objectMapper.writeValueAsString(contaRequestDTO);

        String resposta = mockMvc.perform(
                        // Act
                        put("/api/contas/" + idConta)
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

        Assertions.assertEquals(resposta,"Conta com nome informado já existente.");
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveDeletarUmaConta() throws Exception {

        // Arrange
        String idConta = "1234";
        Mockito.doNothing().when(contaService).deletarConta(idConta, "usuarioTeste");

        mockMvc.perform(
                        // Act
                        delete("/api/contas/" + idConta)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                )
                // Assert
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}