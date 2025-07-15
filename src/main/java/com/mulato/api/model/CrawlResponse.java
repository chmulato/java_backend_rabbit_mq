package com.mulato.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta ao iniciar uma nova busca de crawling")
public class CrawlResponse {
    
    @JsonProperty("id")
    @Schema(
            description = "ID único da tarefa de crawling gerado automaticamente",
            example = "abc12345",
            minLength = 8,
            maxLength = 8
    )
    private String id;
    
    public CrawlResponse() {}
    
    public CrawlResponse(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    @Override
    public String toString() {
        return "CrawlResponse{" +
                "id='" + id + '\'' +
                '}';
    }
}
