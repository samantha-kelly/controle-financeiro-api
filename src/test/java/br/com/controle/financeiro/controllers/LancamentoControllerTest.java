package br.com.controle.financeiro.controllers;

import br.com.controle.financeiro.controllers.config.ControllerUserTestConfig;
import br.com.controle.financeiro.controllers.config.CustomUserDetailsService;
import br.com.controle.financeiro.controllers.dto.LancamentoRequestDTO;
import br.com.controle.financeiro.domain.Categoria;
import br.com.controle.financeiro.domain.Conta;
import br.com.controle.financeiro.domain.Lancamento;
import br.com.controle.financeiro.repositories.dto.LancamentoCompletoDTO;
import br.com.controle.financeiro.services.LancamentoService;
import br.com.controle.financeiro.services.exception.NegocioException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(LancamentoController.class)
@Import(LancamentoController.class)
@ContextConfiguration(classes = {GlobalExceptionHandler.class, NegocioException.class, ControllerUserTestConfig.class, CustomUserDetailsService.class})
class LancamentoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LancamentoService lancamentoService;

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveObterTodosLancamentos() throws Exception {

        // Arrange
        Lancamento lancamentoPizza = Lancamento.builder()
                .id("123")
                .nome("Pizza")
                .valor(BigDecimal.TEN)
                .data(LocalDate.of(2024, 5, 15))
                .pago(false)
                .conta(Conta.builder().id("111").nome("Conta Conjunta").build())
                .categoria(Categoria.builder().id("222").nome("Alimentação").build())
                .build();
        Lancamento lancamentoTaxi = Lancamento.builder()
                .id("456")
                .nome("Taxi")
                .valor(BigDecimal.valueOf(250.12))
                .data(LocalDate.of(2024, 10, 20))
                .pago(true)
                .conta(Conta.builder().id("111").nome("Conta Conjunta").build())
                .categoria(Categoria.builder().id("333").nome("Transporte").build())
                .build();
        Mockito.when(lancamentoService.obterTodosLancamentos("usuarioTeste"))
                .thenReturn(List.of(lancamentoPizza, lancamentoTaxi));

        mockMvc.perform(
                        // Act
                        get("/api/lancamentos")
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(MockMvcResultMatchers.status().isOk())
                // Verifica se o retorno é uma lista
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))

                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nome").value("Pizza"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].valor").value("10"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].data").value("15-05-2024"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].pago").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].idConta").value("111"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].idCategoria").value("222"))

                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("456"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].nome").value("Taxi"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].valor").value("250.12"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].data").value("20-10-2024"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].pago").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].idConta").value("111"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].idCategoria").value("333"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveObterTodosLancamentosCompletos() throws Exception {

        // Arrange
        LancamentoCompletoDTO lancamentoPizza = LancamentoCompletoDTO.builder()
                .id("123")
                .nome("Pizza")
                .valor(BigDecimal.TEN)
                .data(LocalDate.of(2024, 5, 15))
                .pago(false)
                .nomeConta("Conta Conjunta")
                .nomeCategoria("Alimentação")
                .build();
        LancamentoCompletoDTO lancamentoTaxi = LancamentoCompletoDTO.builder()
                .id("456")
                .nome("Taxi")
                .valor(BigDecimal.valueOf(250.12))
                .data(LocalDate.of(2024, 10, 20))
                .pago(true)
                .nomeConta("Conta Conjunta")
                .nomeCategoria("Transporte")
                .build();
        Mockito.when(lancamentoService.obterTodosLancamentosCompletos("usuarioTeste"))
                .thenReturn(List.of(lancamentoPizza, lancamentoTaxi));

        mockMvc.perform(
                        // Act
                        get("/api/lancamentos/completos")
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(MockMvcResultMatchers.status().isOk())
                // Verifica se o retorno é uma lista
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))

                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nome").value("Pizza"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].valor").value("10"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].data").value("15-05-2024"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].pago").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nomeConta").value("Conta Conjunta"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nomeCategoria").value("Alimentação"))

                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id").value("456"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].nome").value("Taxi"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].valor").value("250.12"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].data").value("20-10-2024"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].pago").value("true"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].nomeConta").value("Conta Conjunta"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].nomeCategoria").value("Transporte"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveObterLancamentoPeloId() throws Exception {

        // Arrange
        String idLancamento = "1234";
        Lancamento lancamentoPizza = Lancamento.builder()
                .id(idLancamento)
                .nome("Pizza")
                .valor(BigDecimal.TEN)
                .data(LocalDate.of(2024, 5, 15))
                .pago(false)
                .conta(Conta.builder().id("111").nome("Conta Conjunta").build())
                .categoria(Categoria.builder().id("222").nome("Alimentação").build())
                .build();
        Mockito.when(lancamentoService.obterLancamentoPorId(idLancamento, "usuarioTeste"))
                .thenReturn(lancamentoPizza);

        mockMvc.perform(
                        // Act
                        get("/api/lancamentos/" + idLancamento)
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1234"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Pizza"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.valor").value("10"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("15-05-2024"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pago").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idConta").value("111"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idCategoria").value("222"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveObterLancamentoPorCompetencia() throws Exception {

        // Arrange
        Lancamento lancamentoPizza = Lancamento.builder()
                .id("123")
                .nome("Pizza")
                .valor(BigDecimal.TEN)
                .data(LocalDate.of(2024, 5, 15))
                .pago(false)
                .conta(Conta.builder().id("111").nome("Conta Conjunta").build())
                .categoria(Categoria.builder().id("222").nome("Alimentação").build())
                .build();

        Mockito.when(lancamentoService.obterLancamentosPorCompetencia(202405, "usuarioTeste"))
                .thenReturn(List.of(lancamentoPizza));

        mockMvc.perform(
                        // Act
                        get("/api/lancamentos/competencia/202405")
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .contentType(MediaType.APPLICATION_JSON))
                // Assert
                .andExpect(MockMvcResultMatchers.status().isOk())
                // Verifica se o retorno é uma lista
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray())
                .andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(1)))

                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").value("123"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].nome").value("Pizza"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].valor").value("10"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].data").value("15-05-2024"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].pago").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].idConta").value("111"))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].idCategoria").value("222"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveInserirUmLancamento() throws Exception {

        // Arrange
        LancamentoRequestDTO novoLancamentoDto = new LancamentoRequestDTO(null, "Pizza",
                "idContaFake", "idCategoriaFake",
                "03-05-2024", BigDecimal.TEN, false);

        Lancamento lancamentoPizza = Lancamento.builder()
                .id("111")
                .nome("Pizza")
                .valor(BigDecimal.TEN)
                .data(LocalDate.of(2024, 5, 15))
                .pago(false)
                .conta(Conta.builder().id("222").nome("Conta Conjunta").build())
                .categoria(Categoria.builder().id("333").nome("Alimentação").build())
                .build();

        Mockito.when(lancamentoService.criarLancamento(novoLancamentoDto, "usuarioTeste"))
                .thenReturn(lancamentoPizza);


        // Converte o objeto para JSON
        String jsonContent = objectMapper.writeValueAsString(novoLancamentoDto);

        mockMvc.perform(
                        // Act
                        post("/api/lancamentos")
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent)
                )
                // Assert
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("111"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Pizza"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("15-05-2024"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pago").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idConta").value("222"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idCategoria").value("333"));
    }


    @Test
    @WithUserDetails("usuarioTeste")
    public void naoDeveInserirUmaLancamentoComValor0() throws Exception {

        // Arrange
        LancamentoRequestDTO novoLancamentoDto = new LancamentoRequestDTO(null, "Pizza",
                "idContaFake", "idCategoriaFake",
                "03-05-2024", BigDecimal.ZERO, false);

        Mockito.when(lancamentoService.criarLancamento(novoLancamentoDto, "usuarioTeste"))
                .thenThrow(new NegocioException("Valor do lançamento informado deve ser maior que 0!"));

        // Converte o objeto para JSON
        String jsonContent = objectMapper.writeValueAsString(novoLancamentoDto);

        String resposta = mockMvc.perform(
                        // Act
                        post("/api/lancamentos")
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

        Assertions.assertEquals(resposta, "Valor do lançamento informado deve ser maior que 0!");
    }

    //
    @Test
    @WithUserDetails("usuarioTeste")
    public void deveAtualizarUmaLancamento() throws Exception {

        // Arrange
        String idLancamento = "1234";
        LancamentoRequestDTO novoLancamentoDto = new LancamentoRequestDTO(idLancamento, "Pizza",
                "222", "333",
                "15-05-2024", BigDecimal.TEN, false);

        Lancamento lancamentoAtualizado = Lancamento.builder()
                .id(idLancamento)
                .nome("Pizza")
                .valor(BigDecimal.TEN)
                .data(LocalDate.of(2024, 5, 15))
                .pago(false)
                .conta(Conta.builder().id("222").nome("Conta Conjunta").build())
                .categoria(Categoria.builder().id("333").nome("Alimentação").build())
                .build();

        Mockito.when(lancamentoService.atualizarLancamento(
                        idLancamento,
                        novoLancamentoDto,
                        "usuarioTeste"))
                .thenReturn(lancamentoAtualizado);

        // Converte o objeto para JSON
        String jsonContent = objectMapper.writeValueAsString(novoLancamentoDto);

        mockMvc.perform(
                        // Act
                        put("/api/lancamentos/" + idLancamento)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonContent)
                )
                // Assert
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value("1234"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.nome").value("Pizza"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.data").value("15-05-2024"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.pago").value("false"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idConta").value("222"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.idCategoria").value("333"));
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void naoDeveAtualizarUmaLancamentoComValor0() throws Exception {

        // Arrange
        String idLancamento = "1234";
        LancamentoRequestDTO novoLancamentoDto = new LancamentoRequestDTO(idLancamento, "Pizza",
                "idContaFake", "idCategoriaFake",
                "03-05-2024", BigDecimal.ZERO, false);

        Mockito.when(lancamentoService.atualizarLancamento(
                        idLancamento,
                        novoLancamentoDto,
                        "usuarioTeste"))
                .thenThrow(new NegocioException("Valor do lançamento informado deve ser maior que 0!"));

        // Converte o objeto para JSON
        String jsonContent = objectMapper.writeValueAsString(novoLancamentoDto);

        String resposta = mockMvc.perform(
                        // Act
                        put("/api/lancamentos/" + idLancamento)
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

        Assertions.assertEquals(resposta, "Valor do lançamento informado deve ser maior que 0!");
    }

    @Test
    @WithUserDetails("usuarioTeste")
    public void deveDeletarUmaLancamento() throws Exception {

        // Arrange
        String idLancamento = "1234";
        Mockito.doNothing().when(lancamentoService).deletarLancamento(idLancamento, "usuarioTeste");

        mockMvc.perform(
                        // Act
                        delete("/api/lancamentos/" + idLancamento)
                                .with(SecurityMockMvcRequestPostProcessors.csrf())
                                .header("Authorization", "Bearer " + "fake-token-jwt")
                )
                // Assert
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}