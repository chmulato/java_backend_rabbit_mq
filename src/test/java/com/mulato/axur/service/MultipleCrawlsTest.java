package com.mulato.axur.service;

import com.mulato.axur.model.CrawlResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o Requisito 5: Múltiplas buscas simultâneas
 * 
 * Testa a capacidade de executar múltiplas buscas simultaneamente:
 * - Buscas em paralelo sem conflitos
 * - Manutenção de informações de todas as buscas
 * - Status independente para cada busca
 * - Persistência de dados durante execução da aplicação
 */
@SpringBootTest
@DisplayName("Requisito 5: Múltiplas Buscas Simultâneas")
public class MultipleCrawlsTest {

    @Autowired
    private CrawlService crawlService;

    @Test
    @DisplayName("Deve suportar múltiplas buscas simultâneas sem conflitos")
    @Timeout(value = 30, unit = TimeUnit.SECONDS)
    public void testMultiplasBuscasSimultaneas() throws Exception {
        // Arrange
        String[] keywords = {"java", "spring", "boot", "test", "mock"};
        ExecutorService executor = Executors.newFixedThreadPool(keywords.length);

        try {
            // Act - Inicia múltiplas buscas simultaneamente
            List<CompletableFuture<String>> futures = IntStream.range(0, keywords.length)
                    .mapToObj(i -> CompletableFuture.supplyAsync(() -> 
                        crawlService.startCrawl(keywords[i]), executor))
                    .toList();

            // Aguarda conclusão de todas as submissões
            List<String> ids = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

            // Assert
            assertEquals(keywords.length, ids.size(), 
                        "Todas as buscas devem ter sido iniciadas");
            
            // Verifica se todos os IDs são únicos
            assertEquals(keywords.length, ids.stream().distinct().count(), 
                        "Todos os IDs devem ser únicos");

            // Verifica se todas as buscas foram registradas
            for (String id : ids) {
                CrawlResult result = crawlService.getCrawlResult(id);
                assertNotNull(result, "Resultado não deve ser nulo para ID: " + id);
                assertNotNull(result.getId(), "ID do resultado não deve ser nulo");
                assertNotNull(result.getStatus(), "Status não deve ser nulo");
                assertTrue(List.of("active", "done").contains(result.getStatus()), 
                          "Status deve ser 'active' ou 'done'");
            }

        } finally {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        }
    }

    @Test
    @DisplayName("Informações de buscas devem ser mantidas indefinidamente")
    public void testInformacoesMantidasIndefinidamente() {
        // Arrange
        String id1 = crawlService.startCrawl("persistent");
        String id2 = crawlService.startCrawl("memory");

        // Act - Aguarda um tempo para simular uso prolongado
        try {
            Thread.sleep(1000); // Simula uso da aplicação
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        // Verifica se as informações ainda estão disponíveis
        CrawlResult result1 = crawlService.getCrawlResult(id1);
        CrawlResult result2 = crawlService.getCrawlResult(id2);

        // Assert
        assertNotNull(result1, "Primeira busca deve estar mantida na memória");
        assertNotNull(result2, "Segunda busca deve estar mantida na memória");
        assertEquals(id1, result1.getId(), "ID da primeira busca deve ser mantido");
        assertEquals(id2, result2.getId(), "ID da segunda busca deve ser mantido");
    }

    @Test
    @DisplayName("Status de buscas devem ser independentes")
    public void testStatusIndependentes() {
        // Arrange
        String id1 = crawlService.startCrawl("independent1");
        String id2 = crawlService.startCrawl("independent2");

        // Act
        CrawlResult result1 = crawlService.getCrawlResult(id1);
        CrawlResult result2 = crawlService.getCrawlResult(id2);

        // Assert
        assertNotNull(result1, "Primeira busca deve existir");
        assertNotNull(result2, "Segunda busca deve existir");
        
        // Verifica que são instâncias independentes
        assertNotSame(result1, result2, "Resultados devem ser objetos diferentes");
        assertNotEquals(result1.getId(), result2.getId(), "IDs devem ser diferentes");
        
        // Cada busca pode ter seu próprio status
        assertTrue(List.of("active", "done").contains(result1.getStatus()));
        assertTrue(List.of("active", "done").contains(result2.getStatus()));
    }

    @Test
    @DisplayName("Deve suportar alta concorrência sem perda de dados")
    @Timeout(value = 45, unit = TimeUnit.SECONDS)
    public void testAltaConcorrencia() throws Exception {
        // Arrange
        int numberOfCrawls = 20;
        ExecutorService executor = Executors.newFixedThreadPool(10);

        try {
            // Act - Cria muitas buscas simultâneas
            List<CompletableFuture<String>> futures = IntStream.range(0, numberOfCrawls)
                    .mapToObj(i -> CompletableFuture.supplyAsync(() -> 
                        crawlService.startCrawl("concurrent" + i), executor))
                    .toList();

            List<String> allIds = futures.stream()
                    .map(CompletableFuture::join)
                    .toList();

            // Assert
            assertEquals(numberOfCrawls, allIds.size(), 
                        "Todas as buscas devem ter sido criadas");
            
            assertEquals(numberOfCrawls, allIds.stream().distinct().count(), 
                        "Todos os IDs devem ser únicos");

            // Verifica integridade de cada busca
            for (String id : allIds) {
                CrawlResult result = crawlService.getCrawlResult(id);
                assertNotNull(result, "Busca deve existir para ID: " + id);
                assertEquals(id, result.getId(), "ID deve corresponder");
                assertNotNull(result.getStatus(), "Status deve estar definido");
            }

        } finally {
            executor.shutdown();
            executor.awaitTermination(10, TimeUnit.SECONDS);
        }
    }

    @Test
    @DisplayName("Buscas com mesmo termo devem ser tratadas independentemente")
    public void testBuscasComMesmoTermoIndependentes() {
        // Arrange & Act
        String id1 = crawlService.startCrawl("duplicate");
        String id2 = crawlService.startCrawl("duplicate");
        String id3 = crawlService.startCrawl("duplicate");

        // Assert
        assertNotEquals(id1, id2, "IDs devem ser diferentes mesmo com termo igual");
        assertNotEquals(id2, id3, "IDs devem ser diferentes mesmo com termo igual");
        assertNotEquals(id1, id3, "IDs devem ser diferentes mesmo com termo igual");

        // Verifica que todas as buscas existem independentemente
        CrawlResult result1 = crawlService.getCrawlResult(id1);
        CrawlResult result2 = crawlService.getCrawlResult(id2);
        CrawlResult result3 = crawlService.getCrawlResult(id3);

        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);

        // Cada uma deve ter seu próprio estado
        assertNotSame(result1, result2);
        assertNotSame(result2, result3);
        assertNotSame(result1, result3);
    }
}
