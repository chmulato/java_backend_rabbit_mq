package com.mulato.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes específicos para a função isSameBaseUrl do WebCrawlerService
 * Verifica se apenas URLs da mesma base são seguidos durante o crawling
 * Requisito 4: A URL base é determinada por variável de ambiente.
 * As buscas devem seguir links apenas se eles possuírem a mesma URL base.
 */
@DisplayName("Requisito 4: Teste para garantir que apenas links da mesma base sejam seguidos")
public class WebCrawlerSameBaseUrlTest {

    @Test
    @DisplayName("Teste de método isSameBaseUrl via Reflection")
    public void testIsSameBaseUrlMethod() throws Exception {
        // Arrange
        WebCrawlerService webCrawlerService = new WebCrawlerService();

        // Act - usando reflection para acessar o método privado
        boolean sameHost = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "http://example.com/index.html", 
                "http://example.com/"
        );

        boolean differentHost = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "http://other.com/page.html", 
                "http://example.com/"
        );

        boolean differentScheme = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "https://example.com/index.html", 
                "http://example.com/"
        );

        boolean differentPort = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "http://example.com:8080/index.html", 
                "http://example.com/"
        );

        // Assert
        assertTrue(sameHost, "URLs com o mesmo host, porta e esquema devem ser consideradas da mesma base");
        assertFalse(differentHost, "URLs com hosts diferentes devem ser consideradas de bases diferentes");
        assertFalse(differentScheme, "URLs com esquemas diferentes devem ser consideradas de bases diferentes");
        assertFalse(differentPort, "URLs com portas diferentes devem ser consideradas de bases diferentes");
    }

    @ParameterizedTest(name = "URL {0} com base {1} deve retornar {2}")
    @CsvSource({
        "http://example.com/index.html, http://example.com/, true",
        "http://example.com/path/page.html, http://example.com/, true",
        "http://example.com:80/index.html, http://example.com:80/, true",
        "https://example.com/index.html, https://example.com/, true",
        "http://subdomain.example.com/page.html, http://example.com/, false",
        "http://example.com:8080/index.html, http://example.com/, false",
        "https://example.com/index.html, http://example.com/, false",
        "http://other.com/page.html, http://example.com/, false",
        "http://attacker.com/malicious.html, http://example.com/, false",
        "http://127.0.0.1/local.html, http://example.com/, false",
        "file:///etc/passwd, http://example.com/, false",
        "javascript:alert('xss'), http://example.com/, false"
    })
    public void testVariousUrlScenarios(String url, String baseUrl, boolean expected) {
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
    
    @Test
    @DisplayName("Teste para URLs inválidas")
    public void testInvalidUrls() {
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
