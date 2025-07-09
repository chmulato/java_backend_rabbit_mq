package com.mulato.axur.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CrawlResultTest {

    private CrawlResult crawlResult;

    @BeforeEach
    void setUp() {
        crawlResult = new CrawlResult();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(crawlResult);
        assertNull(crawlResult.getId());
        assertNull(crawlResult.getStatus());
        assertNull(crawlResult.getUrls());
    }

    @Test
    void testConstructorWithParameters() {
        String id = "abc12345";
        String status = "active";
        List<String> urls = Arrays.asList(
            "http://example.com/page1",
            "http://example.com/page2"
        );
        
        CrawlResult result = new CrawlResult(id, status, urls);
        
        assertNotNull(result);
        assertEquals(id, result.getId());
        assertEquals(status, result.getStatus());
        assertEquals(urls, result.getUrls());
        assertEquals(2, result.getUrls().size());
    }

    @Test
    void testSetAndGetId() {
        String id = "test123";
        crawlResult.setId(id);
        assertEquals(id, crawlResult.getId());
    }

    @Test
    void testSetAndGetStatus() {
        String status = "done";
        crawlResult.setStatus(status);
        assertEquals(status, crawlResult.getStatus());
    }

    @Test
    void testSetAndGetUrls() {
        List<String> urls = Arrays.asList(
            "http://test.com/1",
            "http://test.com/2",
            "http://test.com/3"
        );
        
        crawlResult.setUrls(urls);
        assertEquals(urls, crawlResult.getUrls());
        assertEquals(3, crawlResult.getUrls().size());
    }

    @Test
    void testToString() {
        String id = "test123";
        String status = "active";
        List<String> urls = Arrays.asList("http://example.com");
        
        crawlResult.setId(id);
        crawlResult.setStatus(status);
        crawlResult.setUrls(urls);
        
        String toString = crawlResult.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CrawlResult"));
        assertTrue(toString.contains(id));
        assertTrue(toString.contains(status));
    }

    @Test
    void testStatusValues() {
        // Testa status "active"
        crawlResult.setStatus("active");
        assertEquals("active", crawlResult.getStatus());
        
        // Testa status "done"
        crawlResult.setStatus("done");
        assertEquals("done", crawlResult.getStatus());
    }

    @Test
    void testEmptyUrls() {
        List<String> emptyUrls = Arrays.asList();
        crawlResult.setUrls(emptyUrls);
        
        assertNotNull(crawlResult.getUrls());
        assertTrue(crawlResult.getUrls().isEmpty());
        assertEquals(0, crawlResult.getUrls().size());
    }

    @Test
    void testNullValues() {
        crawlResult.setId(null);
        crawlResult.setStatus(null);
        crawlResult.setUrls(null);
        
        assertNull(crawlResult.getId());
        assertNull(crawlResult.getStatus());
        assertNull(crawlResult.getUrls());
    }

    @Test
    void testUrlsWithDifferentFormats() {
        List<String> urls = Arrays.asList(
            "http://example.com/page1",
            "https://secure.example.com/page2",
            "http://example.com:8080/page3",
            "http://example.com/path/to/page4?param=value"
        );
        
        crawlResult.setUrls(urls);
        assertEquals(4, crawlResult.getUrls().size());
        assertTrue(crawlResult.getUrls().contains("http://example.com/page1"));
        assertTrue(crawlResult.getUrls().contains("https://secure.example.com/page2"));
    }

    @Test
    void testCompleteResult() {
        String id = "complete123";
        String status = "done";
        List<String> urls = Arrays.asList(
            "http://hiring.axreng.com/index.html",
            "http://hiring.axreng.com/about.html"
        );
        
        CrawlResult result = new CrawlResult(id, status, urls);
        
        // Verifica se todos os campos estão preenchidos corretamente
        assertEquals(id, result.getId());
        assertEquals(status, result.getStatus());
        assertEquals(2, result.getUrls().size());
        assertEquals(urls, result.getUrls());
        
        // Verifica se o toString contém informações relevantes
        String toString = result.toString();
        assertTrue(toString.contains(id));
        assertTrue(toString.contains(status));
    }
}
