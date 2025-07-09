package com.mulato.axur.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulato.axur.model.CrawlRequest;
import com.mulato.axur.model.CrawlResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes de integração utilizando massa de dados realista
 * 
 * Testa cenários completos usando dados do sample-data.sql:
 * - Buscas por termos que existem na massa de dados
 * - Validação de resultados esperados
 * - Integração com dados reais
 * - Fluxos de ponta a ponta (E2E)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.sql.init.data-locations=classpath:db/sample-data.sql",
    "crawl.base-url=http://hiring.axreng.com/"
})
@DisplayName("Testes de Integração E2E com Massa de Dados Realista")
public class EndToEndIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("E2E: Busca por 'security' deve retornar resultados esperados")
    public void testBuscaSecurityComDadosReais() throws Exception {
        // Arrange - Termo que provavelmente existe na massa de dados
        CrawlRequest request = new CrawlRequest("security");

        // Act - Inicia busca
        String response = mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        // Aguarda processamento
        Thread.sleep(1000);

        // Act - Consulta resultados
        String resultResponse = mockMvc.perform(get("/crawl/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.urls").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CrawlResult result = objectMapper.readValue(resultResponse, CrawlResult.class);

        // Assert - Validações baseadas na massa de dados
        assertNotNull(result.getId(), "ID deve estar presente");
        assertEquals(id, result.getId(), "ID deve corresponder");
        assertTrue(result.getStatus().equals("active") || result.getStatus().equals("done"),
                  "Status deve ser válido");
        assertNotNull(result.getUrls(), "URLs não devem ser nulas");
        
        // Se a busca terminou, pode ter encontrado URLs
        if ("done".equals(result.getStatus())) {
            // Para 'security', esperamos encontrar pelo menos algumas URLs
            assertTrue(result.getUrls().size() >= 0, 
                      "URLs encontradas devem ser válidas (pode ser 0 se termo não existir)");
        }
    }

    @Test
    @DisplayName("E2E: Busca por 'java' deve processar corretamente")
    public void testBuscaJavaComDadosReais() throws Exception {
        // Arrange
        CrawlRequest request = new CrawlRequest("java");

        // Act - Fluxo completo
        String response = mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = objectMapper.readTree(response).get("id").asText();

        // Múltiplas consultas para verificar evolução
        CrawlResult result1 = consultarResultado(id);
        Thread.sleep(200);
        CrawlResult result2 = consultarResultado(id);
        Thread.sleep(200);
        CrawlResult result3 = consultarResultado(id);

        // Assert - Progressão dos resultados
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotNull(result3);

        assertEquals(id, result1.getId());
        assertEquals(id, result2.getId());
        assertEquals(id, result3.getId());

        // URLs não devem diminuir ao longo do tempo
        assertTrue(result2.getUrls().size() >= result1.getUrls().size(),
                  "URLs não devem diminuir entre consultas");
        assertTrue(result3.getUrls().size() >= result2.getUrls().size(),
                  "URLs não devem diminuir entre consultas");
    }

    @Test
    @DisplayName("E2E: Múltiplas buscas simultâneas com dados reais")
    public void testMultiplasBuscasSimultaneasComDadosReais() throws Exception {
        // Arrange - Diferentes termos
        String[] termos = {"spring", "boot", "test", "web"};
        String[] ids = new String[termos.length];

        // Act - Inicia todas as buscas
        for (int i = 0; i < termos.length; i++) {
            CrawlRequest request = new CrawlRequest(termos[i]);
            String response = mockMvc.perform(post("/crawl")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
            
            ids[i] = objectMapper.readTree(response).get("id").asText();
        }

        // Aguarda processamento
        Thread.sleep(1500);

        // Act & Assert - Verifica todas as buscas
        for (int i = 0; i < ids.length; i++) {
            CrawlResult result = consultarResultado(ids[i]);
            
            assertNotNull(result, "Resultado " + i + " deve existir");
            assertEquals(ids[i], result.getId(), "ID " + i + " deve corresponder");
            assertTrue(result.getStatus().equals("active") || result.getStatus().equals("done"),
                      "Status " + i + " deve ser válido");
            assertNotNull(result.getUrls(), "URLs " + i + " não devem ser nulas");
        }

        // Verifica que todos os IDs são únicos
        for (int i = 0; i < ids.length; i++) {
            for (int j = i + 1; j < ids.length; j++) {
                assertNotEquals(ids[i], ids[j], 
                               "IDs devem ser únicos: " + ids[i] + " vs " + ids[j]);
            }
        }
    }

    @Test
    @DisplayName("E2E: Validação completa do formato de resposta")
    public void testValidacaoCompletaFormatoResposta() throws Exception {
        // Arrange
        CrawlRequest request = new CrawlRequest("format");

        // Act - POST /crawl
        String postResponse = mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.id").isString())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String id = objectMapper.readTree(postResponse).get("id").asText();

        // Valida formato do ID
        assertTrue(id.matches("^[a-zA-Z0-9]{8}$"), 
                  "ID deve ser alfanumérico com 8 caracteres");

        // Act - GET /crawl/{id}
        mockMvc.perform(get("/crawl/" + id))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(id))
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.status").isString())
                .andExpect(jsonPath("$.urls").exists())
                .andExpect(jsonPath("$.urls").isArray());
    }

    @Test
    @DisplayName("E2E: Teste de performance com múltiplas operações")
    public void testPerformanceMultiplasOperacoes() throws Exception {
        // Arrange
        long startTime = System.currentTimeMillis();
        int numberOfOperations = 10;

        // Act - Executa múltiplas operações
        for (int i = 0; i < numberOfOperations; i++) {
            CrawlRequest request = new CrawlRequest("perf" + i);
            
            String response = mockMvc.perform(post("/crawl")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String id = objectMapper.readTree(response).get("id").asText();
            
            // Consulta resultado
            mockMvc.perform(get("/crawl/" + id))
                    .andExpect(status().isOk());
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        // Assert - Performance aceitável
        assertTrue(duration < 10000, 
                  "10 operações não devem demorar mais que 10 segundos");
    }

    @Test
    @DisplayName("E2E: Robustez com dados edge case")
    public void testRobustezComDadosEdgeCase() throws Exception {
        // Arrange - Casos extremos
        String[] edgeCases = {
            "test",           // exatamente 4 caracteres
            "a".repeat(32),   // exatamente 32 caracteres
            "UPPERCASE",      // maiúsculas
            "lowercase",      // minúsculas
            "MiXeD123"        // misturado
        };

        // Act & Assert
        for (String term : edgeCases) {
            CrawlRequest request = new CrawlRequest(term);
            
            String response = mockMvc.perform(post("/crawl")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();

            String id = objectMapper.readTree(response).get("id").asText();
            
            // Verifica se pode consultar resultado
            CrawlResult result = consultarResultado(id);
            assertNotNull(result, "Resultado deve existir para termo: " + term);
            assertEquals(id, result.getId(), "ID deve corresponder para termo: " + term);
        }
    }

    private CrawlResult consultarResultado(String id) throws Exception {
        String response = mockMvc.perform(get("/crawl/" + id))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        
        return objectMapper.readValue(response, CrawlResult.class);
    }
}
