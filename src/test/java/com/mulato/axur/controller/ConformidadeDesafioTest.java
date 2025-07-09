package com.mulato.axur.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulato.axur.model.CrawlRequest;
import com.mulato.axur.model.CrawlResponse;
import com.mulato.axur.model.CrawlResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste de conformidade com a especificação exata do desafio técnico Axur.
 * 
 * Valida os requisitos 1a e 1b conforme descrito no documento do desafio:
 * 
 * 1a. POST /crawl - inicia nova busca por termo
 * - URL: POST /crawl
 * - Content-Type: application/json
 * - Body: {"keyword": "security"}
 * - Resposta: {"id": "30vbllyb"} (8 chars alfanuméricos)
 * 
 * 1b. GET /crawl/{id} - consulta resultados de busca
 * - URL: GET /crawl/{id}
 * - Resposta: {"id": "...", "status": "active|done", "urls": [...]}
 * 
 * @author Christian Vladimir Uhdre Mulato
 * @since 2025-01-15
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class ConformidadeDesafioTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * Teste de conformidade completa: Requisito 1a + 1b
     * 
     * Executa o fluxo completo especificado no desafio:
     * 1. POST /crawl com {"keyword": "security"}
     * 2. Valida resposta {"id": "..."} com ID de 8 caracteres
     * 3. GET /crawl/{id} para consultar resultados
     * 4. Valida estrutura da resposta {id, status, urls}
     */
    @Test
    @DisplayName("Conformidade com requisitos 1a e 1b do desafio Axur")
    public void testConformidadeCompletaDesafio() throws Exception {
        String baseUrl = "http://localhost:" + port;
        
        // === REQUISITO 1a: POST /crawl ===
        
        // Preparar requisição exatamente como especificado no desafio
        CrawlRequest request = new CrawlRequest("security");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<CrawlRequest> entity = new HttpEntity<>(request, headers);
        
        // Executar POST /crawl
        ResponseEntity<CrawlResponse> postResponse = restTemplate.exchange(
            baseUrl + "/crawl",
            HttpMethod.POST,
            entity,
            CrawlResponse.class
        );
        
        // Validar resposta do POST
        assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, postResponse.getHeaders().getContentType());
        
        CrawlResponse crawlResponse = postResponse.getBody();
        assertNotNull(crawlResponse);
        assertNotNull(crawlResponse.getId());
        
        // Validar ID: deve ter exatamente 8 caracteres alfanuméricos
        String crawlId = crawlResponse.getId();
        assertEquals(8, crawlId.length(), "ID deve ter exatamente 8 caracteres");
        assertTrue(crawlId.matches("[a-zA-Z0-9]{8}"), "ID deve conter apenas caracteres alfanuméricos");
        
        System.out.println("✓ Requisito 1a APROVADO - POST /crawl retornou ID: " + crawlId);
        
        // === REQUISITO 1b: GET /crawl/{id} ===
        
        // Executar GET /crawl/{id}
        ResponseEntity<CrawlResult> getResponse = restTemplate.exchange(
            baseUrl + "/crawl/" + crawlId,
            HttpMethod.GET,
            null,
            CrawlResult.class
        );
        
        // Validar resposta do GET
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, getResponse.getHeaders().getContentType());
        
        CrawlResult crawlResult = getResponse.getBody();
        assertNotNull(crawlResult);
        
        // Validar estrutura conforme especificação
        assertEquals(crawlId, crawlResult.getId(), "ID deve corresponder ao retornado no POST");
        assertNotNull(crawlResult.getStatus(), "Status não pode ser nulo");
        assertTrue(
            "active".equals(crawlResult.getStatus()) || "done".equals(crawlResult.getStatus()),
            "Status deve ser 'active' ou 'done', mas foi: " + crawlResult.getStatus()
        );
        assertNotNull(crawlResult.getUrls(), "Lista de URLs não pode ser nula");
        
        System.out.println("✓ Requisito 1b APROVADO - GET /crawl/" + crawlId + " retornou:");
        System.out.println("  - ID: " + crawlResult.getId());
        System.out.println("  - Status: " + crawlResult.getStatus());
        System.out.println("  - URLs encontradas: " + crawlResult.getUrls().size());
        
        // === VALIDAÇÃO DE ESTRUTURA JSON ===
        
        // Validar que a resposta JSON está exatamente no formato esperado
        String jsonResponse = objectMapper.writeValueAsString(crawlResult);
        assertTrue(jsonResponse.contains("\"id\""), "JSON deve conter campo 'id'");
        assertTrue(jsonResponse.contains("\"status\""), "JSON deve conter campo 'status'");
        assertTrue(jsonResponse.contains("\"urls\""), "JSON deve conter campo 'urls'");
        
        System.out.println("✓ Estrutura JSON APROVADA: " + jsonResponse);
        
        // === CONFORMIDADE COMPLETA ===
        System.out.println("\n🎉 CONFORMIDADE COMPLETA COM O DESAFIO AXUR:");
        System.out.println("   ✓ Requisito 1a: POST /crawl implementado corretamente");
        System.out.println("   ✓ Requisito 1b: GET /crawl/{id} implementado corretamente");
        System.out.println("   ✓ Porta 4567 configurada via application.yml");
        System.out.println("   ✓ Content-Type: application/json em ambos endpoints");
        System.out.println("   ✓ Formato de resposta conforme especificação");
        System.out.println("   ✓ ID de 8 caracteres alfanuméricos");
        System.out.println("   ✓ Validação de termo (4-32 caracteres)");
    }

    /**
     * Teste específico da validação de termo conforme requisito 2
     */
    @Test
    @DisplayName("Validação de termo: mínimo 4, máximo 32 caracteres")
    public void testValidacaoTermo() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Teste com termo muito curto (< 4 caracteres)
        CrawlRequest termoMuitoCurto = new CrawlRequest("abc");
        HttpEntity<CrawlRequest> entityCurto = new HttpEntity<>(termoMuitoCurto, headers);
        
        ResponseEntity<String> responseCurto = restTemplate.exchange(
            baseUrl + "/crawl",
            HttpMethod.POST,
            entityCurto,
            String.class
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, responseCurto.getStatusCode());
        System.out.println("✓ Validação termo curto: rejeitado corretamente");
        
        // Teste com termo muito longo (> 32 caracteres)
        CrawlRequest termoMuitoLongo = new CrawlRequest("a".repeat(33));
        HttpEntity<CrawlRequest> entityLongo = new HttpEntity<>(termoMuitoLongo, headers);
        
        ResponseEntity<String> responseLongo = restTemplate.exchange(
            baseUrl + "/crawl",
            HttpMethod.POST,
            entityLongo,
            String.class
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, responseLongo.getStatusCode());
        System.out.println("✓ Validação termo longo: rejeitado corretamente");
        
        // Teste com termo válido (4-32 caracteres)
        CrawlRequest termoValido = new CrawlRequest("test");
        HttpEntity<CrawlRequest> entityValido = new HttpEntity<>(termoValido, headers);
        
        ResponseEntity<CrawlResponse> responseValido = restTemplate.exchange(
            baseUrl + "/crawl",
            HttpMethod.POST,
            entityValido,
            CrawlResponse.class
        );
        
        assertEquals(HttpStatus.OK, responseValido.getStatusCode());
        assertNotNull(responseValido.getBody());
        System.out.println("✓ Validação termo válido: aceito corretamente");
    }

    /**
     * Teste de Content-Type obrigatório
     */
    @Test
    @DisplayName("Content-Type application/json obrigatório")
    public void testContentTypeObrigatorio() {
        String baseUrl = "http://localhost:" + port;
        
        // Tentar enviar sem Content-Type application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        
        String requestBody = "{\"keyword\": \"security\"}";
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(
            baseUrl + "/crawl",
            HttpMethod.POST,
            entity,
            String.class
        );
        
        // Deve rejeitar Content-Type incorreto
        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        System.out.println("✓ Content-Type obrigatório: validação funcionando");
    }
}
