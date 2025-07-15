package com.mulato.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulato.api.model.CrawlRequest;
import com.mulato.api.model.CrawlResult;
import com.mulato.api.service.CrawlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * Testes unitários para os endpoints da API HTTP (Requisito 1)
 * 
 * Esta classe testa apenas os endpoints definidos no Requisito 1:
 * - POST /crawl para iniciar uma busca por termo
 * - GET /crawl/{id} para consultar resultados
 *
 * E a validação do termo (Requisito 2):
 * - Min 4, max 32 caracteres
 * - Case insensitive (verificado em nível de serviço)
 * 
 * E a validação do formato do ID (Requisito 3):
 * - 8 caracteres alfanuméricos
 */
@WebMvcTest(ApiHttpController.class)
@DisplayName("Requisito 1: API HTTP - Endpoints POST e GET")
public class ApiHttpControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrawlService crawlService;

    /**
     * Helper para validar Content-Type
     */
    private static ResultMatcher isApplicationJson() {
        return content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("1a. POST /crawl - Deve iniciar nova busca conforme especificação")
    public void testPostCrawl_DeveIniciarNovaBusca() throws Exception {
        // Arrange - usando exemplo exato do desafio
        String expectedId = "30vbllyb";  // ID de 8 caracteres conforme exemplo
        when(crawlService.startCrawl("security")).thenReturn(expectedId);

        CrawlRequest request = new CrawlRequest("security");

        // Act & Assert - seguindo formato exato da especificação
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(isApplicationJson())
                .andExpect(jsonPath("$.id", is(expectedId)))
                .andExpect(jsonPath("$.id", hasLength(8)));
    }

    @Test
    @DisplayName("1b. GET /crawl/{id} - Deve retornar resultados conforme especificação")
    public void testGetCrawl_DeveRetornarResultadosBuscaAtiva() throws Exception {
        // Arrange - usando exemplo exato do desafio
        String taskId = "30vbllyb";  // ID do exemplo do desafio
        CrawlResult mockResult = new CrawlResult(
            taskId, 
            "active",  // status conforme especificação
            Arrays.asList(
                "http://example.com/page1.html",
                "http://example.com/page2.html"
            )
        );
        
        when(crawlService.getCrawlResult(taskId)).thenReturn(mockResult);

        // Act & Assert - formato exato da resposta especificada
        mockMvc.perform(get("/crawl/" + taskId))
                .andExpect(status().isOk())
                .andExpect(isApplicationJson())
                .andExpect(jsonPath("$.id", is(taskId)))
                .andExpect(jsonPath("$.status", is("active")))
                .andExpect(jsonPath("$.urls", hasSize(2)))
                .andExpect(jsonPath("$.urls[0]", is("http://example.com/page1.html")))
                .andExpect(jsonPath("$.urls[1]", is("http://example.com/page2.html")));
    }

    @Test
    @DisplayName("1b. GET /crawl/{id} - Deve retornar resultados da busca concluída")
    public void testGetCrawl_DeveRetornarResultadosBuscaConcluida() throws Exception {
        // Arrange
        String taskId = "def67890";
        CrawlResult mockResult = new CrawlResult(
            taskId, 
            "done", 
            Arrays.asList(
                "http://example.com/about.html",
                "http://example.com/contact.html",
                "http://example.com/services.html"
            )
        );
        
        when(crawlService.getCrawlResult(taskId)).thenReturn(mockResult);

        // Act & Assert
        mockMvc.perform(get("/crawl/" + taskId))
                .andExpect(status().isOk())
                .andExpect(isApplicationJson())
                .andExpect(jsonPath("$.id", is(taskId)))
                .andExpect(jsonPath("$.status", is("done")))
                .andExpect(jsonPath("$.urls", hasSize(3)));
    }

    @Test
    @DisplayName("1b. GET /crawl/{id} - Deve retornar 404 para ID não encontrado")
    public void testGetCrawl_DeveRetornar404ParaIdNaoEncontrado() throws Exception {
        // Arrange
        String taskId = "notfound";
        when(crawlService.getCrawlResult(taskId)).thenReturn(null);

        // Act & Assert
        mockMvc.perform(get("/crawl/" + taskId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("1a. POST /crawl - Deve validar Content-Type application/json")
    public void testPostCrawl_DeveValidarContentType() throws Exception {
        // Arrange
        when(crawlService.startCrawl(anyString())).thenReturn("test1234");
        CrawlRequest request = new CrawlRequest("security");

        // Act & Assert
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(isApplicationJson());
    }

    @Test
    @DisplayName("1a. POST /crawl - Deve rejeitar Content-Type inválido")
    public void testPostCrawl_DeveRejeitarContentTypeInvalido() throws Exception {
        // Arrange
        CrawlRequest request = new CrawlRequest("security");

        // Act & Assert - O Spring deve rejeitar com status 415 (Unsupported Media Type)
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.TEXT_PLAIN)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    @DisplayName("2. Validação do termo - Deve rejeitar termo muito curto (menos de 4 caracteres)")
    public void testRequisito2_DeveRejeitarTermoCurto() throws Exception {
        // Arrange
        CrawlRequest request = new CrawlRequest("abc"); // 3 caracteres - menor que o mínimo

        // Act & Assert
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("2. Validação do termo - Deve rejeitar termo muito longo (mais de 32 caracteres)")
    public void testRequisito2_DeveRejeitarTermoLongo() throws Exception {
        // Arrange
        // 33 caracteres - maior que o máximo permitido
        CrawlRequest request = new CrawlRequest("abcdefghijklmnopqrstuvwxyzabcdefg");

        // Act & Assert
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("2. Validação do termo - Deve aceitar termo exatamente no limite mínimo (4 caracteres)")
    public void testRequisito2_DeveAceitarTermoNoLimiteMinimo() throws Exception {
        // Arrange
        String expectedId = "abcd1234";
        when(crawlService.startCrawl("test")).thenReturn(expectedId);
        
        CrawlRequest request = new CrawlRequest("test"); // 4 caracteres - exatamente o mínimo

        // Act & Assert
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(isApplicationJson())
                .andExpect(jsonPath("$.id", is(expectedId)));
    }

    @Test
    @DisplayName("2. Validação do termo - Deve aceitar termo exatamente no limite máximo (32 caracteres)")
    public void testRequisito2_DeveAceitarTermoNoLimiteMaximo() throws Exception {
        // Arrange
        String expectedId = "wxyz9876";
        // 32 caracteres - exatamente o máximo
        String longKeyword = "abcdefghijklmnopqrstuvwxyzabcdef";
        when(crawlService.startCrawl(longKeyword)).thenReturn(expectedId);
        
        CrawlRequest request = new CrawlRequest(longKeyword);

        // Act & Assert
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(isApplicationJson())
                .andExpect(jsonPath("$.id", is(expectedId)));
    }

    @Test
    @DisplayName("3. ID da busca - Deve ser um código alfanumérico de 8 caracteres")
    public void testRequisito3_IdDaBuscaAlfanumerico() throws Exception {
        // Arrange - Mock de serviço com diversos IDs para validação
        when(crawlService.startCrawl("keyword1")).thenReturn("12345678");
        when(crawlService.startCrawl("keyword2")).thenReturn("abcdefgh");
        when(crawlService.startCrawl("keyword3")).thenReturn("a1b2c3d4");

        // Act & Assert - Testando múltiplos casos
        // ID com 8 dígitos
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CrawlRequest("keyword1"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", hasLength(8)))
                .andExpect(jsonPath("$.id", matchesPattern("[a-zA-Z0-9]{8}")));
                
        // ID com 8 letras
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CrawlRequest("keyword2"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", hasLength(8)))
                .andExpect(jsonPath("$.id", matchesPattern("[a-zA-Z0-9]{8}")));
                
        // ID com letras e números
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new CrawlRequest("keyword3"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", hasLength(8)))
                .andExpect(jsonPath("$.id", matchesPattern("[a-zA-Z0-9]{8}")));
    }

    @Test
    @DisplayName("1a. POST /crawl - Deve rejeitar JSON sem o campo keyword")
    public void testPostCrawl_DeveRejeitarJsonSemKeyword() throws Exception {
        // Act & Assert
        // JSON válido mas sem o campo obrigatório "keyword"
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"outroField\": \"valor\"}"))
                .andExpect(status().isBadRequest());
                
        // JSON vazio
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
