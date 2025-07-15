package com.mulato.api.controller;

import com.mulato.api.entity.CrawlTaskEntity;
import com.mulato.api.model.CrawlRequest;
import com.mulato.api.model.CrawlResponse;
import com.mulato.api.model.CrawlResult;
import com.mulato.api.service.CrawlService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;

/**
 * Controller da API HTTP - Endpoint principal da Web Crawler API
 * 
 * Implementa os endpoints especificados no teste técnico:
 * - 1a. POST /crawl: inicia uma nova busca por um termo (keyword)
 * - 1b. GET /crawl/{id}: consulta resultados de busca
 * 
 * Porta: 4567 (configurada via application.yml)
 * Content-Type: application/json
 * 
 * @author Christian Vladimir Uhdre Mulato
 * @since 2025-07-09
 */
@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "API HTTP", description = "Requisito 1: Endpoints de Web Crawling")
public class ApiHttpController {

    @Autowired
    private CrawlService crawlService;

    /**
     * Requisito 1a: POST /crawl - inicia uma nova busca por um termo (keyword)
     * 
     * Especificação do desafio:
     * POST /crawl HTTP/1.1
     * Host: localhost:4567
     * Content-Type: application/json
     * Body: {"keyword": "security"}
     * 
     * Resposta:
     * 200 OK
     * Content-Type: application/json
     * Body: {"id": "30vbllyb"}
     */
    @PostMapping(value = "/crawl", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "1a. Inicia nova busca por termo",
        description = "Inicia uma nova busca por um termo (keyword) no website configurado via BASE_URL. " +
                     "Retorna um ID único de 8 caracteres alfanuméricos para acompanhar o progresso da busca."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Busca iniciada com sucesso",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CrawlResponse.class),
                examples = @ExampleObject(
                    name = "Exemplo do desafio",
                    value = "{\"id\": \"30vbllyb\"}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "400", 
            description = "Termo inválido (deve ter entre 4-32 caracteres)"
        ),
        @ApiResponse(
            responseCode = "415", 
            description = "Content-Type deve ser application/json"
        )
    })
    public ResponseEntity<CrawlResponse> startCrawl(
        @Parameter(
            description = "Requisição com termo a ser buscado",
            required = true,
            schema = @Schema(implementation = CrawlRequest.class)
        )
        @Valid @RequestBody CrawlRequest request
    ) {
        try {
            String crawlId = crawlService.startCrawl(request.getKeyword());
            CrawlResponse response = new CrawlResponse(crawlId);
            return ResponseEntity.ok()
                .body(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Requisito 1b: GET /crawl/{id} - consulta resultados de busca
     * 
     * Especificação do desafio:
     * GET /crawl/30vbllyb HTTP/1.1
     * Host: localhost:4567
     * 
     * Resposta:
     * 200 OK
     * Content-Type: application/json
     * {
     *   "id": "30vbllyb",
     *   "status": "active",
     *   "urls": [
     *     "http://hiring.axreng.com/index2.html",
     *     "http://hiring.axreng.com/htmlman1/chcon.1.html"
     *   ]
     * }
     */
    @GetMapping("/crawl/{id}")
    @Operation(
        summary = "1b. Consulta resultados de busca",
        description = "Retorna os resultados de uma busca específica pelo seu ID. " +
                     "Status pode ser 'active' (em andamento) ou 'done' (concluída). " +
                     "Resultados parciais são retornados durante o processamento."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Resultados da busca encontrados",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CrawlResult.class),
                examples = {
                    @ExampleObject(
                        name = "Busca ativa (exemplo do desafio)",
                        value = "{\"id\": \"30vbllyb\", \"status\": \"active\", \"urls\": [\"http://hiring.axreng.com/index2.html\", \"http://hiring.axreng.com/htmlman1/chcon.1.html\"]}"
                    ),
                    @ExampleObject(
                        name = "Busca concluída",
                        value = "{\"id\": \"abcd1234\", \"status\": \"done\", \"urls\": [\"http://example.com/page1.html\", \"http://example.com/page2.html\"]}"
                    )
                }
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "ID da busca não encontrado"
        )
    })
    public ResponseEntity<CrawlResult> getCrawlResult(
        @Parameter(
            description = "ID único da busca (8 caracteres alfanuméricos)",
            required = true,
            example = "30vbllyb"
        )
        @PathVariable String id
    ) {
        CrawlResult result = crawlService.getCrawlResult(id);
        
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * Endpoint adicional: GET /crawl/{id}/stats - estatísticas detalhadas da busca
     * Extensão útil para monitoramento e debugging.
     */
    @GetMapping("/crawl/{id}/stats")
    @Operation(
        summary = "Estatísticas detalhadas da busca",
        description = "Retorna estatísticas completas de uma tarefa de crawling específica, " +
                     "incluindo informações de progresso e métricas detalhadas."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Estatísticas encontradas",
            content = @Content(
                mediaType = "application/json",
                examples = @ExampleObject(
                    name = "Estatísticas detalhadas",
                    value = "{\"id\": \"30vbllyb\", \"keyword\": \"security\", \"status\": \"active\", \"total_urls_found\": 15, \"total_pages_processed\": 50}"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404", 
            description = "ID da busca não encontrado"
        )
    })
    public ResponseEntity<CrawlResult> getCrawlStats(
        @Parameter(
            description = "ID único da busca (8 caracteres alfanuméricos)",
            required = true,
            example = "30vbllyb"
        )
        @PathVariable String id
    ) {
        CrawlResult result = crawlService.getCrawlResult(id);
        
        if (result == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok()
            .body(result);
    }

    /**
     * Endpoint adicional: GET /crawl/active - lista tarefas ativas
     * Útil para monitoramento de buscas em andamento.
     */
    @GetMapping("/crawl/active")
    @Operation(
        summary = "Listar buscas ativas",
        description = "Retorna uma lista de todas as buscas que estão atualmente em execução (status 'active')."
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Lista de buscas ativas",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CrawlTaskEntity.class)
            )
        )
    })
    public ResponseEntity<List<CrawlTaskEntity>> getActiveTasks() {
        List<CrawlTaskEntity> activeTasks = crawlService.getActiveTasks();
        return ResponseEntity.ok()
            .body(activeTasks);
    }

    /**
     * Endpoint adicional para validação de conformidade com o desafio.
     * Retorna informações sobre a implementação dos requisitos 1a e 1b.
     */
    @GetMapping("/crawl")
    @Operation(
        summary = "Informações da API",
        description = "Retorna informações sobre os endpoints disponíveis da API HTTP"
    )
    public ResponseEntity<Object> getApiInfo() {
        return ResponseEntity.ok()
            .body(java.util.Map.of(
                "requisito", "1. API HTTP",
                "endpoints", java.util.Map.of(
                    "1a", "POST /crawl - inicia nova busca por termo",
                    "1b", "GET /crawl/{id} - consulta resultados de busca"
                ),
                "porta", 4567,
                "contentType", "application/json",
                "status", "Implementado conforme especificação do desafio"
            ));
    }
}
