package com.mulato.api.service;

import com.mulato.api.entity.CrawlTaskEntity;
import com.mulato.api.model.CrawlResult;
import com.mulato.api.model.CrawlTask;
import com.mulato.api.repository.CrawlResultRepository;
import com.mulato.api.repository.CrawlTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class CrawlService {
    
    @Value("${app.base-url}")
    private String baseUrl;
    
    @Autowired
    private CrawlMessageService crawlMessageService;
    
    @Autowired
    private IdGeneratorService idGeneratorService;
    
    @Autowired
    private CrawlTaskRepository crawlTaskRepository;
    
    @Autowired
    private CrawlResultRepository crawlResultRepository;
    
    // Cache em memória para tarefas ativas
    private final ConcurrentMap<String, CrawlTask> activeTasks = new ConcurrentHashMap<>();
    
    @Transactional
    public String startCrawl(String keyword) {
        String crawlId = idGeneratorService.generateId();
        
        // Salva no banco de dados
        CrawlTaskEntity entity = new CrawlTaskEntity(crawlId, keyword, baseUrl);
        crawlTaskRepository.save(entity);
        
        // Cria task para processamento
        CrawlTask task = new CrawlTask(crawlId, keyword, baseUrl);
        activeTasks.put(crawlId, task);
        
        // Envia mensagem para iniciar o crawling
        crawlMessageService.sendCrawlMessage(task);
        
        return crawlId;
    }
    
    @Transactional(readOnly = true)
    public CrawlResult getCrawlResult(String crawlId) {
        // Primeiro verifica se está nas tarefas ativas
        CrawlTask activeTask = activeTasks.get(crawlId);
        if (activeTask != null) {
            return new CrawlResult(
                activeTask.getId(),
                activeTask.getStatus(),
                activeTask.getFoundUrls()
            );
        }
        
        // Senão, busca no banco de dados
        Optional<CrawlTaskEntity> taskEntity = crawlTaskRepository.findById(crawlId);
        if (taskEntity.isPresent()) {
            List<String> urls = crawlResultRepository.findUrlsByTaskId(crawlId);
            return new CrawlResult(
                taskEntity.get().getId(),
                taskEntity.get().getStatus(),
                urls
            );
        }
        
        return null;
    }
    
    public CrawlTask getActiveCrawlTask(String crawlId) {
        return activeTasks.get(crawlId);
    }
    
    @Transactional
    public void finishCrawlTask(String crawlId) {
        CrawlTask task = activeTasks.remove(crawlId);
        if (task != null) {
            // Atualiza status no banco
            Optional<CrawlTaskEntity> entityOpt = crawlTaskRepository.findById(crawlId);
            if (entityOpt.isPresent()) {
                CrawlTaskEntity entity = entityOpt.get();
                entity.setStatus("done");
                entity.setEndTime(LocalDateTime.now());
                entity.setTotalPagesVisited(task.getVisitedUrls().size());
                entity.setTotalUrlsFound(task.getFoundUrls().size());
                crawlTaskRepository.save(entity);
            }
        }
    }
    
    @Transactional(readOnly = true)
    public List<CrawlTaskEntity> getActiveTasks() {
        return crawlTaskRepository.findByStatus("active");
    }
}
