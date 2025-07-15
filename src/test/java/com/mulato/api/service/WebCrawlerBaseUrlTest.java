package com.mulato.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Teste unitário específico para o requisito 4 do desafio:
 * 
 * "A URL base do website em que as análises são realizadas é determinada por uma 
 * variável de ambiente. As buscas devem seguir links (absolutos e relativos) em 
 * elementos anchor das páginas visitadas se e somente se eles possuírem a mesma URL base."
 * 
 * Este teste valida diretamente a lógica do método isSameBaseUrl() no WebCrawlerService,
 * garantindo que apenas URLs da mesma base sejam seguidas durante o crawling.
 */
@DisplayName("Requisito 4: Teste da verificação de URLs com mesma base")
public class WebCrawlerBaseUrlTest {

    /**
     * Testa o método isSameBaseUrl() usando reflection para acessar o método privado.
     * Valida se URLs com mesmo host, porta e esquema são consideradas da mesma base,
     * e se URLs com hosts, portas ou esquemas diferentes são rejeitadas.
     */
    @Test
    @DisplayName("Verificação de URLs da mesma base - Casos básicos")
    public void testIsSameBaseUrlBasicCases() {
        // Arrange
        WebCrawlerService webCrawlerService = new WebCrawlerService();
        String baseUrl = "http://example.com/";

        // Act & Assert - Mesmo host, porta e esquema (deve retornar true)
        boolean sameBase = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "http://example.com/index.html", 
                baseUrl
        );
        assertTrue(sameBase, "URLs com mesmo host, porta e esquema devem ser consideradas da mesma base");

        // Act & Assert - Host diferente (deve retornar false)
        boolean differentHost = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "http://example.com/page.html", 
                baseUrl
        );
        assertFalse(differentHost, "URLs com hosts diferentes devem ser consideradas de bases diferentes");

        // Act & Assert - Esquema diferente (deve retornar false)
        boolean differentScheme = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "https://example.com/index.html", 
                baseUrl
        );
        assertFalse(differentScheme, "URLs com esquemas diferentes devem ser consideradas de bases diferentes");

        // Act & Assert - Porta diferente (deve retornar false)
        boolean differentPort = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "http://example.com:8080/index.html", 
                baseUrl
        );
        assertFalse(differentPort, "URLs com portas diferentes devem ser consideradas de bases diferentes");
    }

    /**
     * Testa vários cenários de URLs usando ParameterizedTest para validar
     * a consistência do método isSameBaseUrl().
     */
    @ParameterizedTest(name = "URL {0} com base {1} deve retornar {2}")
    @CsvSource({
        // Casos onde as URLs são da mesma base (devem retornar true)
        "http://example.com/index.html, http://example.com/, true",
        "http://example.com/path/page.html, http://example.com/, true",
        "http://example.com:80/index.html, http://example.com:80/, true",
        "https://example.com/index.html, https://example.com/, true",
        
        // Casos onde as URLs são de bases diferentes (devem retornar false)
        "http://subdomain.example.com/page.html, http://example.com/, false",
        "http://example.com:8080/index.html, http://example.com/, false",
        "https://example.com/index.html, http://example.com/, false",
        "http://other.com/page.html, http://example.com/, false",
        "http://attacker.com/malicious.html, http://example.com/, false"
    })
    @DisplayName("Verificação de URLs da mesma base - Múltiplos cenários")
    public void testMultipleUrlScenarios(String url, String baseUrl, boolean expected) {
        // Arrange
        WebCrawlerService webCrawlerService = new WebCrawlerService();

        // Act - usando reflection para acessar o método privado
        boolean result = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                url,
                baseUrl
        );

        // Assert
        assertEquals(expected, result,
                String.format("URL '%s' com base '%s' deveria retornar '%s'", url, baseUrl, expected));
    }
    
    /**
     * Testa casos especiais como URLs inválidas ou malformadas.
     */
    @Test
    @DisplayName("Verificação de URLs da mesma base - Casos especiais")
    public void testSpecialUrlCases() {
        // Arrange
        WebCrawlerService webCrawlerService = new WebCrawlerService();
        String baseUrl = "http://example.com/";
        
        // Act & Assert - URL mal formatada deve retornar false
        boolean resultInvalidUrl = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "not a valid url", 
                baseUrl
        );
        assertFalse(resultInvalidUrl, "URLs mal formatadas não devem ser consideradas da mesma base");

        // Act & Assert - Base URL mal formatada deve retornar false
        boolean resultInvalidBase = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "http://example.com/", 
                "not a valid base url"
        );
        assertFalse(resultInvalidBase, "Quando a base URL é inválida, o método deve retornar false");

        // Act & Assert - URLs com esquemas não HTTP/HTTPS devem retornar false
        boolean resultMaliciousScheme = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "javascript:alert('xss')", 
                baseUrl
        );
        assertFalse(resultMaliciousScheme, "URLs com esquemas maliciosos não devem ser consideradas da mesma base");
    }
}
