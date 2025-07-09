package com.mulato.axur.service;

import com.mulato.axur.entity.CrawlResultEntity;
import com.mulato.axur.entity.VisitedUrlEntity;
import com.mulato.axur.repository.CrawlResultRepository;
import com.mulato.axur.repository.VisitedUrlRepository;
import com.mulato.axur.util.LogCapture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CrawlPersistenceServiceTest {

    @Mock
    private CrawlResultRepository crawlResultRepository;

    @Mock
    private VisitedUrlRepository visitedUrlRepository;

    @InjectMocks
    private CrawlPersistenceService crawlPersistenceService;

    private String testTaskId;
    private String testUrl;

    @BeforeEach
    void setUp() {
        testTaskId = "test-task-123";
        testUrl = "http://example.com/page1";
    }

    @Test
    void testSaveFoundUrl_Success() {
        // Given
        when(crawlResultRepository.save(any(CrawlResultEntity.class)))
                .thenReturn(new CrawlResultEntity(testTaskId, testUrl));

        // When
        crawlPersistenceService.saveFoundUrl(testTaskId, testUrl);

        // Then
        verify(crawlResultRepository, times(1)).save(any(CrawlResultEntity.class));
    }

    @Test
    void testSaveFoundUrl_Exception() {
        // Given
        LogCapture logCapture = new LogCapture(CrawlPersistenceService.class);
        doThrow(new RuntimeException("Database error"))
                .when(crawlResultRepository).save(any(CrawlResultEntity.class));

        // When
        crawlPersistenceService.saveFoundUrl(testTaskId, testUrl);

        // Then
        verify(crawlResultRepository, times(1)).save(any(CrawlResultEntity.class));
        assertTrue(logCapture.hasLogMessage("Error saving found URL for task " + testTaskId));
        
        logCapture.stop();
    }

    @Test
    void testSaveVisitedUrl_Success() {
        // Given
        when(visitedUrlRepository.existsByTaskIdAndUrl(testTaskId, testUrl)).thenReturn(false);
        when(visitedUrlRepository.save(any(VisitedUrlEntity.class)))
                .thenReturn(new VisitedUrlEntity(testTaskId, testUrl));

        // When
        crawlPersistenceService.saveVisitedUrl(testTaskId, testUrl);

        // Then
        verify(visitedUrlRepository, times(1)).existsByTaskIdAndUrl(testTaskId, testUrl);
        verify(visitedUrlRepository, times(1)).save(any(VisitedUrlEntity.class));
    }

    @Test
    void testSaveVisitedUrl_AlreadyExists() {
        // Given
        when(visitedUrlRepository.existsByTaskIdAndUrl(testTaskId, testUrl)).thenReturn(true);

        // When
        crawlPersistenceService.saveVisitedUrl(testTaskId, testUrl);

        // Then
        verify(visitedUrlRepository, times(1)).existsByTaskIdAndUrl(testTaskId, testUrl);
        verify(visitedUrlRepository, never()).save(any(VisitedUrlEntity.class));
    }

    @Test
    void testSaveVisitedUrl_Exception() {
        // Given
        LogCapture logCapture = new LogCapture(CrawlPersistenceService.class);
        when(visitedUrlRepository.existsByTaskIdAndUrl(testTaskId, testUrl)).thenReturn(false);
        doThrow(new RuntimeException("Database error"))
                .when(visitedUrlRepository).save(any(VisitedUrlEntity.class));

        // When
        crawlPersistenceService.saveVisitedUrl(testTaskId, testUrl);

        // Then
        verify(visitedUrlRepository, times(1)).save(any(VisitedUrlEntity.class));
        assertTrue(logCapture.hasLogMessage("Error saving visited URL for task " + testTaskId));
        
        logCapture.stop();
    }

    @Test
    void testIsUrlVisited_True() {
        // Given
        when(visitedUrlRepository.existsByTaskIdAndUrl(testTaskId, testUrl)).thenReturn(true);

        // When
        boolean result = crawlPersistenceService.isUrlVisited(testTaskId, testUrl);

        // Then
        assertTrue(result);
        verify(visitedUrlRepository, times(1)).existsByTaskIdAndUrl(testTaskId, testUrl);
    }

    @Test
    void testIsUrlVisited_False() {
        // Given
        when(visitedUrlRepository.existsByTaskIdAndUrl(testTaskId, testUrl)).thenReturn(false);

        // When
        boolean result = crawlPersistenceService.isUrlVisited(testTaskId, testUrl);

        // Then
        assertFalse(result);
        verify(visitedUrlRepository, times(1)).existsByTaskIdAndUrl(testTaskId, testUrl);
    }

    @Test
    void testGetFoundUrls() {
        // Given
        List<String> expectedUrls = Arrays.asList(
                "http://example.com/page1",
                "http://example.com/page2",
                "http://example.com/page3"
        );
        when(crawlResultRepository.findUrlsByTaskId(testTaskId)).thenReturn(expectedUrls);

        // When
        List<String> result = crawlPersistenceService.getFoundUrls(testTaskId);

        // Then
        assertEquals(expectedUrls, result);
        verify(crawlResultRepository, times(1)).findUrlsByTaskId(testTaskId);
    }

    @Test
    void testGetVisitedUrls() {
        // Given
        List<String> expectedUrls = Arrays.asList(
                "http://example.com/page1",
                "http://example.com/page2",
                "http://example.com/visited1"
        );
        when(visitedUrlRepository.findUrlsByTaskId(testTaskId)).thenReturn(expectedUrls);

        // When
        List<String> result = crawlPersistenceService.getVisitedUrls(testTaskId);

        // Then
        assertEquals(expectedUrls, result);
        verify(visitedUrlRepository, times(1)).findUrlsByTaskId(testTaskId);
    }

    @Test
    void testGetFoundUrlsCount() {
        // Given
        long expectedCount = 15L;
        when(crawlResultRepository.countByTaskId(testTaskId)).thenReturn(expectedCount);

        // When
        long result = crawlPersistenceService.getFoundUrlsCount(testTaskId);

        // Then
        assertEquals(expectedCount, result);
        verify(crawlResultRepository, times(1)).countByTaskId(testTaskId);
    }

    @Test
    void testGetVisitedUrlsCount() {
        // Given
        long expectedCount = 42L;
        when(visitedUrlRepository.countByTaskId(testTaskId)).thenReturn(expectedCount);

        // When
        long result = crawlPersistenceService.getVisitedUrlsCount(testTaskId);

        // Then
        assertEquals(expectedCount, result);
        verify(visitedUrlRepository, times(1)).countByTaskId(testTaskId);
    }

    @Test
    void testGetFoundUrls_EmptyList() {
        // Given
        when(crawlResultRepository.findUrlsByTaskId(testTaskId)).thenReturn(Arrays.asList());

        // When
        List<String> result = crawlPersistenceService.getFoundUrls(testTaskId);

        // Then
        assertTrue(result.isEmpty());
        verify(crawlResultRepository, times(1)).findUrlsByTaskId(testTaskId);
    }

    @Test
    void testGetFoundUrlsCount_Zero() {
        // Given
        when(crawlResultRepository.countByTaskId(testTaskId)).thenReturn(0L);

        // When
        long result = crawlPersistenceService.getFoundUrlsCount(testTaskId);

        // Then
        assertEquals(0L, result);
        verify(crawlResultRepository, times(1)).countByTaskId(testTaskId);
    }
}
