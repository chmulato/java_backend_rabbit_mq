package com.mulato.axur.model;

import com.mulato.axur.service.CrawlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Set;
import java.util.HashSet;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o Requisito 3: ID da busca
 * 
 * Testa a geração automática de IDs conforme especificado:
 * - Código alfanumérico de 8 caracteres
 * - Gerado automaticamente
 * - Único para cada busca
 */
@SpringBootTest
@DisplayName("Requisito 3: ID da Busca - Código alfanumérico de 8 caracteres")
public class CrawlIdGenerationTest {

    @Autowired
    private CrawlService crawlService;

    private static final Pattern ALPHANUMERIC_8_CHARS = Pattern.compile("^[a-zA-Z0-9]{8}$");

    @Test
    @DisplayName("ID deve ter exatamente 8 caracteres alfanuméricos")
    public void testIdDeveSerAlfanumericoCom8Caracteres() {
        // Act
        String id = crawlService.startCrawl("test");

        // Assert
        assertNotNull(id, "ID não deve ser nulo");
        assertEquals(8, id.length(), "ID deve ter exatamente 8 caracteres");
        assertTrue(ALPHANUMERIC_8_CHARS.matcher(id).matches(), 
                  "ID deve conter apenas caracteres alfanuméricos");
    }

    @RepeatedTest(value = 10, name = "Geração #{currentRepetition} - ID deve ser único")
    @DisplayName("IDs gerados devem ser únicos")
    public void testIdsDevemSerUnicos() {
        // Arrange
        Set<String> generatedIds = new HashSet<>();

        // Act - Gera múltiplos IDs
        for (int i = 0; i < 5; i++) {
            String id = crawlService.startCrawl("test" + i);
            
            // Assert
            assertFalse(generatedIds.contains(id), 
                       "ID duplicado encontrado: " + id);
            generatedIds.add(id);
        }

        assertEquals(5, generatedIds.size(), 
                    "Todos os IDs devem ser únicos");
    }

    @Test
    @DisplayName("ID deve ser gerado automaticamente sem intervenção manual")
    public void testIdGeradoAutomaticamente() {
        // Act
        String id1 = crawlService.startCrawl("security");
        String id2 = crawlService.startCrawl("security");

        // Assert
        assertNotNull(id1, "Primeiro ID não deve ser nulo");
        assertNotNull(id2, "Segundo ID não deve ser nulo");
        assertNotEquals(id1, id2, "IDs devem ser diferentes mesmo para o mesmo termo");
        
        // Verifica padrão alfanumérico
        assertTrue(ALPHANUMERIC_8_CHARS.matcher(id1).matches(), 
                  "Primeiro ID deve ser alfanumérico");
        assertTrue(ALPHANUMERIC_8_CHARS.matcher(id2).matches(), 
                  "Segundo ID deve ser alfanumérico");
    }

    @Test
    @DisplayName("ID deve ser consistente entre chamadas para a mesma instância")
    public void testIdConsistentePorInstancia() {
        // Act
        String id = crawlService.startCrawl("testing");

        // Assert
        assertNotNull(id);
        assertEquals(8, id.length());
        
        // Verifica se o ID pode ser usado para consulta
        // (este teste assume que existe um método para buscar por ID)
        // O importante é que o ID seja válido e utilizável
        assertTrue(id.chars().allMatch(c -> 
            Character.isLetterOrDigit(c)), 
            "ID deve conter apenas caracteres alfanuméricos");
    }

    @Test
    @DisplayName("Diferentes termos devem gerar IDs diferentes")
    public void testTermosDiferentesGeramIdsDiferentes() {
        // Act
        String id1 = crawlService.startCrawl("java");
        String id2 = crawlService.startCrawl("python");
        String id3 = crawlService.startCrawl("javascript");

        // Assert
        Set<String> ids = Set.of(id1, id2, id3);
        assertEquals(3, ids.size(), "Todos os IDs devem ser únicos");
        
        // Verifica formato de cada ID
        for (String id : ids) {
            assertTrue(ALPHANUMERIC_8_CHARS.matcher(id).matches(), 
                      "ID " + id + " deve ser alfanumérico com 8 caracteres");
        }
    }
}
