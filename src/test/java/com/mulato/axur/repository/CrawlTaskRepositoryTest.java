package com.mulato.axur.repository;

import com.mulato.axur.entity.CrawlTaskEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class CrawlTaskRepositoryTest {

    @Autowired
    private CrawlTaskRepository crawlTaskRepository;

    private CrawlTaskEntity testTask1;
    private CrawlTaskEntity testTask2;
    private CrawlTaskEntity testTask3;

    @BeforeEach
    void setUp() {
        crawlTaskRepository.deleteAll();
        
        testTask1 = new CrawlTaskEntity("task001", "security", "http://example.com");
        testTask2 = new CrawlTaskEntity("task002", "privacy", "http://test.com");
        testTask3 = new CrawlTaskEntity("task003", "safety", "http://demo.com");
        
        // Set different statuses
        testTask1.setStatus("active");
        testTask2.setStatus("done");
        testTask3.setStatus("active");
        
        // Set different start times
        LocalDateTime now = LocalDateTime.now();
        testTask1.setStartTime(now.minusHours(1));
        testTask2.setStartTime(now.minusHours(2));
        testTask3.setStartTime(now.minusMinutes(30));
    }

    @Test
    void testSaveAndFindById() {
        // When
        CrawlTaskEntity savedTask = crawlTaskRepository.save(testTask1);
        Optional<CrawlTaskEntity> foundTask = crawlTaskRepository.findById("task001");

        // Then
        assertNotNull(savedTask);
        assertTrue(foundTask.isPresent());
        assertEquals("task001", foundTask.get().getId());
        assertEquals("security", foundTask.get().getKeyword());
        assertEquals("http://example.com", foundTask.get().getBaseUrl());
        assertEquals("active", foundTask.get().getStatus());
    }

    @Test
    void testFindByStatus() {
        // Given
        crawlTaskRepository.saveAll(List.of(testTask1, testTask2, testTask3));

        // When
        List<CrawlTaskEntity> activeTasks = crawlTaskRepository.findByStatus("active");
        List<CrawlTaskEntity> doneTasks = crawlTaskRepository.findByStatus("done");

        // Then
        assertEquals(2, activeTasks.size());
        assertEquals(1, doneTasks.size());
        
        assertTrue(activeTasks.stream().anyMatch(task -> task.getId().equals("task001")));
        assertTrue(activeTasks.stream().anyMatch(task -> task.getId().equals("task003")));
        assertTrue(doneTasks.stream().anyMatch(task -> task.getId().equals("task002")));
    }

    @Test
    void testFindByStatus_NoResults() {
        // Given
        crawlTaskRepository.saveAll(List.of(testTask1, testTask2, testTask3));

        // When
        List<CrawlTaskEntity> errorTasks = crawlTaskRepository.findByStatus("error");

        // Then
        assertTrue(errorTasks.isEmpty());
    }

    @Test
    void testFindByStartTimeBetween() {
        // Given
        crawlTaskRepository.saveAll(List.of(testTask1, testTask2, testTask3));
        LocalDateTime start = LocalDateTime.now().minusHours(2).minusMinutes(30);
        LocalDateTime end = LocalDateTime.now().minusMinutes(45);

        // When
        List<CrawlTaskEntity> tasksInRange = crawlTaskRepository.findByStartTimeBetween(start, end);

        // Then
        assertEquals(2, tasksInRange.size());
        assertTrue(tasksInRange.stream().anyMatch(task -> task.getId().equals("task001")));
        assertTrue(tasksInRange.stream().anyMatch(task -> task.getId().equals("task002")));
    }

    @Test
    void testFindStaleActiveTasks() {
        // Given
        crawlTaskRepository.saveAll(List.of(testTask1, testTask2, testTask3));
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(45); // Tasks older than 45 minutes

        // When
        List<CrawlTaskEntity> staleTasks = crawlTaskRepository.findStaleActiveTasks(timeout);

        // Then
        assertEquals(1, staleTasks.size());
        assertEquals("task001", staleTasks.get(0).getId()); // Only task001 is active and older than 45 minutes
    }

    @Test
    void testFindStaleActiveTasks_NoStaleActiveTasks() {
        // Given
        testTask1.setStatus("done"); // Change to done
        crawlTaskRepository.saveAll(List.of(testTask1, testTask2, testTask3));
        LocalDateTime timeout = LocalDateTime.now().minusMinutes(45);

        // When
        List<CrawlTaskEntity> staleTasks = crawlTaskRepository.findStaleActiveTasks(timeout);

        // Then
        assertTrue(staleTasks.isEmpty()); // No active tasks older than timeout
    }

    @Test
    void testUpdateTask() {
        // Given
        CrawlTaskEntity savedTask = crawlTaskRepository.save(testTask1);
        
        // When
        savedTask.setStatus("done");
        savedTask.setEndTime(LocalDateTime.now());
        savedTask.setTotalPagesVisited(50);
        savedTask.setTotalUrlsFound(10);
        CrawlTaskEntity updatedTask = crawlTaskRepository.save(savedTask);

        // Then
        assertEquals("done", updatedTask.getStatus());
        assertNotNull(updatedTask.getEndTime());
        assertEquals(50, updatedTask.getTotalPagesVisited());
        assertEquals(10, updatedTask.getTotalUrlsFound());
    }

    @Test
    void testDeleteById() {
        // Given
        crawlTaskRepository.save(testTask1);
        assertTrue(crawlTaskRepository.existsById("task001"));

        // When
        crawlTaskRepository.deleteById("task001");

        // Then
        assertFalse(crawlTaskRepository.existsById("task001"));
    }

    @Test
    void testFindAll() {
        // Given
        crawlTaskRepository.saveAll(List.of(testTask1, testTask2, testTask3));

        // When
        List<CrawlTaskEntity> allTasks = crawlTaskRepository.findAll();

        // Then
        assertEquals(3, allTasks.size());
    }

    @Test
    void testCount() {
        // Given
        crawlTaskRepository.saveAll(List.of(testTask1, testTask2));

        // When
        long count = crawlTaskRepository.count();

        // Then
        assertEquals(2, count);
    }

    @Test
    void testExistsById() {
        // Given
        crawlTaskRepository.save(testTask1);

        // When & Then
        assertTrue(crawlTaskRepository.existsById("task001"));
        assertFalse(crawlTaskRepository.existsById("nonexistent"));
    }
}
