package com.mulato.axur.repository;

import com.mulato.axur.entity.CrawlResultEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CrawlResultRepositoryTest {

    @Autowired
    private CrawlResultRepository crawlResultRepository;

    private String testTaskId1;
    private String testTaskId2;

    @BeforeEach
    void setUp() {
        crawlResultRepository.deleteAll();
        
        testTaskId1 = "task001";
        testTaskId2 = "task002";
    }

    @Test
    void testSaveAndFindById() {
        // Given
        CrawlResultEntity result = new CrawlResultEntity(testTaskId1, "http://example.com/page1");

        // When
        CrawlResultEntity savedResult = crawlResultRepository.save(result);

        // Then
        assertNotNull(savedResult.getId());
        assertEquals(testTaskId1, savedResult.getTaskId());
        assertEquals("http://example.com/page1", savedResult.getUrl());
        assertNotNull(savedResult.getFoundAt());
    }

    @Test
    void testFindByTaskId() {
        // Given
        CrawlResultEntity result1 = new CrawlResultEntity(testTaskId1, "http://example.com/page1");
        CrawlResultEntity result2 = new CrawlResultEntity(testTaskId1, "http://example.com/page2");
        CrawlResultEntity result3 = new CrawlResultEntity(testTaskId2, "http://test.com/page1");
        
        crawlResultRepository.saveAll(List.of(result1, result2, result3));

        // When
        List<CrawlResultEntity> resultsForTask1 = crawlResultRepository.findByTaskId(testTaskId1);
        List<CrawlResultEntity> resultsForTask2 = crawlResultRepository.findByTaskId(testTaskId2);

        // Then
        assertEquals(2, resultsForTask1.size());
        assertEquals(1, resultsForTask2.size());
        
        assertTrue(resultsForTask1.stream().anyMatch(r -> r.getUrl().equals("http://example.com/page1")));
        assertTrue(resultsForTask1.stream().anyMatch(r -> r.getUrl().equals("http://example.com/page2")));
        assertTrue(resultsForTask2.stream().anyMatch(r -> r.getUrl().equals("http://test.com/page1")));
    }

    @Test
    void testFindByTaskId_NoResults() {
        // Given
        CrawlResultEntity result = new CrawlResultEntity(testTaskId1, "http://example.com/page1");
        crawlResultRepository.save(result);

        // When
        List<CrawlResultEntity> results = crawlResultRepository.findByTaskId("nonexistent");

        // Then
        assertTrue(results.isEmpty());
    }

    @Test
    void testFindUrlsByTaskId() {
        // Given
        CrawlResultEntity result1 = new CrawlResultEntity(testTaskId1, "http://example.com/page1");
        result1.setFoundAt(LocalDateTime.now().minusMinutes(10));
        
        CrawlResultEntity result2 = new CrawlResultEntity(testTaskId1, "http://example.com/page2");
        result2.setFoundAt(LocalDateTime.now().minusMinutes(5));
        
        CrawlResultEntity result3 = new CrawlResultEntity(testTaskId1, "http://example.com/page3");
        result3.setFoundAt(LocalDateTime.now());
        
        crawlResultRepository.saveAll(List.of(result1, result2, result3));

        // When
        List<String> urls = crawlResultRepository.findUrlsByTaskId(testTaskId1);

        // Then
        assertEquals(3, urls.size());
        // Should be ordered by foundAt (oldest first)
        assertEquals("http://example.com/page1", urls.get(0));
        assertEquals("http://example.com/page2", urls.get(1));
        assertEquals("http://example.com/page3", urls.get(2));
    }

    @Test
    void testFindUrlsByTaskId_EmptyResults() {
        // When
        List<String> urls = crawlResultRepository.findUrlsByTaskId("nonexistent");

        // Then
        assertTrue(urls.isEmpty());
    }

    @Test
    void testCountByTaskId() {
        // Given
        CrawlResultEntity result1 = new CrawlResultEntity(testTaskId1, "http://example.com/page1");
        CrawlResultEntity result2 = new CrawlResultEntity(testTaskId1, "http://example.com/page2");
        CrawlResultEntity result3 = new CrawlResultEntity(testTaskId2, "http://test.com/page1");
        
        crawlResultRepository.saveAll(List.of(result1, result2, result3));

        // When
        Long countForTask1 = crawlResultRepository.countByTaskId(testTaskId1);
        Long countForTask2 = crawlResultRepository.countByTaskId(testTaskId2);
        Long countForNonexistent = crawlResultRepository.countByTaskId("nonexistent");

        // Then
        assertEquals(2L, countForTask1);
        assertEquals(1L, countForTask2);
        assertEquals(0L, countForNonexistent);
    }

    @Test
    void testDeleteByTaskId() {
        // Given
        CrawlResultEntity result1 = new CrawlResultEntity(testTaskId1, "http://example.com/page1");
        CrawlResultEntity result2 = new CrawlResultEntity(testTaskId1, "http://example.com/page2");
        CrawlResultEntity result3 = new CrawlResultEntity(testTaskId2, "http://test.com/page1");
        
        crawlResultRepository.saveAll(List.of(result1, result2, result3));
        assertEquals(3L, crawlResultRepository.count());

        // When
        crawlResultRepository.deleteByTaskId(testTaskId1);

        // Then
        assertEquals(1L, crawlResultRepository.count());
        assertTrue(crawlResultRepository.findByTaskId(testTaskId1).isEmpty());
        assertFalse(crawlResultRepository.findByTaskId(testTaskId2).isEmpty());
    }

    @Test
    void testSaveMultipleResultsSameTask() {
        // Given
        String taskId = "task-bulk";
        
        // When
        for (int i = 1; i <= 5; i++) {
            CrawlResultEntity result = new CrawlResultEntity(taskId, "http://example.com/page" + i);
            crawlResultRepository.save(result);
        }

        // Then
        Long count = crawlResultRepository.countByTaskId(taskId);
        List<CrawlResultEntity> results = crawlResultRepository.findByTaskId(taskId);
        
        assertEquals(5L, count);
        assertEquals(5, results.size());
    }

    @Test
    void testFoundAtAutoGeneration() {
        // Given
        LocalDateTime beforeSave = LocalDateTime.now().minusSeconds(1);
        CrawlResultEntity result = new CrawlResultEntity(testTaskId1, "http://example.com/page1");

        // When
        CrawlResultEntity savedResult = crawlResultRepository.save(result);

        // Then
        LocalDateTime afterSave = LocalDateTime.now().plusSeconds(1);
        assertTrue(savedResult.getFoundAt().isAfter(beforeSave));
        assertTrue(savedResult.getFoundAt().isBefore(afterSave));
    }

    @Test
    void testLongUrl() {
        // Given
        String longUrl = "http://example.com/" + "a".repeat(900);
        CrawlResultEntity result = new CrawlResultEntity(testTaskId1, longUrl);

        // When
        CrawlResultEntity savedResult = crawlResultRepository.save(result);

        // Then
        assertEquals(longUrl, savedResult.getUrl());
        assertEquals(testTaskId1, savedResult.getTaskId());
    }

    @Test
    void testOrderingByFoundAt() {
        // Given
        LocalDateTime baseTime = LocalDateTime.now();
        
        CrawlResultEntity result1 = new CrawlResultEntity(testTaskId1, "http://example.com/third");
        result1.setFoundAt(baseTime.plusMinutes(10));
        
        CrawlResultEntity result2 = new CrawlResultEntity(testTaskId1, "http://example.com/first");
        result2.setFoundAt(baseTime);
        
        CrawlResultEntity result3 = new CrawlResultEntity(testTaskId1, "http://example.com/second");
        result3.setFoundAt(baseTime.plusMinutes(5));
        
        crawlResultRepository.saveAll(List.of(result1, result2, result3));

        // When
        List<String> urls = crawlResultRepository.findUrlsByTaskId(testTaskId1);

        // Then
        assertEquals("http://example.com/first", urls.get(0));
        assertEquals("http://example.com/second", urls.get(1));
        assertEquals("http://example.com/third", urls.get(2));
    }
}
