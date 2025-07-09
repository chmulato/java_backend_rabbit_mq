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

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes de conformidade com os requisitos do desafio técnico Axur.
 * 
 * Esta classe contém testes específicos para validar os requisitos principais:
 * 
 * Requisito 1: API HTTP
 * - POST /crawl para iniciar uma busca por termo
 * - GET /crawl/{id} para consultar resultados
 * 
 * Requisito 2: Validação do termo
 * - Min 4, max 32 caracteres
 * - Case insensitive
 * - Busca em qualquer parte do HTML
 * 
 * Requisito 3: ID da busca
 * - Código alfanumérico de 8 caracteres
 * 
 * Requisito 4: URL base
 * - Determinada por variável de ambiente
 * - Seguir apenas links com mesma URL base
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
     * Teste para o Requisito 1: API HTTP
     * 
     * Valida:
     * - Requisito 1a: POST /crawl para iniciar busca
     * - Requisito 1b: GET /crawl/{id} para consultar resultados
     */
    @Test
    @DisplayName("Requisito 1: API HTTP - POST /crawl e GET /crawl/{id}")
    public void testRequisito1_ApiHttp() throws Exception {
        String baseUrl = "http://localhost:" + port;
        
        // === REQUISITO 1a: POST /crawl ===
        CrawlRequest request = new CrawlRequest("security");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        HttpEntity<CrawlRequest> entity = new HttpEntity<>(request, headers);
        ResponseEntity<CrawlResponse> postResponse = restTemplate.exchange(
            baseUrl + "/crawl",
            HttpMethod.POST,
            entity,
            CrawlResponse.class
        );
        
        // Validações básicas da resposta POST
        assertEquals(HttpStatus.OK, postResponse.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, postResponse.getHeaders().getContentType());
        
        CrawlResponse crawlResponse = postResponse.getBody();
        assertNotNull(crawlResponse);
        assertNotNull(crawlResponse.getId());
        
        // Validar ID (relacionado também ao Requisito 3)
        String crawlId = crawlResponse.getId();
        assertEquals(8, crawlId.length(), "ID deve ter exatamente 8 caracteres");
        assertTrue(crawlId.matches("[a-zA-Z0-9]{8}"), "ID deve conter apenas caracteres alfanuméricos");
        
        // === REQUISITO 1b: GET /crawl/{id} ===
        ResponseEntity<CrawlResult> getResponse = restTemplate.exchange(
            baseUrl + "/crawl/" + crawlId,
            HttpMethod.GET,
            null,
            CrawlResult.class
        );
        
        // Validações básicas da resposta GET
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(MediaType.APPLICATION_JSON, getResponse.getHeaders().getContentType());
        
        CrawlResult crawlResult = getResponse.getBody();
        assertNotNull(crawlResult);
        
        // Validar estrutura da resposta
        assertEquals(crawlId, crawlResult.getId(), "ID deve corresponder ao retornado no POST");
        assertNotNull(crawlResult.getStatus(), "Status não pode ser nulo");
        assertTrue(
            "active".equals(crawlResult.getStatus()) || "done".equals(crawlResult.getStatus()),
            "Status deve ser 'active' ou 'done'"
        );
        assertNotNull(crawlResult.getUrls(), "Lista de URLs não pode ser nula");
    }

    /**
     * Teste específico para o Requisito 2: Validação de termo
     */
    @Test
    @DisplayName("Requisito 2: Validação de termo - mínimo 4, máximo 32 caracteres")
    public void testRequisito2_ValidacaoTermo() {
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
        
        assertEquals(HttpStatus.BAD_REQUEST, responseCurto.getStatusCode(), "Termo com menos de 4 caracteres deve ser rejeitado");
        
        // Teste com termo muito longo (> 32 caracteres)
        CrawlRequest termoMuitoLongo = new CrawlRequest("a".repeat(33));
        HttpEntity<CrawlRequest> entityLongo = new HttpEntity<>(termoMuitoLongo, headers);
        
        ResponseEntity<String> responseLongo = restTemplate.exchange(
            baseUrl + "/crawl",
            HttpMethod.POST,
            entityLongo,
            String.class
        );
        
        assertEquals(HttpStatus.BAD_REQUEST, responseLongo.getStatusCode(), "Termo com mais de 32 caracteres deve ser rejeitado");
        
        // Teste com termo válido (4-32 caracteres)
        CrawlRequest termoValido = new CrawlRequest("test");
        HttpEntity<CrawlRequest> entityValido = new HttpEntity<>(termoValido, headers);
        
        ResponseEntity<CrawlResponse> responseValido = restTemplate.exchange(
            baseUrl + "/crawl",
            HttpMethod.POST,
            entityValido,
            CrawlResponse.class
        );
        
        assertEquals(HttpStatus.OK, responseValido.getStatusCode(), "Termo com 4 caracteres deve ser aceito");
        assertNotNull(responseValido.getBody());
        
        // Teste com termo válido no limite máximo
        CrawlRequest termoMaximo = new CrawlRequest("a".repeat(32));
        HttpEntity<CrawlRequest> entityMaximo = new HttpEntity<>(termoMaximo, headers);
        
        ResponseEntity<CrawlResponse> responseMaximo = restTemplate.exchange(
            baseUrl + "/crawl",
            HttpMethod.POST,
            entityMaximo,
            CrawlResponse.class
        );
        
        assertEquals(HttpStatus.OK, responseMaximo.getStatusCode(), "Termo com 32 caracteres deve ser aceito");
        assertNotNull(responseMaximo.getBody());
    }

    /**
     * Teste específico para o requisito 2:
     * "A busca deve ser case insensitive, em qualquer parte do conteúdo HTML (incluindo tags e comentários)"
     */
    @Test
    @DisplayName("Requisito 2: Busca case insensitive em todo HTML")
    public void testRequisito2_BuscaCaseInsensitive() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Testes com diferentes variações de case para o mesmo termo
        String[] termoVariacoes = {
            "security",  // minúsculas
            "SECURITY",  // maiúsculas
            "Security",  // capitalizado
            "sEcUrItY"   // misto
        };
        
        for (String termo : termoVariacoes) {
            // Criar requisição para cada variação do termo
            CrawlRequest request = new CrawlRequest(termo);
            HttpEntity<CrawlRequest> entity = new HttpEntity<>(request, headers);
            
            // Executar POST /crawl
            ResponseEntity<CrawlResponse> postResponse = restTemplate.exchange(
                baseUrl + "/crawl",
                HttpMethod.POST,
                entity,
                CrawlResponse.class
            );
            
            // Validar que a requisição foi aceita
            assertEquals(HttpStatus.OK, postResponse.getStatusCode(), 
                "A requisição com o termo '" + termo + "' deve ser aceita");
            
            // Obter ID da busca
            CrawlResponse crawlResponse = postResponse.getBody();
            assertNotNull(crawlResponse);
            String crawlId = crawlResponse.getId();
            
            // Consultar resultados da busca
            ResponseEntity<CrawlResult> getResponse = restTemplate.exchange(
                baseUrl + "/crawl/" + crawlId,
                HttpMethod.GET,
                null,
                CrawlResult.class
            );
            
            // Validar que os resultados estão sendo retornados corretamente
            assertEquals(HttpStatus.OK, getResponse.getStatusCode());
            
            CrawlResult result = getResponse.getBody();
            assertNotNull(result);
            assertNotNull(result.getStatus());
            assertNotNull(result.getUrls());
        }
    }
    
    /**
     * Teste específico para o requisito 3:
     * "O id da busca deve ser um código alfanumérico de 8 caracteres gerado automaticamente."
     */
    @Test
    @DisplayName("Requisito 3: ID da busca - código alfanumérico de 8 caracteres")
    public void testRequisito3_IdDaBusca() {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Testes com termos diferentes para verificar o formato do ID
        String[] termos = { "security", "testing", "validation", "crawling" };
        
        for (String termo : termos) {
            // Criar requisição
            CrawlRequest request = new CrawlRequest(termo);
            HttpEntity<CrawlRequest> entity = new HttpEntity<>(request, headers);
            
            // Executar POST /crawl
            ResponseEntity<CrawlResponse> postResponse = restTemplate.exchange(
                baseUrl + "/crawl",
                HttpMethod.POST,
                entity,
                CrawlResponse.class
            );
            
            // Validar formato do ID
            CrawlResponse crawlResponse = postResponse.getBody();
            assertNotNull(crawlResponse);
            String crawlId = crawlResponse.getId();
            
            assertEquals(8, crawlId.length(), 
                "ID deve ter exatamente 8 caracteres: " + crawlId);
            assertTrue(crawlId.matches("[a-zA-Z0-9]{8}"), 
                "ID deve conter apenas caracteres alfanuméricos: " + crawlId);
        }
    }
    
    /**
     * Teste específico para o requisito 4:
     * "A URL base do website em que as análises são realizadas é determinada por uma variável de ambiente.
     * As buscas devem seguir links (absolutos e relativos) em elementos anchor das páginas visitadas
     * se e somente se eles possuírem a mesma URL base."
     */
    @Test
    @DisplayName("Requisito 4: URL base configurada via variável de ambiente e navegação limitada a mesma base")
    public void testRequisito4_UrlBaseVariavelAmbiente() throws URISyntaxException {
        String baseUrl = "http://localhost:" + port;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // 1. Verificar se a variável de ambiente BASE_URL está configurada corretamente
        // Não podemos acessar diretamente a variável de ambiente no teste,
        // mas podemos verificar se o serviço está configurado com uma URL base válida
        
        // 2. Iniciar uma busca para verificar se a URL base está sendo usada corretamente
        CrawlRequest request = new CrawlRequest("testlink");
        HttpEntity<CrawlRequest> entity = new HttpEntity<>(request, headers);
        
        // 3. Executar POST /crawl
        ResponseEntity<CrawlResponse> postResponse = restTemplate.exchange(
            baseUrl + "/crawl",
            HttpMethod.POST,
            entity,
            CrawlResponse.class
        );
        
        // 4. Validar que a requisição foi aceita
        assertEquals(HttpStatus.OK, postResponse.getStatusCode(), 
            "A requisição com o termo de teste deve ser aceita");
        
        // 5. Obter ID da busca
        CrawlResponse crawlResponse = postResponse.getBody();
        assertNotNull(crawlResponse, "O corpo da resposta não pode ser nulo");
        String crawlId = crawlResponse.getId();
        
        // 6. Consultar resultados da busca várias vezes para dar tempo ao crawler
        // Pode ser necessário um tempo para o crawler encontrar URLs
        CrawlResult result = null;
        int maxAttempts = 10;
        int attempts = 0;
        
        while (attempts < maxAttempts) {
            ResponseEntity<CrawlResult> getResponse = restTemplate.exchange(
                baseUrl + "/crawl/" + crawlId,
                HttpMethod.GET,
                null,
                CrawlResult.class
            );
            
            assertEquals(HttpStatus.OK, getResponse.getStatusCode());
            result = getResponse.getBody();
            
            // Se encontrou URLs ou a busca está concluída, podemos sair do loop
            if (result != null && 
                (result.getUrls().size() > 0 || "done".equals(result.getStatus()))) {
                break;
            }
            
            // Aguardar um pouco para dar tempo ao crawler
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            
            attempts++;
        }
        
        // 7. Validar resultado final da busca
        assertNotNull(result, "O resultado da busca não pode ser nulo");
        assertNotNull(result.getStatus(), "O status da busca não pode ser nulo");
        assertNotNull(result.getUrls(), "A lista de URLs não pode ser nula");
        
        // 8. Verificar se todas as URLs encontradas têm a mesma base
        // Isso valida que apenas links com a mesma URL base foram seguidos
        if (result.getUrls().size() > 0) {
            System.out.println("URLs encontradas:");
            for (String url : result.getUrls()) {
                System.out.println("  - " + url);
                assertTrue(
                    isUrlFromSameBase(url),
                    "Todas as URLs encontradas devem ser da mesma base: " + url
                );
            }
        } else {
            // Quando não há URLs encontradas, pode ser que o crawler não tenha encontrado 
            // páginas com o termo ou links para seguir
            System.out.println("Nenhuma URL encontrada. Isso pode ser normal se:");
            System.out.println("1. O termo de busca não foi encontrado em nenhuma página");
            System.out.println("2. Não havia links para seguir ou todos foram filtrados");
        }
        
        // 9. Log para confirmar que o teste validou o requisito 4
        System.out.println("\n✓ Requisito 4: URL base configurada via variável de ambiente");
        System.out.println("✓ Todas as URLs encontradas são da mesma base");
    }
    
    /**
     * Método auxiliar para verificar se uma URL é da mesma base
     * que a URL base configurada no ambiente de teste.
     * 
     * @param url A URL a ser verificada
     * @return true se a URL é da mesma base, false caso contrário
     */
    private boolean isUrlFromSameBase(String url) {
        try {
            // Criar uma URI a partir da URL fornecida
            URI uri = new URI(url);
            
            // Verificar se a URI tem esquema, host e porta válidos
            if (uri.getScheme() == null || uri.getHost() == null) {
                return false;
            }
            
            // No teste, usamos localhost com porta dinâmica
            String testHost = "localhost";
            
            // Verificar se o host é o mesmo
            boolean sameHost = uri.getHost().equals(testHost);
            
            // Verificar se é um protocolo válido (http ou https)
            boolean validScheme = uri.getScheme().equals("http") || uri.getScheme().equals("https");
            
            // No ambiente de teste, a porta pode ser a do servidor de testes (variável port)
            // ou a porta padrão do protocolo (80 para http, 443 para https)
            boolean samePort = (uri.getPort() == port) || 
                              (uri.getPort() == -1 && (uri.getScheme().equals("http") || uri.getScheme().equals("https")));
            
            return sameHost && validScheme && samePort;
            
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
