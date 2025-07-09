package com.mulato.axur.listener;

import com.mulato.axur.config.RabbitConfig;
import com.mulato.axur.model.CrawlTask;
import com.mulato.axur.service.CrawlService;
import com.mulato.axur.service.WebCrawlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CrawlTaskListener {
    
    private static final Logger logger = LoggerFactory.getLogger(CrawlTaskListener.class);
    
    @Autowired
    private WebCrawlerService webCrawlerService;
    
    @Autowired
    private CrawlService crawlService;
    
    @RabbitListener(queues = RabbitConfig.CRAWL_QUEUE)
    public void processCrawlTask(CrawlTask task) {
        logger.info("Received crawl task from queue: {}", task.getId());
        
        try {
            webCrawlerService.crawlWebsite(task);
            crawlService.finishCrawlTask(task.getId());
            logger.info("Crawl task completed successfully: {}", task.getId());
        } catch (Exception e) {
            logger.error("Error processing crawl task: {}", task.getId(), e);
            task.setActive(false);
            crawlService.finishCrawlTask(task.getId());
        }
    }
}
