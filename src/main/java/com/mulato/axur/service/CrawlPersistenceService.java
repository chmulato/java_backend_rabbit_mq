package com.mulato.axur.service;

import com.mulato.axur.entity.CrawlResultEntity;
import com.mulato.axur.entity.VisitedUrlEntity;
import com.mulato.axur.repository.CrawlResultRepository;
import com.mulato.axur.repository.VisitedUrlRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CrawlPersistenceService {
    
    private static final Logger logger = LoggerFactory.getLogger(CrawlPersistenceService.class);
    
    @Autowired
    private CrawlResultRepository crawlResultRepository;
    
    @Autowired
    private VisitedUrlRepository visitedUrlRepository;
    
    @Transactional
    public void saveFoundUrl(String taskId, String url) {
        try {
            CrawlResultEntity result = new CrawlResultEntity(taskId, url);
            crawlResultRepository.save(result);
            logger.debug("Saved found URL for task {}: {}", taskId, url);
        } catch (Exception e) {
            logger.error("Error saving found URL for task {}: {}", taskId, url, e);
        }
    }
    
    @Transactional
    public void saveVisitedUrl(String taskId, String url) {
        try {
            if (!visitedUrlRepository.existsByTaskIdAndUrl(taskId, url)) {
                VisitedUrlEntity visited = new VisitedUrlEntity(taskId, url);
                visitedUrlRepository.save(visited);
                logger.debug("Saved visited URL for task {}: {}", taskId, url);
            }
        } catch (Exception e) {
            logger.error("Error saving visited URL for task {}: {}", taskId, url, e);
        }
    }
    
    @Transactional(readOnly = true)
    public boolean isUrlVisited(String taskId, String url) {
        return visitedUrlRepository.existsByTaskIdAndUrl(taskId, url);
    }
    
    @Transactional(readOnly = true)
    public List<String> getFoundUrls(String taskId) {
        return crawlResultRepository.findUrlsByTaskId(taskId);
    }
    
    @Transactional(readOnly = true)
    public List<String> getVisitedUrls(String taskId) {
        return visitedUrlRepository.findUrlsByTaskId(taskId);
    }
    
    @Transactional(readOnly = true)
    public long getFoundUrlsCount(String taskId) {
        return crawlResultRepository.countByTaskId(taskId);
    }
    
    @Transactional(readOnly = true)
    public long getVisitedUrlsCount(String taskId) {
        return visitedUrlRepository.countByTaskId(taskId);
    }
}
