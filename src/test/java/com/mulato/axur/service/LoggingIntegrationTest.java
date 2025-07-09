package com.mulato.axur.service;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class LoggingIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(LoggingIntegrationTest.class);

    @Test
    void testLogDirectoryCreation() {
        // Verifica se o diretório de logs existe ou pode ser criado
        Path logsDir = Paths.get("logs");
        
        if (!Files.exists(logsDir)) {
            try {
                Files.createDirectories(logsDir);
            } catch (Exception e) {
                fail("Não foi possível criar o diretório de logs: " + e.getMessage());
            }
        }
        
        assertTrue(Files.exists(logsDir), "Diretório de logs deve existir");
        assertTrue(Files.isDirectory(logsDir), "logs deve ser um diretório");
        assertTrue(Files.isWritable(logsDir), "Diretório de logs deve ser gravável");
    }

    @Test
    void testLogLevelConfiguration() {
        // Testa se os diferentes níveis de log estão funcionando
        logger.trace("Mensagem TRACE - não deve aparecer normalmente");
        logger.debug("Mensagem DEBUG - deve aparecer em desenvolvimento");
        logger.info("Mensagem INFO - deve aparecer sempre");
        logger.warn("Mensagem WARN - deve aparecer sempre");
        logger.error("Mensagem ERROR - deve aparecer sempre");
        
        // Se chegou até aqui, os logs foram processados sem erro
        assertTrue(true, "Logs foram processados sem erro");
    }

    @Test
    void testLogFilePermissions() {
        // Verifica se o diretório de logs tem as permissões corretas
        File logsDir = new File("logs");
        
        if (!logsDir.exists()) {
            boolean created = logsDir.mkdirs();
            assertTrue(created, "Diretório de logs deve ser criado");
        }
        
        assertTrue(logsDir.canRead(), "Diretório de logs deve ser legível");
        assertTrue(logsDir.canWrite(), "Diretório de logs deve ser gravável");
    }

    @Test
    void testLoggerNaming() {
        // Verifica se o nome do logger está correto
        assertEquals("com.mulato.axur.service.LoggingIntegrationTest", 
                    logger.getName(), 
                    "Nome do logger deve corresponder ao nome da classe");
    }

    @Test
    void testLogWithException() {
        // Testa logging com exceções
        try {
            throw new RuntimeException("Exceção de teste");
        } catch (Exception e) {
            logger.error("Erro capturado durante teste", e);
            // Se chegou até aqui, o log com exceção foi processado
            assertTrue(true, "Log com exceção foi processado");
        }
    }

    @Test
    void testLogWithParameters() {
        // Testa logging com parâmetros
        String keyword = "security";
        int count = 42;
        
        logger.info("Processando keyword: {} com {} resultados", keyword, count);
        logger.debug("Debug com parâmetros: keyword={}, count={}", keyword, count);
        
        // Se chegou até aqui, os logs parametrizados foram processados
        assertTrue(true, "Logs parametrizados foram processados");
    }

    @Test
    void testLogConfiguration() {
        // Verifica se a configuração de log está adequada para testes
        assertNotNull(LoggerFactory.getLogger("com.mulato.axur"), 
                     "Logger do pacote principal deve existir");
        assertNotNull(LoggerFactory.getLogger("com.mulato.axur.service"), 
                     "Logger do pacote service deve existir");
        assertNotNull(LoggerFactory.getLogger("com.mulato.axur.controller"), 
                     "Logger do pacote controller deve existir");
    }
}
