package com.mulato.axur.repository;

import com.mulato.axur.entity.VisitedUrlEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VisitedUrlRepository extends JpaRepository<VisitedUrlEntity, Long> {
    
    List<VisitedUrlEntity> findByTaskId(String taskId);
    
    boolean existsByTaskIdAndUrl(String taskId, String url);
    
    @Query("SELECT v.url FROM VisitedUrlEntity v WHERE v.taskId = :taskId")
    List<String> findUrlsByTaskId(@Param("taskId") String taskId);
    
    Long countByTaskId(String taskId);
    
    void deleteByTaskId(String taskId);
}
