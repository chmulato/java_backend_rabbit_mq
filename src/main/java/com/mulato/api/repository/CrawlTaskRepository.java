package com.mulato.api.repository;

import com.mulato.api.entity.CrawlTaskEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CrawlTaskRepository extends JpaRepository<CrawlTaskEntity, String> {
    
    List<CrawlTaskEntity> findByStatus(String status);
    
    List<CrawlTaskEntity> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM CrawlTaskEntity t WHERE t.status = 'active' AND t.startTime < :timeout")
    List<CrawlTaskEntity> findStaleActiveTasks(@Param("timeout") LocalDateTime timeout);
}
