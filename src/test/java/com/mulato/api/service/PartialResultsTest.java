package com.mulato.api.service;

import com.mulato.api.model.CrawlResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.RepeatedTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o Requisito 6: Resultados parciais
 * 
 * Testa a capacidade de retornar resultados parciais durante execução:
 * - Resultados parciais disponíveis durante busca ativa
 * - URLs encontradas incrementalmente
 * - Status correto durante e após execução
 */
@SpringBootTest
@DisplayName("Requisito 6: Resultados Parciais")
public class PartialResultsTest {

    @Autowired
    private CrawlService crawlService;

    @Test
    @DisplayName("Deve retornar resultados parciais durante busca ativa")
    public void testResultadosParciaisDuranteBuscaAtiva() throws InterruptedException {
        // Arrange
        String id = crawlService.startCrawl("test");

        // Act - Consulta imediatamente após iniciar
        CrawlResult immediateResult = crawlService.getCrawlResult(id);

        // Assert inicial
        assertNotNull(immediateResult, "Resultado deve estar disponível imediatamente");
        assertEquals(id, immediateResult.getId(), "ID deve corresponder");
        
        // Durante busca ativa, status pode ser "active" ou "done" se for muito rápida
        assertTrue(List.of("active", "done").contains(immediateResult.getStatus()),
                  "Status deve ser 'active' ou 'done'");
        
        assertNotNull(immediateResult.getUrls(), "Lista de URLs não deve ser nula");
        
        // Aguarda um pouco e verifica novamente
        Thread.sleep(100);
        CrawlResult laterResult = crawlService.getCrawlResult(id);
        
        assertNotNull(laterResult, "Resultado deve continuar disponível");
        assertEquals(id, laterResult.getId(), "ID deve permanecer o mesmo");
        assertTrue(List.of("active", "done").contains(laterResult.getStatus()),
                  "Status deve ser válido");
    }

    @Test
    @DisplayName("URLs encontradas devem ser incrementalmente adicionadas")
    public void testUrlsIncrementalmenteAdicionadas() throws InterruptedException {
        // Arrange
        String id = crawlService.startCrawl("link");

        // Act - Múltiplas consultas ao longo do tempo
        CrawlResult result1 = crawlService.getCrawlResult(id);
        int initialUrlCount = result1.getUrls().size();

        // Aguarda processamento
        Thread.sleep(200);
        
        CrawlResult result2 = crawlService.getCrawlResult(id);
        int laterUrlCount = result2.getUrls().size();

        // Assert
        assertEquals(id, result1.getId(), "ID deve ser consistente");
        assertEquals(id, result2.getId(), "ID deve ser consistente");
        
        assertNotNull(result1.getUrls(), "URLs iniciais não devem ser nulas");
        assertNotNull(result2.getUrls(), "URLs posteriores não devem ser nulas");
        
        // URLs encontradas devem ser mantidas (não podem diminuir)
        assertTrue(laterUrlCount >= initialUrlCount,
                  "Número de URLs não deve diminuir ao longo do tempo");
        
        // Se ainda ativo, pode ter mais URLs; se done, deve ter mesmo número
        if ("active".equals(result1.getStatus()) && "done".equals(result2.getStatus())) {
            assertTrue(laterUrlCount >= initialUrlCount,
                      "URLs devem ser incrementadas ou mantidas");
        }
    }

    @Test
    @DisplayName("Status deve transicionar corretamente de active para done")
    public void testTransicaoStatusActiveDone() throws InterruptedException {
        // Arrange
        String id = crawlService.startCrawl("status");

        // Act - Monitora mudança de status
        CrawlResult initialResult = crawlService.getCrawlResult(id);
        
        // Aguarda conclusão potencial
        int maxAttempts = 50; // máximo 5 segundos
        CrawlResult finalResult = null;
        
        for (int i = 0; i < maxAttempts; i++) {
            finalResult = crawlService.getCrawlResult(id);
            if ("done".equals(finalResult.getStatus())) {
                break;
            }
            Thread.sleep(100);
        }

        // Assert
        assertNotNull(initialResult, "Resultado inicial deve existir");
        assertNotNull(finalResult, "Resultado final deve existir");
        
        assertEquals(id, initialResult.getId(), "ID inicial deve ser correto");
        assertEquals(id, finalResult.getId(), "ID final deve ser correto");
        
        // Status deve ser válido em ambos os momentos
        assertTrue(List.of("active", "done").contains(initialResult.getStatus()),
                  "Status inicial deve ser válido");
        assertTrue(List.of("active", "done").contains(finalResult.getStatus()),
                  "Status final deve ser válido");
        
        // Se mudou de status, deve ter sido de active para done
        if (!initialResult.getStatus().equals(finalResult.getStatus())) {
            assertEquals("active", initialResult.getStatus(),
                        "Status inicial deve ser 'active' se houver mudança");
            assertEquals("done", finalResult.getStatus(),
                        "Status final deve ser 'done' se houver mudança");
        }
    }

