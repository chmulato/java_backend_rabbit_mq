package com.mulato.axur.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrawlRequestTest {

    private CrawlRequest crawlRequest;

    @BeforeEach
    void setUp() {
        crawlRequest = new CrawlRequest();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(crawlRequest);
        assertNull(crawlRequest.getKeyword());
    }

    @Test
    void testConstructorWithKeyword() {
        String keyword = "security";
        CrawlRequest request = new CrawlRequest(keyword);
        
        assertNotNull(request);
        assertEquals(keyword, request.getKeyword());
    }

    @Test
    void testSetAndGetKeyword() {
        String keyword = "testing";
        crawlRequest.setKeyword(keyword);
        
        assertEquals(keyword, crawlRequest.getKeyword());
    }

    @Test
    void testToString() {
        String keyword = "security";
        crawlRequest.setKeyword(keyword);
        
        String toString = crawlRequest.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CrawlRequest"));
        assertTrue(toString.contains(keyword));
    }

    @Test
    void testKeywordValidation() {
        // Testa keyword válida (4 caracteres)
        crawlRequest.setKeyword("test");
        assertEquals("test", crawlRequest.getKeyword());
        
        // Testa keyword válida (32 caracteres)
        String longKeyword = "a".repeat(32);
        crawlRequest.setKeyword(longKeyword);
        assertEquals(longKeyword, crawlRequest.getKeyword());
    }

    @Test
    void testKeywordEdgeCases() {
        // Testa keyword nula
        crawlRequest.setKeyword(null);
        assertNull(crawlRequest.getKeyword());
        
        // Testa keyword vazia
        crawlRequest.setKeyword("");
        assertEquals("", crawlRequest.getKeyword());
        
        // Testa keyword com espaços
        crawlRequest.setKeyword("test keyword");
        assertEquals("test keyword", crawlRequest.getKeyword());
    }

    @Test
    void testEqualsAndHashCode() {
        CrawlRequest request1 = new CrawlRequest("security");
        CrawlRequest request2 = new CrawlRequest("security");
        CrawlRequest request3 = new CrawlRequest("different");
        
        // Como não implementamos equals/hashCode, testamos a referência
        assertNotEquals(request1, request2); // Diferentes objetos
        assertEquals(request1, request1); // Mesmo objeto
        assertNotEquals(request1, request3);
    }
}
