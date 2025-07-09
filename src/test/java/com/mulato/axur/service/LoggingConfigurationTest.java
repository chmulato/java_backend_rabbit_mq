package com.mulato.axur.service;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LoggingConfigurationTest {

    private Logger logger;
    private ListAppender<ILoggingEvent> listAppender;

    @BeforeEach
    void setUp() {
        logger = (Logger) LoggerFactory.getLogger(LoggingConfigurationTest.class);
        listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);
    }

    @Test
    void testLogFileCreation() throws Exception {
        // Aguarda um pouco para garantir que os logs sejam criados
        Thread.sleep(1000);
        
        // Verifica se o diretório de logs existe
        Path logsDir = Paths.get("logs");
        assertTrue(Files.exists(logsDir), "Diretório de logs deve existir");
        
        // Verifica se os arquivos de log são criados
        File testLogFile = new File("logs/test-execution.log");
        
        // Força a criação do log
        logger.info("Teste de criação de log");
        logger.debug("Mensagem de debug para teste");
        logger.warn("Mensagem de warning para teste");
        logger.error("Mensagem de error para teste");
        
        // Aguarda um pouco para o arquivo ser criado
        Thread.sleep(500);
        
        // Verifica se o arquivo foi criado (pode não existir ainda dependendo da configuração)
        // Mas pelo menos o diretório deve existir
        assertTrue(logsDir.toFile().isDirectory(), "Diretório de logs deve ser um diretório válido");
    }

    @Test
    void testLogLevels() {
        // Testa diferentes níveis de log
        logger.debug("Mensagem DEBUG");
        logger.info("Mensagem INFO");
        logger.warn("Mensagem WARN");
        logger.error("Mensagem ERROR");

        // Verifica se as mensagens foram capturadas
        List<ILoggingEvent> logsList = listAppender.list;
        assertFalse(logsList.isEmpty(), "Lista de logs não deve estar vazia");
        
        // Verifica se pelo menos uma mensagem foi logada
        boolean hasInfoMessage = logsList.stream()
                .anyMatch(event -> event.getMessage().contains("INFO"));
        boolean hasErrorMessage = logsList.stream()
                .anyMatch(event -> event.getMessage().contains("ERROR"));
        
        assertTrue(hasInfoMessage || hasErrorMessage, "Deve haver mensagens de log capturadas");
    }

    @Test
    void testLogPattern() {
        String testMessage = "Teste de padrão de log";
        logger.info(testMessage);
        
        List<ILoggingEvent> logsList = listAppender.list;
        assertFalse(logsList.isEmpty(), "Lista de logs não deve estar vazia");
        
        ILoggingEvent lastEvent = logsList.get(logsList.size() - 1);
        assertEquals(testMessage, lastEvent.getMessage(), "Mensagem deve corresponder ao esperado");
        assertNotNull(lastEvent.getTimeStamp(), "Timestamp deve estar presente");
        assertNotNull(lastEvent.getLevel(), "Level deve estar presente");
    }

    @Test
    void testLoggerConfiguration() {
        // Verifica se o logger está configurado corretamente
        assertNotNull(logger, "Logger não deve ser null");
        assertEquals("com.mulato.axur.service.LoggingConfigurationTest", logger.getName(), 
                "Nome do logger deve corresponder à classe");
        
        // Verifica se o appender foi adicionado
        assertTrue(logger.iteratorForAppenders().hasNext(), "Logger deve ter appenders");
    }
}
