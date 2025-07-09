package com.mulato.axur.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilitário para capturar e verificar logs durante os testes
 */
public class LogCapture {
    
    private final Logger logger;
    private final ListAppender<ILoggingEvent> listAppender;
    
    public LogCapture(Class<?> clazz) {
        this.logger = (Logger) LoggerFactory.getLogger(clazz);
        this.listAppender = new ListAppender<>();
        this.listAppender.start();
        this.logger.addAppender(listAppender);
    }
    
    public LogCapture(String loggerName) {
        this.logger = (Logger) LoggerFactory.getLogger(loggerName);
        this.listAppender = new ListAppender<>();
        this.listAppender.start();
        this.logger.addAppender(listAppender);
    }
    
    /**
     * Retorna todos os eventos de log capturados
     */
    public List<ILoggingEvent> getLogEvents() {
        return listAppender.list;
    }
    
    /**
     * Retorna todas as mensagens de log capturadas
     */
    public List<String> getLogMessages() {
        return listAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toList());
    }
    
    /**
     * Verifica se uma mensagem específica foi logada
     */
    public boolean hasLogMessage(String message) {
        return listAppender.list.stream()
                .anyMatch(event -> event.getFormattedMessage().contains(message));
    }
    
    /**
     * Retorna o número de mensagens de log capturadas
     */
    public int getLogCount() {
        return listAppender.list.size();
    }
    
    /**
     * Limpa todas as mensagens de log capturadas
     */
    public void clearLogs() {
        listAppender.list.clear();
    }
    
    /**
     * Para a captura de logs e remove o appender
     */
    public void stop() {
        logger.detachAppender(listAppender);
        listAppender.stop();
    }
    
    /**
     * Retorna mensagens de log de um nível específico
     */
    public List<String> getLogMessagesByLevel(ch.qos.logback.classic.Level level) {
        return listAppender.list.stream()
                .filter(event -> event.getLevel().equals(level))
                .map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toList());
    }
    
    /**
     * Verifica se há mensagens de erro
     */
    public boolean hasErrorMessages() {
        return listAppender.list.stream()
                .anyMatch(event -> event.getLevel().equals(ch.qos.logback.classic.Level.ERROR));
    }
    
    /**
     * Verifica se há mensagens de warning
     */
    public boolean hasWarningMessages() {
        return listAppender.list.stream()
                .anyMatch(event -> event.getLevel().equals(ch.qos.logback.classic.Level.WARN));
    }
    
    /**
     * Retorna as últimas N mensagens de log
     */
    public List<String> getLastLogMessages(int count) {
        List<String> messages = getLogMessages();
        int size = messages.size();
        if (size <= count) {
            return messages;
        }
        return messages.subList(size - count, size);
    }
}
