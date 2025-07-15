package com.mulato.api.repository;

import com.mulato.api.entity.CrawlResultEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CrawlResultRepository extends JpaRepository<CrawlResultEntity, Long> {
    
    List<CrawlResultEntity> findByTaskId(String taskId);
    
    @Query("SELECT r.url FROM CrawlResultEntity r WHERE r.taskId = :taskId ORDER BY r.foundAt")
    List<String> findUrlsByTaskId(@Param("taskId") String taskId);
    
    Long countByTaskId(String taskId);
    
    void deleteByTaskId(String taskId);
}
