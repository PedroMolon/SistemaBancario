package com.pedromolon.SistemaBancarioTDE.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureJsonTesters
@Transactional
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CenarioTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void setup() {
        jdbcTemplate.execute("TRUNCATE TABLE contas_corrente CASCADE;");
        jdbcTemplate.execute("TRUNCATE TABLE clientes CASCADE;");
    }

    @Test
    @Order(1)
    @DisplayName("Fluxo principal: criar clientes/contas, depositar, sacar, transferir e consultar saldo")
    void deveExecutarFluxoPrincipal() throws Exception {
        System.out.println("Iniciando o fluxo principal do cenário de teste...");

        // Criar Cliente A
        System.out.println("Criando Cliente A...");
        var novoClienteA = """
            { "nome": "Cliente A", "idade": 28, "email": "clientea@email.com", "ativo": true }
        """;
        MvcResult resClienteA = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(novoClienteA))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode clienteAJson = objectMapper.readTree(resClienteA.getResponse().getContentAsString());
        Long clienteAId = clienteAJson.path("id").asLong();
        System.out.println("Cliente A criado com ID: " + clienteAId);

        // Criar Cliente B
        System.out.println("Criando Cliente B...");
        var novoClienteB = """
            { "nome": "Cliente B", "idade": 31, "email": "clienteb@email.com", "ativo": true }
        """;
        MvcResult resClienteB = mockMvc.perform(post("/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(novoClienteB))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode clienteBJson = objectMapper.readTree(resClienteB.getResponse().getContentAsString());
        Long clienteBId = clienteBJson.path("id").asLong();
        System.out.println("Cliente B criado com ID: " + clienteBId);

        // Criar Conta para Cliente A
        System.out.println("Criando Conta para Cliente A...");
        var novaContaA = String.format("""
            { "clienteId": %d }
        """, clienteAId);
        MvcResult resContaA = mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(novaContaA))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode contaAJson = objectMapper.readTree(resContaA.getResponse().getContentAsString());
        Long contaAId = contaAJson.path("id").asLong();
        System.out.println("Conta para Cliente A criada com ID: " + contaAId);

        // Criar Conta para Cliente B
        System.out.println("Criando Conta para Cliente B...");
        var novaContaB = String.format("""
            { "clienteId": %d }
        """, clienteBId);
        MvcResult resContaB = mockMvc.perform(post("/contas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(novaContaB))
                .andExpect(status().isCreated())
                .andReturn();
        JsonNode contaBJson = objectMapper.readTree(resContaB.getResponse().getContentAsString());
        Long contaBId = contaBJson.path("id").asLong();
        System.out.println("Conta para Cliente B criada com ID: " + contaBId);

        // Depositar na Conta A
        System.out.println("Depositando 1000.0 na Conta A...");
        var depositoA = """
            { "valor": 1000.0 }
        """;
        mockMvc.perform(post("/contas/" + contaAId + "/depositar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(depositoA))
                .andExpect(status().isOk());
        System.out.println("Depósito realizado com sucesso.");

        // Sacar da Conta A
        System.out.println("Sacando 200.0 da Conta A...");
        var saqueA = """
            { "valor": 200.0 }
        """;
        mockMvc.perform(post("/contas/" + contaAId + "/sacar")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(saqueA))
                .andExpect(status().isOk());
        System.out.println("Saque realizado com sucesso.");

        // Transferir da Conta A para Conta B
        System.out.println("Transferindo 300.0 da Conta A para Conta B...");
        var transferencia = String.format("""
            { "contaDestino": %d, "valor": 300.0 }
        """, contaBId);
        mockMvc.perform(post("/contas/" + contaAId + "/transferir")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(transferencia))
                .andExpect(status().isOk());
        System.out.println("Transferência realizada com sucesso.");

        // Consultar Saldo Conta A
        System.out.println("Consultando saldo da Conta A...");
        mockMvc.perform(get("/contas/" + contaAId + "/saldo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(500.0));
        System.out.println("Saldo da Conta A verificado: 500.0");

        // Consultar Saldo Conta B
        System.out.println("Consultando saldo da Conta B...");
        mockMvc.perform(get("/contas/" + contaBId + "/saldo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(300.0));
        System.out.println("Saldo da Conta B verificado: 300.0");

        System.out.println("Fluxo principal do cenário de teste concluído com sucesso!");
    }
}
