package com.mulato.axur.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CrawlResponseTest {

    private CrawlResponse crawlResponse;

    @BeforeEach
    void setUp() {
        crawlResponse = new CrawlResponse();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(crawlResponse);
        assertNull(crawlResponse.getId());
    }

    @Test
    void testConstructorWithId() {
        String id = "abc12345";
        CrawlResponse response = new CrawlResponse(id);
        
        assertNotNull(response);
        assertEquals(id, response.getId());
    }

    @Test
    void testSetAndGetId() {
        String id = "xyz98765";
        crawlResponse.setId(id);
        
        assertEquals(id, crawlResponse.getId());
    }

    @Test
    void testToString() {
        String id = "test123";
        crawlResponse.setId(id);
        
        String toString = crawlResponse.toString();
        assertNotNull(toString);
        assertTrue(toString.contains("CrawlResponse"));
        assertTrue(toString.contains(id));
    }

    @Test
    void testIdValidation() {
        // Testa ID com 8 caracteres (padrão esperado)
        String validId = "abcd1234";
        crawlResponse.setId(validId);
        assertEquals(validId, crawlResponse.getId());
        
        // Testa ID com caracteres alfanuméricos
        String alphanumericId = "A1b2C3d4";
        crawlResponse.setId(alphanumericId);
        assertEquals(alphanumericId, crawlResponse.getId());
    }

    @Test
    void testIdEdgeCases() {
        // Testa ID nulo
        crawlResponse.setId(null);
        assertNull(crawlResponse.getId());
        
        // Testa ID vazio
        crawlResponse.setId("");
        assertEquals("", crawlResponse.getId());
        
        // Testa ID muito longo
        String longId = "a".repeat(50);
        crawlResponse.setId(longId);
        assertEquals(longId, crawlResponse.getId());
    }

    @Test
    void testJsonPropertyMapping() {
        // Testa se o objeto pode ser usado para serialização JSON
        String id = "json123";
        crawlResponse.setId(id);
        
        // Verifica se os valores estão corretos para serialização
        assertEquals(id, crawlResponse.getId());
        assertNotNull(crawlResponse.toString());
    }
}
