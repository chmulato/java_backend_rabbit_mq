package com.mulato.axur.listener;

import com.mulato.axur.model.CrawlTask;
import com.mulato.axur.service.CrawlService;
import com.mulato.axur.service.WebCrawlerService;
import com.mulato.axur.util.LogCapture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrawlTaskListenerTest {

    @Mock
    private WebCrawlerService webCrawlerService;

    @Mock
    private CrawlService crawlService;

    @InjectMocks
    private CrawlTaskListener crawlTaskListener;

    private CrawlTask testTask;

    @BeforeEach
    void setUp() {
        testTask = new CrawlTask("test-123", "security", "http://example.com");
    }

    @Test
    void testProcessCrawlTask_Success() {
        // Given
        doNothing().when(webCrawlerService).crawlWebsite(testTask);
        doNothing().when(crawlService).finishCrawlTask(testTask.getId());

        // When
        crawlTaskListener.processCrawlTask(testTask);

        // Then
        verify(webCrawlerService, times(1)).crawlWebsite(testTask);
        verify(crawlService, times(1)).finishCrawlTask(testTask.getId());
    }

    @Test
    void testProcessCrawlTask_WebCrawlerServiceThrowsException() {
        // Given
        String errorMessage = "Network error";
        doThrow(new RuntimeException(errorMessage))
                .when(webCrawlerService).crawlWebsite(testTask);
        doNothing().when(crawlService).finishCrawlTask(testTask.getId());

        // When
        crawlTaskListener.processCrawlTask(testTask);

        // Then
        verify(webCrawlerService, times(1)).crawlWebsite(testTask);
        verify(crawlService, times(1)).finishCrawlTask(testTask.getId());
        assertFalse(testTask.isActive());
    }

    @Test
    void testProcessCrawlTask_CrawlServiceThrowsException() {
        // Given
        doNothing().when(webCrawlerService).crawlWebsite(testTask);
        doThrow(new RuntimeException("Database error"))
                .when(crawlService).finishCrawlTask(testTask.getId());

        // When
        crawlTaskListener.processCrawlTask(testTask);

        // Then
        verify(webCrawlerService, times(1)).crawlWebsite(testTask);
        verify(crawlService, times(1)).finishCrawlTask(testTask.getId());
        assertFalse(testTask.isActive());
    }

    @Test
    void testProcessCrawlTask_LogsCorrectly() {
        // Given
        LogCapture logCapture = new LogCapture(CrawlTaskListener.class);
        doNothing().when(webCrawlerService).crawlWebsite(testTask);
        doNothing().when(crawlService).finishCrawlTask(testTask.getId());

        // When
        crawlTaskListener.processCrawlTask(testTask);

        // Then
        assertTrue(logCapture.hasLogMessage("Received crawl task from queue: " + testTask.getId()));
        assertTrue(logCapture.hasLogMessage("Crawl task completed successfully: " + testTask.getId()));
        
        logCapture.stop();
    }

    @Test
    void testProcessCrawlTask_LogsErrorOnException() {
        // Given
        LogCapture logCapture = new LogCapture(CrawlTaskListener.class);
        String errorMessage = "Test error";
        doThrow(new RuntimeException(errorMessage))
                .when(webCrawlerService).crawlWebsite(testTask);
        doNothing().when(crawlService).finishCrawlTask(testTask.getId());

        // When
        crawlTaskListener.processCrawlTask(testTask);

        // Then
        assertTrue(logCapture.hasLogMessage("Received crawl task from queue: " + testTask.getId()));
        assertTrue(logCapture.hasLogMessage("Error processing crawl task: " + testTask.getId()));
        
        logCapture.stop();
    }

    @Test
    void testProcessCrawlTask_TaskSetToInactiveOnException() {
        // Given
        testTask.setActive(true);
        doThrow(new RuntimeException("Test error"))
                .when(webCrawlerService).crawlWebsite(testTask);
        doNothing().when(crawlService).finishCrawlTask(testTask.getId());

        // When
        crawlTaskListener.processCrawlTask(testTask);

        // Then
        assertFalse(testTask.isActive());
        verify(crawlService, times(1)).finishCrawlTask(testTask.getId());
    }

    @Test
    void testProcessCrawlTask_WithNullTask() {
        // When & Then
        assertThrows(NullPointerException.class, 
            () -> crawlTaskListener.processCrawlTask(null));
    }

    @Test
    void testProcessCrawlTask_CallsServicesInCorrectOrder() {
        // Given
        doNothing().when(webCrawlerService).crawlWebsite(testTask);
        doNothing().when(crawlService).finishCrawlTask(testTask.getId());

        // When
        crawlTaskListener.processCrawlTask(testTask);

        // Then
        // Verify the order of calls
        var inOrder = inOrder(webCrawlerService, crawlService);
        inOrder.verify(webCrawlerService).crawlWebsite(testTask);
        inOrder.verify(crawlService).finishCrawlTask(testTask.getId());
    }
}
