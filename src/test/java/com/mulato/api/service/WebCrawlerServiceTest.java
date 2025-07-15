package com.mulato.api.service;

import com.mulato.api.model.CrawlTask;
import com.mulato.api.util.LogCapture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.jsoup.Jsoup;
import org.jsoup.Connection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebCrawlerServiceTest {

    @Mock
    private CrawlPersistenceService crawlPersistenceService;

    @Mock
    private Connection connection;

    @Mock
    private Document document;

    @Mock
    private Elements elements;

    @Mock
    private Element element;

    @InjectMocks
    private WebCrawlerService webCrawlerService;

    private CrawlTask testTask;
    private String testUrl;

    @BeforeEach
    void setUp() {
        testTask = new CrawlTask("test-123", "security", "http://example.com");
        testUrl = "http://example.com";
        
        // Configure properties via reflection
        ReflectionTestUtils.setField(webCrawlerService, "timeout", 30000);
        ReflectionTestUtils.setField(webCrawlerService, "userAgent", "Test Crawler");
        ReflectionTestUtils.setField(webCrawlerService, "delay", 100);
        ReflectionTestUtils.setField(webCrawlerService, "maxPages", 10);
    }

    @Test
    void testCrawlWebsite_Success() throws Exception {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Given
            String htmlContent = "<html><body><h1>Security testing</h1><a href='/page1'>Link</a></body></html>";
            
            when(crawlPersistenceService.isUrlVisited(anyString(), anyString())).thenReturn(false);
            
            jsoupMock.when(() -> Jsoup.connect(testUrl)).thenReturn(connection);
            when(connection.timeout(30000)).thenReturn(connection);
            when(connection.userAgent("Test Crawler")).thenReturn(connection);
            when(connection.get()).thenReturn(document);
            when(document.html()).thenReturn(htmlContent);
            when(document.select("a[href]")).thenReturn(elements);
            when(elements.iterator()).thenReturn(java.util.Collections.emptyIterator());

            // When
            webCrawlerService.crawlWebsite(testTask);

            // Then
            assertFalse(testTask.isActive());
            verify(crawlPersistenceService, times(1)).saveVisitedUrl(testTask.getId(), testUrl);
            verify(crawlPersistenceService, times(1)).saveFoundUrl(testTask.getId(), testUrl);
        }
    }

    @Test
    void testCrawlWebsite_NoKeywordFound() throws Exception {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Given
            String htmlContent = "<html><body><h1>Test page without keyword</h1></body></html>";
            
            when(crawlPersistenceService.isUrlVisited(anyString(), anyString())).thenReturn(false);
            
            jsoupMock.when(() -> Jsoup.connect(testUrl)).thenReturn(connection);
            when(connection.timeout(30000)).thenReturn(connection);
            when(connection.userAgent("Test Crawler")).thenReturn(connection);
            when(connection.get()).thenReturn(document);
            when(document.html()).thenReturn(htmlContent);
            when(document.select("a[href]")).thenReturn(elements);
            when(elements.iterator()).thenReturn(java.util.Collections.emptyIterator());

            // When
            webCrawlerService.crawlWebsite(testTask);

            // Then
            assertFalse(testTask.isActive());
            verify(crawlPersistenceService, times(1)).saveVisitedUrl(testTask.getId(), testUrl);
            verify(crawlPersistenceService, never()).saveFoundUrl(testTask.getId(), testUrl);
            assertTrue(testTask.getFoundUrls().isEmpty());
        }
    }

    @Test
    void testCrawlWebsite_UrlAlreadyVisited() {
        // Given
        when(crawlPersistenceService.isUrlVisited(testTask.getId(), testUrl)).thenReturn(true);

        // When
        webCrawlerService.crawlWebsite(testTask);

        // Then
        assertFalse(testTask.isActive());
        verify(crawlPersistenceService, never()).saveVisitedUrl(testTask.getId(), testUrl);
        verify(crawlPersistenceService, never()).saveFoundUrl(testTask.getId(), testUrl);
    }

    @Test
    void testCrawlWebsite_FetchDocumentIOException() throws Exception {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Given
            LogCapture logCapture = new LogCapture(WebCrawlerService.class);
            
            when(crawlPersistenceService.isUrlVisited(anyString(), anyString())).thenReturn(false);
            
            jsoupMock.when(() -> Jsoup.connect(testUrl)).thenReturn(connection);
            when(connection.timeout(30000)).thenReturn(connection);
            when(connection.userAgent("Test Crawler")).thenReturn(connection);
            when(connection.get()).thenThrow(new IOException("Connection timeout"));

            // When
            webCrawlerService.crawlWebsite(testTask);

            // Then
            assertFalse(testTask.isActive());
            verify(crawlPersistenceService, times(1)).saveVisitedUrl(testTask.getId(), testUrl);
            assertTrue(logCapture.hasLogMessage("Failed to fetch URL: " + testUrl));
            
            logCapture.stop();
        }
    }

    @Test
    void testCrawlWebsite_WithLinks() throws Exception {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Given
            String htmlContent = "<html><body><h1>Security page</h1></body></html>";
            String linkHref = "/page1";
            
            when(crawlPersistenceService.isUrlVisited(anyString(), anyString())).thenReturn(false);
            
            jsoupMock.when(() -> Jsoup.connect(testUrl)).thenReturn(connection);
            when(connection.timeout(30000)).thenReturn(connection);
            when(connection.userAgent("Test Crawler")).thenReturn(connection);
            when(connection.get()).thenReturn(document);
            when(document.html()).thenReturn(htmlContent);
            when(document.select("a[href]")).thenReturn(elements);
            
            when(elements.iterator()).thenReturn(java.util.Arrays.asList(element).iterator());
            when(element.attr("href")).thenReturn(linkHref);

            // When
            webCrawlerService.crawlWebsite(testTask);

            // Then
            assertFalse(testTask.isActive());
            verify(crawlPersistenceService, times(1)).saveVisitedUrl(testTask.getId(), testUrl);
            verify(crawlPersistenceService, times(1)).saveFoundUrl(testTask.getId(), testUrl);
        }
    }

    @Test
    void testCrawlWebsite_TaskNotActive() {
        // Given
        testTask.setActive(false);

        // When
        webCrawlerService.crawlWebsite(testTask);

        // Then
        assertFalse(testTask.isActive());
        verify(crawlPersistenceService, never()).saveVisitedUrl(anyString(), anyString());
        verify(crawlPersistenceService, never()).saveFoundUrl(anyString(), anyString());
    }

    @Test
    void testCrawlWebsite_MaxPagesReached() throws Exception {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Given
            ReflectionTestUtils.setField(webCrawlerService, "maxPages", 1);
            String htmlContent = "<html><body><h1>Security page</h1><a href='/page1'>Link</a></body></html>";
            
            when(crawlPersistenceService.isUrlVisited(anyString(), anyString())).thenReturn(false);
            
            jsoupMock.when(() -> Jsoup.connect(testUrl)).thenReturn(connection);
            when(connection.timeout(30000)).thenReturn(connection);
            when(connection.userAgent("Test Crawler")).thenReturn(connection);
            when(connection.get()).thenReturn(document);
            when(document.html()).thenReturn(htmlContent);
            when(document.select("a[href]")).thenReturn(elements);
            
            when(elements.iterator()).thenReturn(java.util.Arrays.asList(element).iterator());
            when(element.attr("href")).thenReturn("/page1");

            // When
            webCrawlerService.crawlWebsite(testTask);

            // Then
            assertFalse(testTask.isActive());
            verify(crawlPersistenceService, times(1)).saveVisitedUrl(testTask.getId(), testUrl);
        }
    }

    @Test
    void testCrawlWebsite_LogsCorrectly() throws Exception {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Given
            LogCapture logCapture = new LogCapture(WebCrawlerService.class);
            String htmlContent = "<html><body><h1>Security testing</h1></body></html>";
            
            when(crawlPersistenceService.isUrlVisited(anyString(), anyString())).thenReturn(false);
            
            jsoupMock.when(() -> Jsoup.connect(testUrl)).thenReturn(connection);
            when(connection.timeout(30000)).thenReturn(connection);
            when(connection.userAgent("Test Crawler")).thenReturn(connection);
            when(connection.get()).thenReturn(document);
            when(document.html()).thenReturn(htmlContent);
            when(document.select("a[href]")).thenReturn(elements);
            when(elements.iterator()).thenReturn(java.util.Collections.emptyIterator());

            // When
            webCrawlerService.crawlWebsite(testTask);

            // Then
            assertTrue(logCapture.hasLogMessage("Starting crawl for task: " + testTask.getId()));
            assertTrue(logCapture.hasLogMessage("Found keyword 'security' in URL: " + testUrl));
            assertTrue(logCapture.hasLogMessage("Crawl completed for task: " + testTask.getId()));
            
            logCapture.stop();
        }
    }

    @Test
    void testCrawlWebsite_CaseInsensitiveKeyword() throws Exception {
        try (MockedStatic<Jsoup> jsoupMock = mockStatic(Jsoup.class)) {
            // Given
            String htmlContent = "<html><body><h1>SECURITY TESTING</h1></body></html>";
            
            when(crawlPersistenceService.isUrlVisited(anyString(), anyString())).thenReturn(false);
            
            jsoupMock.when(() -> Jsoup.connect(testUrl)).thenReturn(connection);
            when(connection.timeout(30000)).thenReturn(connection);
            when(connection.userAgent("Test Crawler")).thenReturn(connection);
            when(connection.get()).thenReturn(document);
            when(document.html()).thenReturn(htmlContent);
            when(document.select("a[href]")).thenReturn(elements);
            when(elements.iterator()).thenReturn(java.util.Collections.emptyIterator());

            // When
            webCrawlerService.crawlWebsite(testTask);

            // Then
            verify(crawlPersistenceService, times(1)).saveFoundUrl(testTask.getId(), testUrl);
            assertEquals(1, testTask.getFoundUrls().size());
        }
    }
}
