package com.mulato.axur.service;

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
                "http://hiring.axreng.com/index.html", 
                "http://hiring.axreng.com/"
        );

        boolean differentHost = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "http://example.com/page.html", 
                "http://hiring.axreng.com/"
        );

        boolean differentScheme = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "https://hiring.axreng.com/index.html", 
                "http://hiring.axreng.com/"
        );

        boolean differentPort = (boolean) ReflectionTestUtils.invokeMethod(
                webCrawlerService, 
                "isSameBaseUrl", 
                "http://hiring.axreng.com:8080/index.html", 
                "http://hiring.axreng.com/"
        );

        // Assert
        assertTrue(sameHost, "URLs com o mesmo host, porta e esquema devem ser consideradas da mesma base");
        assertFalse(differentHost, "URLs com hosts diferentes devem ser consideradas de bases diferentes");
        assertFalse(differentScheme, "URLs com esquemas diferentes devem ser consideradas de bases diferentes");
        assertFalse(differentPort, "URLs com portas diferentes devem ser consideradas de bases diferentes");
    }

    @ParameterizedTest(name = "URL {0} com base {1} deve retornar {2}")
    @CsvSource({
        "http://hiring.axreng.com/index.html, http://hiring.axreng.com/, true",
        "http://hiring.axreng.com/path/page.html, http://hiring.axreng.com/, true",
        "http://hiring.axreng.com:80/index.html, http://hiring.axreng.com:80/, true",
        "https://hiring.axreng.com/index.html, https://hiring.axreng.com/, true",
        "http://subdomain.hiring.axreng.com/page.html, http://hiring.axreng.com/, false",
        "http://hiring.axreng.com:8080/index.html, http://hiring.axreng.com/, false",
        "https://hiring.axreng.com/index.html, http://hiring.axreng.com/, false",
        "http://example.com/page.html, http://hiring.axreng.com/, false",
        "http://attacker.com/malicious.html, http://hiring.axreng.com/, false",
        "http://127.0.0.1/local.html, http://hiring.axreng.com/, false",
        "file:///etc/passwd, http://hiring.axreng.com/, false",
        "javascript:alert('xss'), http://hiring.axreng.com/, false"
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
        String baseUrl = "http://hiring.axreng.com/";
        
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
                "http://hiring.axreng.com/", 
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
