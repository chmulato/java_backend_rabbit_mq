package com.mulato.axur.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuração do OpenAPI/Swagger para documentação da API
 */
@Configuration
public class OpenApiConfig {

    @Value("${app.base-url:http://localhost:4567}")
    private String baseUrl;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Web Crawler API - Axur")
                        .description("API REST para web crawling e busca de termos específicos em websites. " +
                                "\n\n**Desenvolvedor:** Christian Vladimir Uhdre Mulato\n" +
                                "**Empresa:** Axur\n" +
                                "**Teste Técnico:** Desenvolvedor Java Sênior\n\n" +
                                "## Funcionalidades\n\n" +
                                "- ✅ **Web Crawling**: Busca termos em websites de forma assíncrona\n" +
                                "- ✅ **Processamento Assíncrono**: Utiliza RabbitMQ para background processing\n" +
                                "- ✅ **Persistência**: Armazena resultados em banco H2 com Flyway\n" +
                                "- ✅ **Monitoramento**: Endpoints Actuator para observabilidade\n" +
                                "- ✅ **Containerização**: Docker e Docker Compose ready\n\n" +
                                "## Como usar\n\n" +
                                "1. **Iniciar busca**: `POST /crawl` com keyword\n" +
                                "2. **Consultar resultados**: `GET /crawl/{id}`\n" +
                                "3. **Ver estatísticas**: `GET /crawl/{id}/stats`")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Christian Vladimir Uhdre Mulato")
                                .email("christian.mulato@example.com")
                                .url("https://github.com/christian-mulato"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:4567")
                                .description("Ambiente de Desenvolvimento"),
                        new Server()
                                .url(baseUrl)
                                .description("Ambiente Configurado")
                ));
    }
}
