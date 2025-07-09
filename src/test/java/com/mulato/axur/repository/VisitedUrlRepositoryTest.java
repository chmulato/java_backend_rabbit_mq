package com.mulato.axur.repository;

import com.mulato.axur.entity.VisitedUrlEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class VisitedUrlRepositoryTest {

    @Autowired
    private VisitedUrlRepository visitedUrlRepository;

    private String testTaskId1;
    private String testTaskId2;

    @BeforeEach
    void setUp() {
        visitedUrlRepository.deleteAll();
        
        testTaskId1 = "task001";
        testTaskId2 = "task002";
    }

    @Test
    void testSaveAndFindById() {
        // Given
        VisitedUrlEntity visitedUrl = new VisitedUrlEntity(testTaskId1, "http://example.com/visited1");

        // When
        VisitedUrlEntity savedUrl = visitedUrlRepository.save(visitedUrl);

        // Then
        assertNotNull(savedUrl.getId());
        assertEquals(testTaskId1, savedUrl.getTaskId());
        assertEquals("http://example.com/visited1", savedUrl.getUrl());
        assertNotNull(savedUrl.getVisitedAt());
    }

    @Test
    void testFindByTaskId() {
        // Given
        VisitedUrlEntity visited1 = new VisitedUrlEntity(testTaskId1, "http://example.com/page1");
        VisitedUrlEntity visited2 = new VisitedUrlEntity(testTaskId1, "http://example.com/page2");
        VisitedUrlEntity visited3 = new VisitedUrlEntity(testTaskId2, "http://test.com/page1");
        
        visitedUrlRepository.saveAll(List.of(visited1, visited2, visited3));

        // When
        List<VisitedUrlEntity> visitedForTask1 = visitedUrlRepository.findByTaskId(testTaskId1);
        List<VisitedUrlEntity> visitedForTask2 = visitedUrlRepository.findByTaskId(testTaskId2);

        // Then
        assertEquals(2, visitedForTask1.size());
        assertEquals(1, visitedForTask2.size());
        
        assertTrue(visitedForTask1.stream().anyMatch(v -> v.getUrl().equals("http://example.com/page1")));
        assertTrue(visitedForTask1.stream().anyMatch(v -> v.getUrl().equals("http://example.com/page2")));
        assertTrue(visitedForTask2.stream().anyMatch(v -> v.getUrl().equals("http://test.com/page1")));
    }

    @Test
    void testFindByTaskId_NoResults() {
        // Given
        VisitedUrlEntity visited = new VisitedUrlEntity(testTaskId1, "http://example.com/page1");
        visitedUrlRepository.save(visited);

        // When
        List<VisitedUrlEntity> results = visitedUrlRepository.findByTaskId("nonexistent");

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    void testExistsByTaskIdAndUrl() {
        // Given
        String url = "http://example.com/page1";
        VisitedUrlEntity visited = new VisitedUrlEntity(testTaskId1, url);
        visitedUrlRepository.save(visited);

        // When & Then
        assertTrue(visitedUrlRepository.existsByTaskIdAndUrl(testTaskId1, url));
        assertFalse(visitedUrlRepository.existsByTaskIdAndUrl(testTaskId1, "http://example.com/page2"));
        assertFalse(visitedUrlRepository.existsByTaskIdAndUrl(testTaskId2, url));
        assertFalse(visitedUrlRepository.existsByTaskIdAndUrl("nonexistent", url));
    }

    @Test
    void testFindUrlsByTaskId() {
        // Given
        VisitedUrlEntity visited1 = new VisitedUrlEntity(testTaskId1, "http://example.com/page1");
        VisitedUrlEntity visited2 = new VisitedUrlEntity(testTaskId1, "http://example.com/page2");
        VisitedUrlEntity visited3 = new VisitedUrlEntity(testTaskId2, "http://test.com/page1");
        
        visitedUrlRepository.saveAll(List.of(visited1, visited2, visited3));

        // When
        List<String> urlsForTask1 = visitedUrlRepository.findUrlsByTaskId(testTaskId1);
        List<String> urlsForTask2 = visitedUrlRepository.findUrlsByTaskId(testTaskId2);

        // Then
        assertEquals(2, urlsForTask1.size());
        assertEquals(1, urlsForTask2.size());
        
        assertTrue(urlsForTask1.contains("http://example.com/page1"));
        assertTrue(urlsForTask1.contains("http://example.com/page2"));
        assertTrue(urlsForTask2.contains("http://test.com/page1"));
    }

    @Test
    void testFindUrlsByTaskId_EmptyResults() {
        // When
        List<String> urls = visitedUrlRepository.findUrlsByTaskId("nonexistent");

        // Then
        assertTrue(urls.isEmpty());
    }

    @Test
    void testCountByTaskId() {
        // Given
        VisitedUrlEntity visited1 = new VisitedUrlEntity(testTaskId1, "http://example.com/page1");
        VisitedUrlEntity visited2 = new VisitedUrlEntity(testTaskId1, "http://example.com/page2");
        VisitedUrlEntity visited3 = new VisitedUrlEntity(testTaskId2, "http://test.com/page1");
        
        visitedUrlRepository.saveAll(List.of(visited1, visited2, visited3));

        // When
        Long countForTask1 = visitedUrlRepository.countByTaskId(testTaskId1);
        Long countForTask2 = visitedUrlRepository.countByTaskId(testTaskId2);
        Long countForNonexistent = visitedUrlRepository.countByTaskId("nonexistent");

        // Then
        assertEquals(2L, countForTask1);
        assertEquals(1L, countForTask2);
        assertEquals(0L, countForNonexistent);
    }

    @Test
    void testDeleteByTaskId() {
        // Given
        VisitedUrlEntity visited1 = new VisitedUrlEntity(testTaskId1, "http://example.com/page1");
        VisitedUrlEntity visited2 = new VisitedUrlEntity(testTaskId1, "http://example.com/page2");
        VisitedUrlEntity visited3 = new VisitedUrlEntity(testTaskId2, "http://test.com/page1");
        
        visitedUrlRepository.saveAll(List.of(visited1, visited2, visited3));
        assertEquals(3L, visitedUrlRepository.count());

        // When
        visitedUrlRepository.deleteByTaskId(testTaskId1);

        // Then
        assertEquals(1L, visitedUrlRepository.count());
        assertTrue(visitedUrlRepository.findByTaskId(testTaskId1).isEmpty());
        assertFalse(visitedUrlRepository.findByTaskId(testTaskId2).isEmpty());
    }

    @Test
    void testSaveDuplicateUrls_SameTask() {
        // Given
        String url = "http://example.com/page1";
        VisitedUrlEntity visited1 = new VisitedUrlEntity(testTaskId1, url);
        VisitedUrlEntity visited2 = new VisitedUrlEntity(testTaskId1, url);

        // When
        visitedUrlRepository.save(visited1);
        visitedUrlRepository.save(visited2);

        // Then
        Long count = visitedUrlRepository.countByTaskId(testTaskId1);
        assertEquals(2L, count); // Both should be saved (different entities)
        assertTrue(visitedUrlRepository.existsByTaskIdAndUrl(testTaskId1, url));
    }

    @Test
    void testSaveSameUrl_DifferentTasks() {
        // Given
        String url = "http://example.com/page1";
        VisitedUrlEntity visited1 = new VisitedUrlEntity(testTaskId1, url);
        VisitedUrlEntity visited2 = new VisitedUrlEntity(testTaskId2, url);

        // When
        visitedUrlRepository.saveAll(List.of(visited1, visited2));

        // Then
        assertTrue(visitedUrlRepository.existsByTaskIdAndUrl(testTaskId1, url));
        assertTrue(visitedUrlRepository.existsByTaskIdAndUrl(testTaskId2, url));
        assertEquals(1L, visitedUrlRepository.countByTaskId(testTaskId1));
        assertEquals(1L, visitedUrlRepository.countByTaskId(testTaskId2));
    }

    @Test
    void testSaveMultipleVisitedUrls() {
        // Given
        String taskId = "task-bulk";
        
        // When
        for (int i = 1; i <= 5; i++) {
            VisitedUrlEntity visited = new VisitedUrlEntity(taskId, "http://example.com/page" + i);
            visitedUrlRepository.save(visited);
        }

        // Then
        Long count = visitedUrlRepository.countByTaskId(taskId);
        List<VisitedUrlEntity> visitedUrls = visitedUrlRepository.findByTaskId(taskId);
        
        assertEquals(5L, count);
        assertEquals(5, visitedUrls.size());
    }

    @Test
    void testVisitedAtAutoGeneration() {
        // Given
        VisitedUrlEntity visited = new VisitedUrlEntity(testTaskId1, "http://example.com/page1");

        // When
        VisitedUrlEntity savedVisited = visitedUrlRepository.save(visited);

        // Then
        assertNotNull(savedVisited.getVisitedAt());
    }

    @Test
    void testLongUrl() {
        // Given
        String longUrl = "http://example.com/" + "b".repeat(900);
        VisitedUrlEntity visited = new VisitedUrlEntity(testTaskId1, longUrl);

        // When
        VisitedUrlEntity savedVisited = visitedUrlRepository.save(visited);

        // Then
        assertEquals(longUrl, savedVisited.getUrl());
        assertEquals(testTaskId1, savedVisited.getTaskId());
        assertTrue(visitedUrlRepository.existsByTaskIdAndUrl(testTaskId1, longUrl));
    }

    @Test
    void testDeleteByTaskId_NoEffect() {
        // Given
        VisitedUrlEntity visited = new VisitedUrlEntity(testTaskId1, "http://example.com/page1");
        visitedUrlRepository.save(visited);
        assertEquals(1L, visitedUrlRepository.count());

        // When
        visitedUrlRepository.deleteByTaskId("nonexistent");

        // Then
        assertEquals(1L, visitedUrlRepository.count()); // Should remain unchanged
    }
}
