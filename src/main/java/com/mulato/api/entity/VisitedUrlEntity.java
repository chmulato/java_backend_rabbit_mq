package com.mulato.api.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "visited_urls")
public class VisitedUrlEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "task_id", nullable = false, length = 8)
    private String taskId;
    
    @Column(nullable = false, length = 1000)
    private String url;
    
    @Column(name = "visited_at", nullable = false)
    private LocalDateTime visitedAt;
    
    public VisitedUrlEntity() {}
    
    public VisitedUrlEntity(String taskId, String url) {
        this.taskId = taskId;
        this.url = url;
        this.visitedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getTaskId() {
        return taskId;
    }
    
    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public LocalDateTime getVisitedAt() {
        return visitedAt;
    }
    
    public void setVisitedAt(LocalDateTime visitedAt) {
        this.visitedAt = visitedAt;
    }
}
