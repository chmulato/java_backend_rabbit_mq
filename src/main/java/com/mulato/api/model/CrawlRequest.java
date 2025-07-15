package com.mulato.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "Requisição para iniciar uma nova busca de crawling")
public class CrawlRequest {
    
    @JsonProperty("keyword")
    @Schema(
            description = "Palavra-chave a ser buscada no conteúdo das páginas web",
            example = "security",
            minLength = 4,
            maxLength = 32,
            required = true
    )
    @NotBlank(message = "Keyword cannot be blank")
    @Size(min = 4, max = 32, message = "Keyword must be between 4 and 32 characters")
    private String keyword;
    
    public CrawlRequest() {}
    
    public CrawlRequest(String keyword) {
        this.keyword = keyword;
    }
    
    public String getKeyword() {
        return keyword;
    }
    
    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
    
    @Override
    public String toString() {
        return "CrawlRequest{" +
                "keyword='" + keyword + '\'' +
                '}';
    }
}
