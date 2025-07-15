package com.mulato.api.service;

import com.mulato.api.model.CrawlTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

@Service
public class WebCrawlerService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebCrawlerService.class);
    
    @Value("${app.crawler.timeout:30000}")
    private int timeout;
    
    @Value("${app.crawler.user-agent:Web Crawler 1.0}")
    private String userAgent;
    
    @Value("${app.crawler.delay:100}")
    private int delay;
    
    @Value("${app.crawler.max-pages:1000}")
    private int maxPages;
    
    @Autowired
    private CrawlPersistenceService crawlPersistenceService;
    
    public void crawlWebsite(CrawlTask task) {
        logger.info("Starting crawl for task: {}", task.getId());
        
        BlockingQueue<String> urlQueue = new LinkedBlockingQueue<>();
        urlQueue.offer(task.getBaseUrl());
        
        int pagesProcessed = 0;
        
        while (!urlQueue.isEmpty() && task.isActive() && pagesProcessed < maxPages) {
            try {
                String url = urlQueue.poll(1, TimeUnit.SECONDS);
                if (url == null) continue;
                
                // Verifica se URL já foi visitada (no banco ou na memória)
                if (task.isUrlVisited(url) || crawlPersistenceService.isUrlVisited(task.getId(), url)) {
                    continue;
                }
                
                task.markUrlAsVisited(url);
                crawlPersistenceService.saveVisitedUrl(task.getId(), url);
                pagesProcessed++;
                
                Document doc = fetchDocument(url);
                if (doc != null) {
                    // Verifica se o conteúdo contém a palavra-chave
                    if (containsKeyword(doc.html(), task.getKeyword())) {
                        task.addFoundUrl(url);
                        crawlPersistenceService.saveFoundUrl(task.getId(), url);
                        logger.info("Found keyword '{}' in URL: {}", task.getKeyword(), url);
                    }
                    
                    // Extrai links para processar
                    Elements links = doc.select("a[href]");
                    for (Element link : links) {
                        String href = link.attr("href");
                        String absoluteUrl = resolveUrl(url, href);
                        
                        if (absoluteUrl != null && 
                            isSameBaseUrl(absoluteUrl, task.getBaseUrl()) &&
                            !task.isUrlVisited(absoluteUrl) &&
                            !crawlPersistenceService.isUrlVisited(task.getId(), absoluteUrl)) {
                            urlQueue.offer(absoluteUrl);
                        }
                    }
                }
                
                // Delay entre requisições
                Thread.sleep(delay);
                
            } catch (InterruptedException e) {
                logger.warn("Crawl task interrupted: {}", task.getId());
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                logger.error("Error processing URL in task {}: {}", task.getId(), e.getMessage());
            }
        }
        
        task.setActive(false);
        logger.info("Crawl completed for task: {} - Pages processed: {}, URLs found: {}", 
                   task.getId(), pagesProcessed, task.getFoundUrls().size());
    }
    
    private Document fetchDocument(String url) {
        try {
            return Jsoup.connect(url)
                    .timeout(timeout)
                    .userAgent(userAgent)
                    .get();
        } catch (IOException e) {
            logger.warn("Failed to fetch URL: {} - {}", url, e.getMessage());
            return null;
        }
    }
    
    private boolean containsKeyword(String content, String keyword) {
        return content.toLowerCase().contains(keyword.toLowerCase());
    }
    
    private String resolveUrl(String baseUrl, String href) {
        try {
            URI base = new URI(baseUrl);
            URI resolved = base.resolve(href);
            return resolved.toString();
        } catch (URISyntaxException e) {
            logger.warn("Failed to resolve URL: {} with href: {}", baseUrl, href);
            return null;
        }
    }
    
    private boolean isSameBaseUrl(String url, String baseUrl) {
        try {
            URI uri = new URI(url);
            URI baseUri = new URI(baseUrl);
            
            String uriHost = uri.getHost();
            String baseUriHost = baseUri.getHost();
            String uriScheme = uri.getScheme();
            String baseUriScheme = baseUri.getScheme();
            
            // Se qualquer host ou scheme for null, as URLs não são da mesma base
            if (uriHost == null || baseUriHost == null || uriScheme == null || baseUriScheme == null) {
                return false;
            }
            
            // Verifica se os hosts são iguais
            if (!uriHost.equals(baseUriHost)) {
                return false;
            }
            
            // Verifica se os schemes são iguais
            if (!uriScheme.equals(baseUriScheme)) {
                return false;
            }
            
            // Verifica as portas, considerando valores padrão (80 para HTTP, 443 para HTTPS)
            int uriPort = uri.getPort() == -1 ? getDefaultPort(uriScheme) : uri.getPort();
            int baseUriPort = baseUri.getPort() == -1 ? getDefaultPort(baseUriScheme) : baseUri.getPort();
            
            return uriPort == baseUriPort;
        } catch (URISyntaxException e) {
            return false;
        }
    }
    
    /**
     * Retorna a porta padrão para um protocolo específico
     * @param scheme O protocolo (http ou https)
     * @return Porta padrão (80 para http, 443 para https)
     */
    private int getDefaultPort(String scheme) {
        return "https".equalsIgnoreCase(scheme) ? 443 : 80;
    }
}
