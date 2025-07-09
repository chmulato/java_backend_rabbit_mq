package com.mulato.axur.service;

import com.mulato.axur.config.RabbitConfig;
import com.mulato.axur.model.CrawlTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CrawlMessageService {
    
    private static final Logger logger = LoggerFactory.getLogger(CrawlMessageService.class);
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    public void sendCrawlMessage(CrawlTask task) {
        try {
            logger.info("Sending crawl task to queue: {}", task.getId());
            rabbitTemplate.convertAndSend(
                RabbitConfig.CRAWL_EXCHANGE,
                RabbitConfig.CRAWL_ROUTING_KEY,
                task
            );
            logger.info("Crawl task sent successfully: {}", task.getId());
        } catch (Exception e) {
            logger.error("Error sending crawl task to queue: {}", task.getId(), e);
            throw new RuntimeException("Failed to send crawl task to queue", e);
        }
    }
}
