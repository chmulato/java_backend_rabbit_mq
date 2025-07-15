package com.mulato.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Resultado de uma busca de crawling")
public class CrawlResult {
    
    @JsonProperty("id")
    @Schema(description = "ID único da tarefa de crawling", example = "abc12345")
    private String id;
    
    @JsonProperty("status")
    @Schema(
            description = "Status atual da busca", 
            example = "active",
            allowableValues = {"active", "done"}
    )
    private String status;
    
    @JsonProperty("urls")
    @Schema(
            description = "Lista de URLs onde a palavra-chave foi encontrada",
            example = "[\"http://example.com/page1.html\", \"http://example.com/page2.html\"]"
    )
    private List<String> urls;
    
    public CrawlResult() {}
    
    public CrawlResult(String id, String status, List<String> urls) {
        this.id = id;
        this.status = status;
        this.urls = urls;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public List<String> getUrls() {
        return urls;
    }
    
    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
    
    @Override
    public String toString() {
        return "CrawlResult{" +
                "id='" + id + '\'' +
                ", status='" + status + '\'' +
                ", urls=" + urls +
                '}';
    }
}
