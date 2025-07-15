package com.mulato.api.model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class CrawlTask {
    
    private final String id;
    private final String keyword;
    private final String baseUrl;
    private final LocalDateTime startTime;
    private final ConcurrentHashMap<String, Boolean> visitedUrls = new ConcurrentHashMap<>();
    private final CopyOnWriteArrayList<String> foundUrls = new CopyOnWriteArrayList<>();
    private volatile boolean active = true;
    
    public CrawlTask(String id, String keyword, String baseUrl) {
        this.id = id;
        this.keyword = keyword;
        this.baseUrl = baseUrl;
        this.startTime = LocalDateTime.now();
    }
    
    public String getId() {
        return id;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public String getBaseUrl() {
        return baseUrl;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public ConcurrentHashMap<String, Boolean> getVisitedUrls() {
        return visitedUrls;
    }
    
    public List<String> getFoundUrls() {
        return foundUrls;
    }
    
    public void addFoundUrl(String url) {
        if (!foundUrls.contains(url)) {
            foundUrls.add(url);
        }
    }
    
    public void markUrlAsVisited(String url) {
        visitedUrls.put(url, true);
    }
    
    public boolean isUrlVisited(String url) {
        return visitedUrls.containsKey(url);
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void setActive(boolean active) {
        this.active = active;
    }
    
    public String getStatus() {
        return active ? "active" : "done";
    }
    
    @Override
    public String toString() {
        return "CrawlTask{" +
                "id='" + id + '\'' +
                ", keyword='" + keyword + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                ", startTime=" + startTime +
                ", active=" + active +
                ", foundUrls=" + foundUrls.size() +
                '}';
    }
}
