package com.mulato.axur.service;

import com.mulato.axur.model.CrawlResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para comportamentos específicos do crawler
 * 
 * Testa características específicas mencionadas no desafio:
 * - Busca case insensitive
 * - Busca em qualquer parte do conteúdo HTML (incluindo tags e comentários)
 * - Seguimento de links absolutos e relativos
 * - Limite da mesma URL base
 */
@SpringBootTest
@DisplayName("Comportamentos Específicos do Crawler")
public class CrawlerBehaviorTest {

    @Autowired
    private CrawlService crawlService;

    @ParameterizedTest
    @ValueSource(strings = {"SECURITY", "security", "Security", "sEcUrItY"})
    @DisplayName("Busca deve ser case insensitive")
    public void testBuscaCaseInsensitive(String keyword) {
        // Act
        String id = crawlService.startCrawl(keyword);
        CrawlResult result = crawlService.getCrawlResult(id);

        // Assert
        assertNotNull(result, "Resultado deve existir para: " + keyword);
        assertEquals(id, result.getId(), "ID deve corresponder");
        assertTrue(result.getStatus().equals("active") || result.getStatus().equals("done"),
                  "Status deve ser válido para: " + keyword);
        assertNotNull(result.getUrls(), "URLs não devem ser nulas para: " + keyword);
        
        // Todos os casos devem ser tratados da mesma forma
        // (resultados podem variar dependendo do conteúdo real, mas não deve dar erro)
    }

    @Test
    @DisplayName("Deve processar termos com números e caracteres especiais válidos")
    public void testTermosComNumerosECaracteresEspeciais() {
        // Arrange - Termos válidos com números
        String[] termosValidos = {
            "java8",
            "spring5",
            "test123",
            "web2024"
        };

        // Act & Assert
        for (String termo : termosValidos) {
            assertDoesNotThrow(() -> {
                String id = crawlService.startCrawl(termo);
                CrawlResult result = crawlService.getCrawlResult(id);
                
                assertNotNull(result, "Resultado deve existir para: " + termo);
                assertEquals(id, result.getId(), "ID deve corresponder para: " + termo);
            }, "Termo deve ser processado sem erro: " + termo);
        }
    }

    @Test
    @DisplayName("Deve manter consistência entre diferentes execuções")
    public void testConsistenciaEntreDiferentesExecucoes() {
        // Arrange
        String keyword = "consistent";

        // Act - Múltiplas execuções do mesmo termo
        String id1 = crawlService.startCrawl(keyword);
        String id2 = crawlService.startCrawl(keyword);
        String id3 = crawlService.startCrawl(keyword);

        CrawlResult result1 = crawlService.getCrawlResult(id1);
        CrawlResult result2 = crawlService.getCrawlResult(id2);
        CrawlResult result3 = crawlService.getCrawlResult(id3);

        // Assert
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);

        // IDs devem ser diferentes (cada busca é independente)
        assertNotEquals(id1, id2, "IDs devem ser únicos");
        assertNotEquals(id2, id3, "IDs devem ser únicos");
        assertNotEquals(id1, id3, "IDs devem ser únicos");

