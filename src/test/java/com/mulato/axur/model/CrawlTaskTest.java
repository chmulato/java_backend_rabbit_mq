package com.mulato.axur.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CrawlTaskTest {

    private CrawlTask crawlTask;
    private String testId;
    private String testKeyword;
    private String testBaseUrl;

    @BeforeEach
    void setUp() {
        testId = "test123";
        testKeyword = "security";
        testBaseUrl = "http://example.com";
        crawlTask = new CrawlTask(testId, testKeyword, testBaseUrl);
    }
    
    @Test
    void testCrawlTaskCreation() {
        CrawlTask task = new CrawlTask("abc12345", "security", "http://example.com");
        
        assertEquals("abc12345", task.getId());
        assertEquals("security", task.getKeyword());
        assertEquals("http://example.com", task.getBaseUrl());
        assertTrue(task.isActive());
        assertEquals("active", task.getStatus());
        assertNotNull(task.getStartTime());
        assertTrue(task.getFoundUrls().isEmpty());
        assertTrue(task.getVisitedUrls().isEmpty());
    }
    
    @Test
    void testAddFoundUrl() {
        CrawlTask task = new CrawlTask("abc12345", "security", "http://example.com");
        
        task.addFoundUrl("http://example.com/page1");
        task.addFoundUrl("http://example.com/page2");
        
        assertEquals(2, task.getFoundUrls().size());
        assertTrue(task.getFoundUrls().contains("http://example.com/page1"));
        assertTrue(task.getFoundUrls().contains("http://example.com/page2"));
    }
    
    @Test
    void testAddDuplicateFoundUrl() {
        CrawlTask task = new CrawlTask("abc12345", "security", "http://example.com");
        
        task.addFoundUrl("http://example.com/page1");
        task.addFoundUrl("http://example.com/page1");
        
        assertEquals(1, task.getFoundUrls().size());
    }
    
    @Test
    void testMarkUrlAsVisited() {
        CrawlTask task = new CrawlTask("abc12345", "security", "http://example.com");
        
        task.markUrlAsVisited("http://example.com/page1");
        
        assertTrue(task.isUrlVisited("http://example.com/page1"));
        assertFalse(task.isUrlVisited("http://example.com/page2"));
    }
    
    @Test
    void testSetActive() {
        CrawlTask task = new CrawlTask("abc12345", "security", "http://example.com");
        
        assertTrue(task.isActive());
        assertEquals("active", task.getStatus());
        
        task.setActive(false);
        
        assertFalse(task.isActive());
        assertEquals("done", task.getStatus());
    }
}
