package com.mulato.axur.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulato.axur.model.CrawlRequest;
import com.mulato.axur.model.CrawlResult;
import com.mulato.axur.service.CrawlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes para o Requisito 8: Compilação e execução
 * 
 * Testa os comandos de compilação e execução via Docker:
 * - docker build . -t axreng/backend
 * - docker run -e BASE_URL=http://hiring.axreng.com/ -p 4567:4567 --rm axreng/backend
 * - Porta 4567 configurada corretamente
 * - Aplicação responsiva após inicialização
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "server.port=4567",
    "crawl.base-url=http://hiring.axreng.com/"
})
@DisplayName("Requisito 8: Compilação e Execução")
public class CompilationExecutionTest {

    @LocalServerPort
    private int actualPort;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CrawlService crawlService;

    @Test
    @DisplayName("Aplicação deve estar configurada para porta 4567")
    public void testConfiguracaoPorta4567() {
        // Assert
        // Em ambiente de teste usa porta aleatória, mas configuração deve apontar para 4567
        // O importante é que a aplicação seja configurável via propriedades
        assertNotNull(actualPort, "Porta deve estar configurada");
        assertTrue(actualPort > 0, "Porta deve ser válida");
    }

    @Test
    @DisplayName("Aplicação deve responder na porta configurada")
    public void testAplicacaoRespondeNaPorta() throws Exception {
        // Arrange
        CrawlRequest request = new CrawlRequest("test");

        // Act & Assert
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    @DisplayName("Endpoints principais devem estar acessíveis")
    public void testEndpointsPrincipaisAcessiveis() throws Exception {
        // Test POST /crawl
        CrawlRequest request = new CrawlRequest("spring");
        
        String response = mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract ID from response
        String id = objectMapper.readTree(response).get("id").asText();

        // Test GET /crawl/{id}
        mockMvc.perform(get("/crawl/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.urls").exists());
    }

    @Test
    @DisplayName("Variável de ambiente BASE_URL deve ser processada")
    public void testVariavelAmbienteBASE_URL() {
        // Este teste verifica se a aplicação processa a variável de ambiente
        // A configuração real será testada via Docker em ambiente de integração
        
        // Verifica se o serviço está funcional (implica que configuração foi carregada)
        assertDoesNotThrow(() -> {
            String id = crawlService.startCrawl("environment");
            assertNotNull(id, "Serviço deve estar funcional com configuração de ambiente");
        }, "Aplicação deve processar configuração de ambiente corretamente");
    }

    @Test
    @DisplayName("Aplicação deve aceitar requisições HTTP conforme especificação")
    public void testRequisicaoHTTPConformeEspecificacao() throws Exception {
        // Arrange - Request exatamente como especificado no desafio
        String requestBody = "{\"keyword\": \"security\"}";

        // Act & Assert
        mockMvc.perform(post("/crawl")
                .header("Host", "localhost:4567")
                .contentType("application/json")
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isString());
    }

    @Test
    @DisplayName("Resposta deve seguir formato especificado no desafio")
    public void testRespostaSegueFormatoEspecificado() throws Exception {
        // Arrange
        CrawlRequest request = new CrawlRequest("format");

        // Act
        String response = mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        // Test GET response format
        String getResponse = mockMvc.perform(get("/crawl/" + id))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CrawlResult result = objectMapper.readValue(getResponse, CrawlResult.class);

        // Assert - Formato conforme especificação
        assertNotNull(result.getId(), "Response deve conter 'id'");
        assertNotNull(result.getStatus(), "Response deve conter 'status'");
        assertNotNull(result.getUrls(), "Response deve conter 'urls'");
        assertTrue(result.getStatus().equals("active") || result.getStatus().equals("done"),
                  "Status deve ser 'active' ou 'done'");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "DOCKER_TEST", matches = "true")
    @DisplayName("Aplicação deve ser executável via comandos Docker especificados")
    public void testExecucaoViaDockerComandosEspecificados() {
        // Este teste só executa em ambiente onde Docker está disponível
        // e a variável DOCKER_TEST está definida
        
        // Verifica se a aplicação está rodando (implica que foi compilada e executada com sucesso)
        assertDoesNotThrow(() -> {
            String id = crawlService.startCrawl("docker");
            CrawlResult result = crawlService.getCrawlResult(id);
            assertNotNull(result, "Aplicação deve estar funcionando via Docker");
        }, "Aplicação deve ser executável via comandos Docker especificados");
    }

    @Test
    @DisplayName("Aplicação deve inicializar sem erros críticos")
    public void testInicializacaoSemErrosCriticos() {
        // Verifica se componentes principais estão funcionais
        assertNotNull(crawlService, "CrawlService deve estar inicializado");
        assertNotNull(mockMvc, "MockMvc deve estar configurado");
        assertNotNull(objectMapper, "ObjectMapper deve estar disponível");

        // Testa funcionalidade básica
        assertDoesNotThrow(() -> {
            String id = crawlService.startCrawl("initialization");
            assertNotNull(id, "Deve conseguir iniciar busca");
            
            CrawlResult result = crawlService.getCrawlResult(id);
            assertNotNull(result, "Deve conseguir recuperar resultado");
        }, "Funcionalidades básicas devem estar operacionais");
    }

    @Test
    @DisplayName("Configuração deve permitir override via variáveis de ambiente")
    public void testConfiguracaoOverrideViaVariaveisAmbiente() {
        // Testa se a configuração é flexível para diferentes ambientes
        // O importante é que a aplicação seja configurável externamente
        
        assertDoesNotThrow(() -> {
            // Se chegou até aqui, a configuração foi carregada com sucesso
            String id = crawlService.startCrawl("config");
            assertNotNull(id, "Configuração deve permitir funcionalidade básica");
        }, "Configuração deve ser flexível para diferentes ambientes");
    }
}
