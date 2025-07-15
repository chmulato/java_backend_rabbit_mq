package com.mulato.api.service;

import com.mulato.api.entity.CrawlTaskEntity;
import com.mulato.api.model.CrawlResult;
import com.mulato.api.model.CrawlTask;
import com.mulato.api.repository.CrawlResultRepository;
import com.mulato.api.repository.CrawlTaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrawlServiceTest {

    @Mock
    private CrawlMessageService crawlMessageService;

    @Mock
    private IdGeneratorService idGeneratorService;

    @Mock
    private CrawlTaskRepository crawlTaskRepository;

    @Mock
    private CrawlResultRepository crawlResultRepository;

    @InjectMocks
    private CrawlService crawlService;

    private String testBaseUrl;

    @BeforeEach
    void setUp() {
        testBaseUrl = "http://example.com";
        ReflectionTestUtils.setField(crawlService, "baseUrl", testBaseUrl);
    }

    @Test
    void testStartCrawl() {
        // Arrange
        String keyword = "security";
        String expectedId = "abc12345";
        
        when(idGeneratorService.generateId()).thenReturn(expectedId);
        doNothing().when(crawlMessageService).sendCrawlMessage(any(CrawlTask.class));

        // Act
        String crawlId = crawlService.startCrawl(keyword);

        // Assert
        assertEquals(expectedId, crawlId);
        verify(idGeneratorService).generateId();
        verify(crawlMessageService).sendCrawlMessage(any(CrawlTask.class));
    }

    @Test
    void testStartCrawlCreatesTask() {
        // Arrange
        String keyword = "testing";
        String expectedId = "xyz98765";
        
        when(idGeneratorService.generateId()).thenReturn(expectedId);
        doNothing().when(crawlMessageService).sendCrawlMessage(any(CrawlTask.class));

        // Act
        String crawlId = crawlService.startCrawl(keyword);

        // Assert
        assertEquals(expectedId, crawlId);
        
        // Verifica se a tarefa foi armazenada internamente
        CrawlResult result = crawlService.getCrawlResult(crawlId);
        assertNotNull(result);
        assertEquals(expectedId, result.getId());
        assertEquals("active", result.getStatus());
        assertNotNull(result.getUrls());
        assertTrue(result.getUrls().isEmpty()); // Inicialmente vazia
    }

    @Test
    void testGetCrawlResultExisting() {
        // Arrange
        String keyword = "security";
        String crawlId = "test123";
        
        when(idGeneratorService.generateId()).thenReturn(crawlId);
        doNothing().when(crawlMessageService).sendCrawlMessage(any(CrawlTask.class));
        
        // Primeiro cria uma tarefa
        crawlService.startCrawl(keyword);

        // Act
        CrawlResult result = crawlService.getCrawlResult(crawlId);

        // Assert
        assertNotNull(result);
        assertEquals(crawlId, result.getId());
        assertEquals("active", result.getStatus());
        assertNotNull(result.getUrls());
    }

    @Test
    void testGetCrawlResultNonExisting() {
        // Act
        CrawlResult result = crawlService.getCrawlResult("nonexistent");

        // Assert
        assertNull(result);
    }

    @Test
    void testGetActiveCrawlTask() {
        // Arrange
        String keyword = "testing";
        String crawlId = "task123";
        
        when(idGeneratorService.generateId()).thenReturn(crawlId);
        doNothing().when(crawlMessageService).sendCrawlMessage(any(CrawlTask.class));
        
        // Primeiro cria uma tarefa
        crawlService.startCrawl(keyword);

        // Act
        CrawlTask task = crawlService.getActiveCrawlTask(crawlId);

        // Assert
        assertNotNull(task);
        assertEquals(crawlId, task.getId());
        assertEquals(keyword, task.getKeyword());
        assertEquals(testBaseUrl, task.getBaseUrl());
        assertTrue(task.isActive());
    }

    @Test
    void testGetActiveCrawlTaskNonExisting() {
        // Act
        CrawlTask task = crawlService.getActiveCrawlTask("nonexistent");

        // Assert
        assertNull(task);
    }

    @Test
    void testMultipleCrawlTasks() {
        // Arrange
        String keyword1 = "security";
        String keyword2 = "testing";
        String id1 = "task001";
        String id2 = "task002";
        
        when(idGeneratorService.generateId())
            .thenReturn(id1)
            .thenReturn(id2);
        doNothing().when(crawlMessageService).sendCrawlMessage(any(CrawlTask.class));

        // Act
        String crawlId1 = crawlService.startCrawl(keyword1);
        String crawlId2 = crawlService.startCrawl(keyword2);

        // Assert
        assertEquals(id1, crawlId1);
        assertEquals(id2, crawlId2);
        
        CrawlResult result1 = crawlService.getCrawlResult(crawlId1);
        CrawlResult result2 = crawlService.getCrawlResult(crawlId2);
        
        assertNotNull(result1);
        assertNotNull(result2);
        assertNotEquals(result1.getId(), result2.getId());
        
        CrawlTask task1 = crawlService.getActiveCrawlTask(crawlId1);
        CrawlTask task2 = crawlService.getActiveCrawlTask(crawlId2);
        
        assertEquals(keyword1, task1.getKeyword());
        assertEquals(keyword2, task2.getKeyword());
    }

    @Test
    void testStartCrawlWithDifferentKeywords() {
        // Arrange
        String[] keywords = {"security", "privacy", "authentication", "authorization"};
        String[] ids = {"id1", "id2", "id3", "id4"};
        
        when(idGeneratorService.generateId())
            .thenReturn(ids[0])
            .thenReturn(ids[1])
            .thenReturn(ids[2])
            .thenReturn(ids[3]);
        doNothing().when(crawlMessageService).sendCrawlMessage(any(CrawlTask.class));

        // Act & Assert
        for (int i = 0; i < keywords.length; i++) {
            String crawlId = crawlService.startCrawl(keywords[i]);
            assertEquals(ids[i], crawlId);
            
            CrawlTask task = crawlService.getActiveCrawlTask(crawlId);
            assertNotNull(task);
            assertEquals(keywords[i], task.getKeyword());
            assertEquals(testBaseUrl, task.getBaseUrl());
        }
        
        // Verifica se todas as tarefas foram criadas
        for (String id : ids) {
            assertNotNull(crawlService.getCrawlResult(id));
        }
    }

    @Test
    void testCrawlResultStatusMapping() {
        // Arrange
        String keyword = "status_test";
        String crawlId = "status123";
        
        when(idGeneratorService.generateId()).thenReturn(crawlId);
        doNothing().when(crawlMessageService).sendCrawlMessage(any(CrawlTask.class));
        
        // Act
        crawlService.startCrawl(keyword);
        CrawlTask task = crawlService.getActiveCrawlTask(crawlId);
        CrawlResult result = crawlService.getCrawlResult(crawlId);

        // Assert - tarefa ativa
        assertTrue(task.isActive());
        assertEquals("active", result.getStatus());
        
        // Simula finalização da tarefa
        task.setActive(false);
        result = crawlService.getCrawlResult(crawlId);
        assertEquals("done", result.getStatus());
    }

    @Test
    void testBaseUrlConfiguration() {
        // Arrange
        String customBaseUrl = "http://custom.example.com";
        ReflectionTestUtils.setField(crawlService, "baseUrl", customBaseUrl);
        
        String keyword = "url_test";
        String crawlId = "url123";
        
        when(idGeneratorService.generateId()).thenReturn(crawlId);
        doNothing().when(crawlMessageService).sendCrawlMessage(any(CrawlTask.class));

        // Act
        crawlService.startCrawl(keyword);
        CrawlTask task = crawlService.getActiveCrawlTask(crawlId);

        // Assert
        assertEquals(customBaseUrl, task.getBaseUrl());
    }
}
