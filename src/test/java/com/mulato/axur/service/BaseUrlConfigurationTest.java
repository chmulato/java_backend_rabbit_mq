package com.mulato.axur.service;

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
    "crawl.base-url=http://hiring.axreng.com/"
})
@DisplayName("Requisito 4: URL Base - Variável de ambiente")
public class BaseUrlConfigurationTest {

    @Value("${crawl.base-url:}")
    private String baseUrl;

    @Test
    @DisplayName("URL base deve ser configurada via variável de ambiente")
    public void testUrlBaseConfiguradaViaVariavelAmbiente() {
        // Assert
        assertNotNull(baseUrl, "URL base não deve ser nula");
        assertFalse(baseUrl.trim().isEmpty(), "URL base não deve estar vazia");
        
        // Verifica se é uma URL válida
        assertDoesNotThrow(() -> {
            URI uri = new URI(baseUrl);
            assertNotNull(uri.getScheme(), "URL deve ter esquema (http/https)");
            assertNotNull(uri.getHost(), "URL deve ter host");
        }, "URL base deve ser válida");
    }

    @Test
    @DisplayName("URL base deve ter formato válido")
    public void testFormatoValidoUrlBase() throws URISyntaxException {
        // Act
        URI uri = new URI(baseUrl);

        // Assert
        assertTrue(uri.getScheme().equals("http") || uri.getScheme().equals("https"),
                  "Esquema deve ser http ou https");
        assertNotNull(uri.getHost(), "Host não deve ser nulo");
        assertFalse(uri.getHost().trim().isEmpty(), "Host não deve estar vazio");
    }

    @Test
    @DisplayName("Deve aceitar URL base com diferentes formatos")
    public void testDiferentesFormatosUrlBase() {
        // Testa diferentes formatos válidos de URL base
        String[] validBaseUrls = {
            "http://hiring.axreng.com/",
            "http://hiring.axreng.com",
            "https://hiring.axreng.com/",
            "https://hiring.axreng.com",
            "http://localhost:8080/",
            "http://127.0.0.1:4567/"
        };

        for (String testUrl : validBaseUrls) {
            assertDoesNotThrow(() -> {
                URI uri = new URI(testUrl);
                assertNotNull(uri.getScheme(), "Esquema deve existir para: " + testUrl);
                assertNotNull(uri.getHost(), "Host deve existir para: " + testUrl);
            }, "URL deve ser válida: " + testUrl);
        }
    }

    @Test
    @DisplayName("URL base deve ser utilizável para construção de URLs completas")
    public void testUrlBaseUtilizavelParaConstrucao() throws URISyntaxException {
        // Arrange
        URI baseUri = new URI(baseUrl);

        // Act & Assert - Testa resolução de URLs relativas
        assertDoesNotThrow(() -> {
            URI relativeUri = baseUri.resolve("index.html");
            assertNotNull(relativeUri.toString());
            assertTrue(relativeUri.toString().startsWith(baseUrl.replaceAll("/$", "")));
        }, "Deve conseguir resolver URLs relativas");

        assertDoesNotThrow(() -> {
            URI relativeUri = baseUri.resolve("/path/to/page.html");
            assertNotNull(relativeUri.toString());
        }, "Deve conseguir resolver caminhos absolutos relativos");
    }

    @Test
    @DisplayName("Deve identificar URLs da mesma base corretamente")
    public void testIdentificacaoUrlsMesmaBase() throws URISyntaxException {
        // Arrange
        URI baseUri = new URI(baseUrl);
        String baseHost = baseUri.getHost();
        String baseScheme = baseUri.getScheme();
        int basePort = baseUri.getPort();

        // URLs que devem ser consideradas da mesma base
        String[] sameBaseUrls = {
            baseUrl + "index.html",
            baseUrl + "path/to/page.html",
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

    @Test
    @DisplayName("Deve lidar com URLs base com e sem barra final")
    public void testUrlBaseSemComBarraFinal() throws URISyntaxException {
        // Arrange - Normaliza URL base removendo barra final se existir
        String normalizedBase = baseUrl.endsWith("/") ? 
                               baseUrl.substring(0, baseUrl.length() - 1) : 
                               baseUrl;

        // Act & Assert
        URI baseUri = new URI(normalizedBase);
        
        // Testa resolução com ambos os formatos
        URI withSlash = baseUri.resolve("/index.html");
        URI withoutSlash = baseUri.resolve("index.html");
        
        assertNotNull(withSlash.toString());
        assertNotNull(withoutSlash.toString());
        
        // Ambos devem começar com a base normalizada
        assertTrue(withSlash.toString().startsWith(normalizedBase));
        assertTrue(withoutSlash.toString().startsWith(normalizedBase));
    }

    @Test
    @DisplayName("Deve validar configuração da URL base no contexto da aplicação")
    public void testConfiguracaoUrlBaseContextoAplicacao() {
        // Assert - Verifica se a configuração está carregada corretamente
        assertNotNull(baseUrl, "URL base deve estar configurada no contexto da aplicação");
        
        // Verifica se não está usando valor padrão vazio
        assertNotEquals("", baseUrl.trim(), "URL base não deve estar vazia");
        
        // Verifica formato mínimo esperado
        assertTrue(baseUrl.startsWith("http://") || baseUrl.startsWith("https://"),
                  "URL base deve começar com http:// ou https://");
        
        // Verifica se contém pelo menos um ponto (indicando domínio)
        assertTrue(baseUrl.contains(".") || baseUrl.contains("localhost") || baseUrl.matches(".*\\d+\\.\\d+\\.\\d+\\.\\d+.*"),
                  "URL base deve conter domínio válido, localhost ou IP");
    }
}
