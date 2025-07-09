package com.mulato.axur.validation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mulato.axur.controller.ApiHttpController;
import com.mulato.axur.model.CrawlRequest;
import com.mulato.axur.service.CrawlService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Testes para o Requisito 2: Validação do termo
 * 
 * Testa as validações conforme especificado:
 * - Mínimo 4 caracteres
 * - Máximo 32 caracteres
 * - Busca case insensitive
 */
@WebMvcTest(ApiHttpController.class)
@DisplayName("Requisito 2: Validação do Termo")
public class TermValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CrawlService crawlService;

    @Test
    @DisplayName("Deve aceitar termo com exatamente 4 caracteres (limite mínimo)")
    public void testTermoComQuatroCaracteres_DeveSerAceito() throws Exception {
        // Arrange
        when(crawlService.startCrawl("test")).thenReturn("abcd1234");
        CrawlRequest request = new CrawlRequest("test");

        // Act & Assert
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve aceitar termo com exatamente 32 caracteres (limite máximo)")
    public void testTermoComTrintaDoisCaracteres_DeveSerAceito() throws Exception {
        // Arrange
        String keyword32Chars = "a".repeat(32);
        when(crawlService.startCrawl(keyword32Chars)).thenReturn("abcd1234");
        CrawlRequest request = new CrawlRequest(keyword32Chars);

        // Act & Assert
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "a", "ab", "abc"})
    @DisplayName("Deve rejeitar termos com menos de 4 caracteres")
    public void testTermoComMenosDeQuatroCaracteres_DeveSerRejeitado(String keyword) throws Exception {
        // Arrange
        CrawlRequest request = new CrawlRequest(keyword);

        // Act & Assert
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve rejeitar termo com mais de 32 caracteres")
    public void testTermoComMaisDeTrintaDoisCaracteres_DeveSerRejeitado() throws Exception {
        // Arrange
        String keyword33Chars = "a".repeat(33);
        CrawlRequest request = new CrawlRequest(keyword33Chars);

        // Act & Assert
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"security", "SECURITY", "Security", "sEcUrItY"})
    @DisplayName("Deve aceitar diferentes variações de case (case insensitive)")
    public void testTermosComDiferentesCases_DevemSerAceitos(String keyword) throws Exception {
        // Arrange
        when(crawlService.startCrawl(anyString())).thenReturn("test1234");
        CrawlRequest request = new CrawlRequest(keyword);

        // Act & Assert
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }
}
