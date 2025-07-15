package com.mulato.api.util;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Utilitário para capturar logs durante os testes
 */
public class LogCapture {
    
    private final ListAppender<ILoggingEvent> listAppender;
    private final Logger logger;
    
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
    
    public List<String> getMessages() {
        return listAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .collect(Collectors.toList());
    }
    
    public List<String> getMessagesContaining(String text) {
        return getMessages().stream()
                .filter(message -> message.contains(text))
                .collect(Collectors.toList());
    }
    
    public boolean hasMessageContaining(String text) {
        return getMessages().stream()
                .anyMatch(message -> message.contains(text));
    }
    
    public boolean hasLogMessage(String text) {
        return hasMessageContaining(text);
    }
    
    public void clear() {
        listAppender.list.clear();
    }
    
    public void stop() {
        logger.detachAppender(listAppender);
        listAppender.stop();
    }
}
