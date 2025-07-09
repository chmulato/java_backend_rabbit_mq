package com.mulato.axur.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "crawl_tasks")
public class CrawlTaskEntity {
    
    @Id
    private String id;
    
    @Column(nullable = false, length = 32)
    private String keyword;
    
    @Column(name = "base_url", nullable = false, length = 500)
    private String baseUrl;
    
    @Column(nullable = false, length = 20)
    private String status;
    
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "total_pages_visited")
    private Integer totalPagesVisited = 0;
    
    @Column(name = "total_urls_found")
    private Integer totalUrlsFound = 0;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "taskId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CrawlResultEntity> results;
    
    @OneToMany(mappedBy = "taskId", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<VisitedUrlEntity> visitedUrls;
    
    public CrawlTaskEntity() {}
    
    public CrawlTaskEntity(String id, String keyword, String baseUrl) {
        this.id = id;
        this.keyword = keyword;
        this.baseUrl = baseUrl;
        this.status = "active";
        this.startTime = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public LocalDateTime getEndTime() {
        return endTime;
    }
    
    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
    
    public Integer getTotalPagesVisited() {
        return totalPagesVisited;
    }
    
    public void setTotalPagesVisited(Integer totalPagesVisited) {
        this.totalPagesVisited = totalPagesVisited;
    }
    
    public Integer getTotalUrlsFound() {
        return totalUrlsFound;
    }
    
    public void setTotalUrlsFound(Integer totalUrlsFound) {
        this.totalUrlsFound = totalUrlsFound;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<CrawlResultEntity> getResults() {
        return results;
    }
    
    public void setResults(List<CrawlResultEntity> results) {
        this.results = results;
    }
    
    public List<VisitedUrlEntity> getVisitedUrls() {
        return visitedUrls;
    }
    
    public void setVisitedUrls(List<VisitedUrlEntity> visitedUrls) {
        this.visitedUrls = visitedUrls;
    }
}