    @RepeatedTest(3)
    @DisplayName("Resultados parciais devem ser consistentes entre consultas")
    public void testConsistenciaResultadosParciais() throws InterruptedException {
        // Arrange
        String id = crawlService.startCrawl("consistent");

        // Act - Múltiplas consultas rápidas
        CrawlResult result1 = crawlService.getCrawlResult(id);
        CrawlResult result2 = crawlService.getCrawlResult(id);
        CrawlResult result3 = crawlService.getCrawlResult(id);

        // Assert
        assertEquals(result1.getId(), result2.getId(), "IDs devem ser consistentes");
        assertEquals(result2.getId(), result3.getId(), "IDs devem ser consistentes");
        
        assertNotNull(result1.getUrls(), "URLs não devem ser nulas");
        assertNotNull(result2.getUrls(), "URLs não devem ser nulas");
        assertNotNull(result3.getUrls(), "URLs não devem ser nulas");
        
        // URLs não devem desaparecer entre consultas
        if ("done".equals(result1.getStatus()) && "done".equals(result2.getStatus())) {
            assertEquals(result1.getUrls().size(), result2.getUrls().size(),
                        "URLs devem ser consistentes quando status é 'done'");
        }
    }

    @Test
    @DisplayName("Deve manter histórico completo de URLs encontradas")
    public void testHistoricoCompletoUrls() throws InterruptedException {
        // Arrange
        String id = crawlService.startCrawl("history");

        // Act - Coleta snapshots ao longo do tempo
        CrawlResult snapshot1 = crawlService.getCrawlResult(id);
        Thread.sleep(150);
        
        CrawlResult snapshot2 = crawlService.getCrawlResult(id);
        Thread.sleep(150);
        
        CrawlResult snapshot3 = crawlService.getCrawlResult(id);

        // Assert
        assertNotNull(snapshot1.getUrls(), "Snapshot 1 deve ter lista de URLs");
        assertNotNull(snapshot2.getUrls(), "Snapshot 2 deve ter lista de URLs");
        assertNotNull(snapshot3.getUrls(), "Snapshot 3 deve ter lista de URLs");
        
        // URLs não devem ser perdidas entre snapshots
        int count1 = snapshot1.getUrls().size();
        int count2 = snapshot2.getUrls().size();
        int count3 = snapshot3.getUrls().size();
        
        assertTrue(count2 >= count1, "URLs não devem diminuir do snapshot 1 para 2");
        assertTrue(count3 >= count2, "URLs não devem diminuir do snapshot 2 para 3");
        
        // Se busca concluída, todos os snapshots finais devem ter mesmo resultado
        if ("done".equals(snapshot2.getStatus()) && "done".equals(snapshot3.getStatus())) {
            assertEquals(count2, count3, "URLs devem ser iguais quando busca concluída");
        }
    }

    @Test
    @DisplayName("Deve suportar consultas frequentes sem degradação")
    public void testConsultasFrequentesSemDegradacao() {
        // Arrange
        String id = crawlService.startCrawl("frequent");

        // Act - Muitas consultas rápidas
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < 20; i++) {
            CrawlResult result = crawlService.getCrawlResult(id);
            
            // Assert em cada consulta
            assertNotNull(result, "Resultado deve estar sempre disponível");
            assertEquals(id, result.getId(), "ID deve ser consistente");
            assertTrue(List.of("active", "done").contains(result.getStatus()),
                      "Status deve ser válido");
            assertNotNull(result.getUrls(), "URLs devem estar disponíveis");
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Consultas não devem ser excessivamente lentas (menos de 5 segundos para 20 consultas)
        assertTrue(duration < 5000, 
                  "20 consultas não devem demorar mais que 5 segundos");
    }
}
