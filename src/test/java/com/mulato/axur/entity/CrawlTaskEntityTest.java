package com.mulato.axur.entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CrawlTaskEntityTest {

    private CrawlTaskEntity crawlTaskEntity;
    private String testId;
    private String testKeyword;
    private String testBaseUrl;

    @BeforeEach
    void setUp() {
        testId = "test-task-123";
        testKeyword = "security";
        testBaseUrl = "http://example.com";
        crawlTaskEntity = new CrawlTaskEntity(testId, testKeyword, testBaseUrl);
    }

    @Test
    void testConstructorWithParameters() {
        // When
        CrawlTaskEntity entity = new CrawlTaskEntity(testId, testKeyword, testBaseUrl);

        // Then
        assertEquals(testId, entity.getId());
        assertEquals(testKeyword, entity.getKeyword());
        assertEquals(testBaseUrl, entity.getBaseUrl());
        assertEquals("active", entity.getStatus());
        assertNotNull(entity.getStartTime());
        assertNotNull(entity.getCreatedAt());
        assertNotNull(entity.getUpdatedAt());
        assertEquals(0, entity.getTotalPagesVisited());
        assertEquals(0, entity.getTotalUrlsFound());
        assertNull(entity.getEndTime());
    }

    @Test
    void testDefaultConstructor() {
        // When
        CrawlTaskEntity entity = new CrawlTaskEntity();

        // Then
        assertNull(entity.getId());
        assertNull(entity.getKeyword());
        assertNull(entity.getBaseUrl());
        assertNull(entity.getStatus());
        assertNull(entity.getStartTime());
        assertNull(entity.getEndTime());
        assertNull(entity.getCreatedAt());
        assertNull(entity.getUpdatedAt());
        assertNull(entity.getTotalPagesVisited());
        assertNull(entity.getTotalUrlsFound());
    }

    @Test
    void testSettersAndGetters() {
        // Given
        CrawlTaskEntity entity = new CrawlTaskEntity();
        LocalDateTime testTime = LocalDateTime.now();

        // When
        entity.setId("new-id");
        entity.setKeyword("new-keyword");
        entity.setBaseUrl("http://new-url.com");
        entity.setStatus("done");
        entity.setStartTime(testTime);
        entity.setEndTime(testTime.plusHours(1));
        entity.setTotalPagesVisited(50);
        entity.setTotalUrlsFound(25);
        entity.setCreatedAt(testTime);
        entity.setUpdatedAt(testTime.plusMinutes(30));

        // Then
        assertEquals("new-id", entity.getId());
        assertEquals("new-keyword", entity.getKeyword());
        assertEquals("http://new-url.com", entity.getBaseUrl());
        assertEquals("done", entity.getStatus());
        assertEquals(testTime, entity.getStartTime());
        assertEquals(testTime.plusHours(1), entity.getEndTime());
        assertEquals(50, entity.getTotalPagesVisited());
        assertEquals(25, entity.getTotalUrlsFound());
        assertEquals(testTime, entity.getCreatedAt());
        assertEquals(testTime.plusMinutes(30), entity.getUpdatedAt());
    }

    @Test
    void testResultsRelationship() {
        // Given
        List<CrawlResultEntity> results = new ArrayList<>();
        CrawlResultEntity result1 = new CrawlResultEntity(testId, "http://example.com/page1");
        CrawlResultEntity result2 = new CrawlResultEntity(testId, "http://example.com/page2");
        results.add(result1);
        results.add(result2);

        // When
        crawlTaskEntity.setResults(results);

        // Then
        assertEquals(2, crawlTaskEntity.getResults().size());
        assertTrue(crawlTaskEntity.getResults().contains(result1));
        assertTrue(crawlTaskEntity.getResults().contains(result2));
    }

    @Test
    void testVisitedUrlsRelationship() {
        // Given
        List<VisitedUrlEntity> visitedUrls = new ArrayList<>();
        VisitedUrlEntity visited1 = new VisitedUrlEntity(testId, "http://example.com/visited1");
        VisitedUrlEntity visited2 = new VisitedUrlEntity(testId, "http://example.com/visited2");
        visitedUrls.add(visited1);
        visitedUrls.add(visited2);

        // When
        crawlTaskEntity.setVisitedUrls(visitedUrls);

        // Then
        assertEquals(2, crawlTaskEntity.getVisitedUrls().size());
        assertTrue(crawlTaskEntity.getVisitedUrls().contains(visited1));
        assertTrue(crawlTaskEntity.getVisitedUrls().contains(visited2));
    }

    @Test
    void testSetTotalPagesVisited() {
        // Given
        int newCount = 42;

        // When
        crawlTaskEntity.setTotalPagesVisited(newCount);

        // Then
        assertEquals(newCount, crawlTaskEntity.getTotalPagesVisited());
    }

    @Test
    void testSetTotalUrlsFound() {
        // Given
        int newCount = 15;

        // When
        crawlTaskEntity.setTotalUrlsFound(newCount);

        // Then
        assertEquals(newCount, crawlTaskEntity.getTotalUrlsFound());
    }

    @Test
    void testEqualsAndHashCode() {
        // Given
        CrawlTaskEntity entity1 = new CrawlTaskEntity(testId, testKeyword, testBaseUrl);
        CrawlTaskEntity entity2 = new CrawlTaskEntity(testId, testKeyword, testBaseUrl);
        CrawlTaskEntity entity3 = new CrawlTaskEntity("different-id", testKeyword, testBaseUrl);
        
        // Test reflexivity
        assertEquals(entity1, entity1);
        
        // Test symmetry  
        assertEquals(entity1, entity2);
        assertEquals(entity2, entity1);
        
        // Test with different objects
        assertNotEquals(entity1, entity3);
        assertNotEquals(entity1, null);
        assertNotEquals(entity1, "string");
    }

    @Test
    void testToString() {
        // When
        String toString = crawlTaskEntity.toString();

        // Then
        assertNotNull(toString);
        assertTrue(toString.contains(testId));
        assertTrue(toString.contains(testKeyword));
        assertTrue(toString.contains(testBaseUrl));
    }
}
