package com.mulato.api.service;

import com.mulato.api.config.RabbitConfig;
import com.mulato.api.model.CrawlTask;
import com.mulato.api.util.LogCapture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrawlMessageServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CrawlMessageService crawlMessageService;

    private CrawlTask testTask;

    @BeforeEach
    void setUp() {
        testTask = new CrawlTask("test-123", "security", "http://example.com");
    }

    @Test
    void testSendCrawlMessage_Success() {
        // Given
        doNothing().when(rabbitTemplate).convertAndSend(
            eq(RabbitConfig.CRAWL_EXCHANGE),
            eq(RabbitConfig.CRAWL_ROUTING_KEY),
            eq(testTask)
        );

        // When & Then
        assertDoesNotThrow(() -> crawlMessageService.sendCrawlMessage(testTask));

        // Verify
        verify(rabbitTemplate, times(1)).convertAndSend(
            eq(RabbitConfig.CRAWL_EXCHANGE),
            eq(RabbitConfig.CRAWL_ROUTING_KEY),
            eq(testTask)
        );
    }

    @Test
    void testSendCrawlMessage_RabbitTemplateThrowsException() {
        // Given
        String errorMessage = "Connection refused";
        doThrow(new RuntimeException(errorMessage))
            .when(rabbitTemplate).convertAndSend(
                eq(RabbitConfig.CRAWL_EXCHANGE),
                eq(RabbitConfig.CRAWL_ROUTING_KEY),
                eq(testTask)
            );

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, 
            () -> crawlMessageService.sendCrawlMessage(testTask));

        assertEquals("Failed to send crawl task to queue", exception.getMessage());
        assertEquals(errorMessage, exception.getCause().getMessage());

        // Verify
        verify(rabbitTemplate, times(1)).convertAndSend(
            eq(RabbitConfig.CRAWL_EXCHANGE),
            eq(RabbitConfig.CRAWL_ROUTING_KEY),
            eq(testTask)
        );
    }

    @Test
    void testSendCrawlMessage_LogsCorrectly() {
        // Given
        LogCapture logCapture = new LogCapture(CrawlMessageService.class);
        
        doNothing().when(rabbitTemplate).convertAndSend(
            any(String.class), any(String.class), any(CrawlTask.class)
        );

        // When
        crawlMessageService.sendCrawlMessage(testTask);

        // Then
        assertTrue(logCapture.hasLogMessage("Sending crawl task to queue: " + testTask.getId()));
        assertTrue(logCapture.hasLogMessage("Crawl task sent successfully: " + testTask.getId()));
        
        logCapture.stop();
    }

    @Test
    void testSendCrawlMessage_LogsErrorOnException() {
        // Given
        LogCapture logCapture = new LogCapture(CrawlMessageService.class);
        String errorMessage = "Connection refused";
        
        doThrow(new RuntimeException(errorMessage))
            .when(rabbitTemplate).convertAndSend(
                any(String.class), any(String.class), any(CrawlTask.class)
            );

        // When
        assertThrows(RuntimeException.class, () -> crawlMessageService.sendCrawlMessage(testTask));

        // Then
        assertTrue(logCapture.hasLogMessage("Sending crawl task to queue: " + testTask.getId()));
        assertTrue(logCapture.hasLogMessage("Error sending crawl task to queue: " + testTask.getId()));
        
        logCapture.stop();
    }

    @Test
    void testSendCrawlMessage_WithNullTask() {
        // When & Then
        assertThrows(RuntimeException.class, 
            () -> crawlMessageService.sendCrawlMessage(null));
    }
}
