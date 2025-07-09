package com.mulato.axur.service;

import com.mulato.axur.util.LogCapture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class CrawlServiceLogTest {

    private LogCapture logCapture;

    @BeforeEach
    void setUp() {
        logCapture = new LogCapture(CrawlService.class);
    }

    @AfterEach
    void tearDown() {
        logCapture.stop();
    }

    @Test
    void testLogCaptureBasicFunctionality() {
        // Verifica se a captura de logs está funcionando
        assertEquals(0, logCapture.getLogCount(), "Deve iniciar sem logs");
        
        // Simula alguns logs
        logCapture.clearLogs();
        
        // Verifica se não há mensagens de erro inicialmente
        assertFalse(logCapture.hasErrorMessages(), "Não deve haver mensagens de erro inicialmente");
        assertFalse(logCapture.hasWarningMessages(), "Não deve haver mensagens de warning inicialmente");
    }

    @Test
    void testLogMessageFiltering() {
        // Testa a funcionalidade de filtro de mensagens
        logCapture.clearLogs();
        
        // Como não estamos realmente logando aqui, vamos apenas testar a estrutura
        // Em um cenário real, você faria algo como:
        // Logger logger = LoggerFactory.getLogger(CrawlService.class);
        // logger.info("Teste de mensagem");
        
        // Verifica se os métodos utilitários funcionam
        assertTrue(logCapture.getLogMessages().isEmpty(), "Lista de mensagens deve estar vazia");
        assertTrue(logCapture.getLastLogMessages(5).isEmpty(), "Últimas mensagens devem estar vazias");
    }

    @Test
    void testLogLevelFiltering() {
        // Testa a filtragem por nível de log
        logCapture.clearLogs();
        
        // Verifica se os métodos de filtragem funcionam
        assertTrue(logCapture.getLogMessagesByLevel(ch.qos.logback.classic.Level.ERROR).isEmpty(),
                  "Mensagens de erro devem estar vazias");
        assertTrue(logCapture.getLogMessagesByLevel(ch.qos.logback.classic.Level.INFO).isEmpty(),
                  "Mensagens de info devem estar vazias");
        assertTrue(logCapture.getLogMessagesByLevel(ch.qos.logback.classic.Level.DEBUG).isEmpty(),
                  "Mensagens de debug devem estar vazias");
    }

    @Test
    void testLogCaptureReset() {
        // Testa a funcionalidade de reset
        logCapture.clearLogs();
        assertEquals(0, logCapture.getLogCount(), "Contagem deve ser zero após limpeza");
        
        // Verifica se as listas estão realmente vazias
        assertTrue(logCapture.getLogMessages().isEmpty(), "Lista de mensagens deve estar vazia após limpeza");
        assertTrue(logCapture.getLogEvents().isEmpty(), "Lista de eventos deve estar vazia após limpeza");
    }

    @Test
    void testLogCaptureSearch() {
        // Testa a funcionalidade de busca
        logCapture.clearLogs();
        
        // Verifica se a busca funciona corretamente (mesmo sem logs)
        assertFalse(logCapture.hasLogMessage("teste"), "Não deve encontrar mensagem que não existe");
        assertFalse(logCapture.hasLogMessage(""), "Não deve encontrar mensagem vazia");
    }
}
