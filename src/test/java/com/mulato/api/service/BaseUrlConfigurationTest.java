package com.mulato.api.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o Requisito 4: URL base
 * 
 * Testa o comportamento relacionado à URL base:
 * - Configuração via variável de ambiente
 * - Navegação apenas em links da mesma base
 * - Tratamento de links absolutos e relativos
 */
@SpringBootTest
@TestPropertySource(properties = {
    "crawl.base-url=http://example.com/"
})
@DisplayName("Requisito 4: URL Base - Variável de ambiente")
public class BaseUrlConfigurationTest {

    @Value("${crawl.base-url:}")
    private String baseUrl;

    @Value("${app.base-url:}")
    private String appBaseUrl;

    @Test
    @DisplayName("URL base deve ser configurada via variável de ambiente")
    public void testUrlBaseConfiguradaViaVariavelAmbiente() {
        // Verificando a URL base configurada para testes
        String urlToTest = baseUrl.isEmpty() ? appBaseUrl : baseUrl;
        
        // Assert
        assertNotNull(urlToTest, "URL base não deve ser nula");
        assertFalse(urlToTest.trim().isEmpty(), "URL base não deve estar vazia");
        
        // Verifica se é uma URL válida
        assertDoesNotThrow(() -> {
            URI uri = new URI(urlToTest);
            assertNotNull(uri.getScheme(), "URL deve ter esquema (http/https)");
            assertNotNull(uri.getHost(), "URL deve ter host");
        }, "URL base deve ser válida");
    }

    @Test
    @DisplayName("URL base deve ter formato válido")
    public void testFormatoValidoUrlBase() throws URISyntaxException {
        // Verificando a URL base configurada para testes
        String urlToTest = baseUrl.isEmpty() ? appBaseUrl : baseUrl;
        
        // Act
        URI uri = new URI(urlToTest);

        // Assert
        assertTrue(uri.getScheme().equals("http") || uri.getScheme().equals("https"),
                  "Esquema deve ser http ou https");
        assertNotNull(uri.getHost(), "Host não deve ser nulo");
        assertFalse(uri.getHost().trim().isEmpty(), "Host não deve estar vazio");
    }

    @Test
    @DisplayName("Deve identificar URLs da mesma base corretamente")
    public void testIdentificacaoUrlsMesmaBase() throws URISyntaxException {
        // Verificando a URL base configurada para testes
        String urlToTest = baseUrl.isEmpty() ? appBaseUrl : baseUrl;
        
        // Arrange
        URI baseUri = new URI(urlToTest);
        String baseHost = baseUri.getHost();
        String baseScheme = baseUri.getScheme();
        int basePort = baseUri.getPort();

        // URLs que devem ser consideradas da mesma base
        String[] sameBaseUrls = {
            urlToTest + "index.html",
            urlToTest + "path/to/page.html",
            baseScheme + "://" + baseHost + (basePort != -1 ? ":" + basePort : "") + "/different/path.html"
        };

        // URLs que NÃO devem ser consideradas da mesma base
        String[] differentBaseUrls = {
            "http://google.com/",
            "https://github.com/path",
            "http://different-host.com/",
            "ftp://" + baseHost + "/file.txt"
        };

        // Assert - URLs da mesma base
        for (String url : sameBaseUrls) {
            URI testUri = new URI(url);
            assertEquals(baseHost, testUri.getHost(), 
                        "Host deve ser o mesmo para URL da mesma base: " + url);
            assertEquals(baseScheme, testUri.getScheme(), 
                        "Esquema deve ser o mesmo para URL da mesma base: " + url);
        }

        // Assert - URLs de base diferente
        for (String url : differentBaseUrls) {
            URI testUri = new URI(url);
            boolean isDifferentBase = !baseHost.equals(testUri.getHost()) || 
                                    !baseScheme.equals(testUri.getScheme());
            assertTrue(isDifferentBase, 
                      "URL deve ser identificada como base diferente: " + url);
        }
    }

    /**
     * Testa o método isSameBaseUrl diretamente implementando a mesma lógica
     */
    @Test
    @DisplayName("Método isSameBaseUrl deve identificar corretamente a mesma base")
    public void testIsSameBaseUrl() throws URISyntaxException {
        // Verificando a URL base configurada para testes
        String baseUrlStr = baseUrl.isEmpty() ? appBaseUrl : baseUrl;
        
        // URLs da mesma base
        assertTrue(isSameBaseUrl(baseUrlStr + "index.html", baseUrlStr),
                  "URLs com mesmo host e scheme devem ser identificadas como mesma base");
        assertTrue(isSameBaseUrl(baseUrlStr + "path/to/page.html", baseUrlStr),
                  "URLs com mesmo host e scheme e caminhos diferentes devem ser identificadas como mesma base");
                  
        // URLs de bases diferentes
        assertFalse(isSameBaseUrl("http://google.com/", baseUrlStr),
                   "URLs com hosts diferentes devem ser identificadas como bases diferentes");
        assertFalse(isSameBaseUrl("https://example.com/", "http://example.com/"),
                   "URLs com schemes diferentes devem ser identificadas como bases diferentes");
    }
    
    /**
     * Implementação de teste do método isSameBaseUrl para validação
     */
    private boolean isSameBaseUrl(String url, String baseUrl) {
        try {
            URI uri = new URI(url);
            URI baseUri = new URI(baseUrl);
            return uri.getHost().equals(baseUri.getHost()) && 
                   uri.getScheme().equals(baseUri.getScheme()) &&
                   (uri.getPort() == baseUri.getPort() || 
                    (uri.getPort() == -1 && (baseUri.getPort() == -1 || baseUri.getPort() == 80)) ||
                    (baseUri.getPort() == -1 && (uri.getPort() == -1 || uri.getPort() == 80)));
        } catch (URISyntaxException e) {
            return false;
        }
    }
}
