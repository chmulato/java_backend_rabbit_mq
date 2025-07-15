package com.mulato.api.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "app.search.id-length=8"
})
class IdGeneratorServiceTest {
    
    @Autowired
    private IdGeneratorService idGeneratorService;
    
    @Test
    void testGenerateId() {
        String id = idGeneratorService.generateId();
        
        assertNotNull(id);
        assertEquals(8, id.length());
        assertTrue(id.matches("[A-Za-z0-9]+"));
    }
    
    @Test
    void testGenerateIdUniqueness() {
        String id1 = idGeneratorService.generateId();
        String id2 = idGeneratorService.generateId();
        
        assertNotEquals(id1, id2);
    }
    
    @Test
    void testGenerateIdMultipleTimes() {
        for (int i = 0; i < 100; i++) {
            String id = idGeneratorService.generateId();
            assertEquals(8, id.length());
            assertTrue(id.matches("[A-Za-z0-9]+"));
        }
    }
}