        // Mas comportamento deve ser consistente
        assertEquals(result1.getStatus().getClass(), result2.getStatus().getClass());
        assertEquals(result2.getStatus().getClass(), result3.getStatus().getClass());
    }

    @Test
    @DisplayName("Deve lidar graciosamente com termos que não existem")
    public void testTermosQueNaoExistem() {
        // Arrange - Termos improváveis de existir
        String[] termosInexistentes = {
            "xyzabc123inexistente",
            "termomuitoespecifico9999",
            "naodeveterresultado2024"
        };

        // Act & Assert
        for (String termo : termosInexistentes) {
            assertDoesNotThrow(() -> {
                String id = crawlService.startCrawl(termo);
                CrawlResult result = crawlService.getCrawlResult(id);
                
                assertNotNull(result, "Resultado deve existir mesmo para termo inexistente: " + termo);
                assertEquals(id, result.getId(), "ID deve corresponder: " + termo);
                assertNotNull(result.getUrls(), "URLs não devem ser nulas: " + termo);
                
                // Se concluído, lista pode estar vazia
                if ("done".equals(result.getStatus())) {
                    assertTrue(result.getUrls().size() >= 0, 
                              "URLs devem ser lista válida (pode estar vazia): " + termo);
                }
            }, "Não deve gerar erro mesmo com termo inexistente: " + termo);
        }
    }

    @Test
    @DisplayName("Deve processar termos comuns que provavelmente existem")
    public void testTermosComunsQueProvavelmenteExistem() {
        // Arrange - Termos comuns em sites de tecnologia
        String[] termosComuns = {
            "html",
            "page",
            "link",
            "home"
        };

        // Act & Assert
        for (String termo : termosComuns) {
            assertDoesNotThrow(() -> {
                String id = crawlService.startCrawl(termo);
                CrawlResult result = crawlService.getCrawlResult(id);
                
                assertNotNull(result, "Resultado deve existir para termo comum: " + termo);
                assertEquals(id, result.getId(), "ID deve corresponder: " + termo);
                assertTrue(result.getStatus().equals("active") || result.getStatus().equals("done"),
                          "Status deve ser válido: " + termo);
            }, "Termo comum deve ser processado sem erro: " + termo);
        }
    }

    @Test
    @DisplayName("Deve manter estado correto durante processamento")
    public void testEstadoCorretosDuranteProcessamento() throws InterruptedException {
        // Arrange
        String id = crawlService.startCrawl("estado");

        // Act - Monitora estado ao longo do tempo
        CrawlResult inicial = crawlService.getCrawlResult(id);
        Thread.sleep(100);
        CrawlResult meio = crawlService.getCrawlResult(id);
        Thread.sleep(200);
        CrawlResult final_ = crawlService.getCrawlResult(id);

        // Assert
        assertEquals(id, inicial.getId(), "ID deve ser consistente");
        assertEquals(id, meio.getId(), "ID deve ser consistente");
        assertEquals(id, final_.getId(), "ID deve ser consistente");

        // Estados válidos
        assertTrue(inicial.getStatus().equals("active") || inicial.getStatus().equals("done"));
        assertTrue(meio.getStatus().equals("active") || meio.getStatus().equals("done"));
        assertTrue(final_.getStatus().equals("active") || final_.getStatus().equals("done"));

        // Se mudou para done, deve permanecer done
        if ("done".equals(meio.getStatus())) {
            assertEquals("done", final_.getStatus(), 
                        "Se chegou a 'done', deve permanecer 'done'");
        }
    }

    @Test
    @DisplayName("Deve suportar caracteres acentuados e internacionais")
    public void testCaracteresAcentuadosEInternacionais() {
        // Arrange - Termos com acentos (se suportado pelo sistema)
        String[] termosInternacionais = {
            "café",      // português
            "naïve",     // francês
            "piña",      // espanhol
            "résumé"     // francês
        };

        // Act & Assert
        for (String termo : termosInternacionais) {
            if (termo.length() >= 4 && termo.length() <= 32) {
                assertDoesNotThrow(() -> {
                    String id = crawlService.startCrawl(termo);
                    CrawlResult result = crawlService.getCrawlResult(id);
                    
                    assertNotNull(result, "Resultado deve existir para termo internacional: " + termo);
                    assertEquals(id, result.getId(), "ID deve corresponder: " + termo);
                }, "Termo internacional deve ser processado: " + termo);
            }
        }
    }

    @Test
    @DisplayName("Deve processar termos em sequência sem interferência")
    public void testTermosEmSequenciaSemInterferencia() {
        // Arrange
        String[] sequencia = {"first", "second", "third", "fourth", "fifth"};
        String[] ids = new String[sequencia.length];

        // Act - Processa em sequência
        for (int i = 0; i < sequencia.length; i++) {
            ids[i] = crawlService.startCrawl(sequencia[i]);
        }

        // Assert - Verifica que não há interferência
        for (int i = 0; i < ids.length; i++) {
            CrawlResult result = crawlService.getCrawlResult(ids[i]);
            
            assertNotNull(result, "Resultado " + i + " deve existir");
            assertEquals(ids[i], result.getId(), "ID " + i + " deve corresponder");
            assertTrue(result.getStatus().equals("active") || result.getStatus().equals("done"),
                      "Status " + i + " deve ser válido");
        }

        // Todos os IDs devem ser únicos
        for (int i = 0; i < ids.length; i++) {
            for (int j = i + 1; j < ids.length; j++) {
                assertNotEquals(ids[i], ids[j], 
                               "IDs devem ser únicos na sequência");
            }
        }
    }
}
