# Especificações Técnicas

## Web Crawler API

Este documento contém as especificações técnicas detalhadas da Web Crawler API, incluindo requisitos funcionais, requisitos não-funcionais, interfaces externas, dependências tecnológicas e detalhes de implementação.

## Requisitos do Sistema

### Requisitos Mínimos de Hardware

- **CPU**: 2 cores
- **Memória RAM**: 2GB
- **Armazenamento**: 1GB

### Ambiente de Execução

- **Sistema Operacional**: Linux, Windows ou macOS
- **JDK**: Java 17 ou superior
- **Container**: Docker 20.10+ e Docker Compose 2.0+

### Infraestrutura de Rede

- **Portas Expostas**:
  - 4567: API HTTP
  - 5672: RabbitMQ AMQP
  - 15672: RabbitMQ Management Console
- **Requisitos de Rede**: Acesso à internet para crawling de sites externos

## Stack Tecnológica

### Backend

| Componente          | Tecnologia                | Versão  | Descrição                             |
|---------------------|---------------------------|---------|---------------------------------------|
| Linguagem           | Java                      | 17      | Plataforma de programação principal   |
| Framework Web       | Spring Boot               | 3.4.1   | Framework para desenvolvimento web    |
| ORM                 | Spring Data JPA/Hibernate | 6.2.0   | Framework ORM para persistência       |
| Message Broker      | RabbitMQ                  | 3.12.0  | Broker de mensagens para async        |
| Banco de Dados      | H2 Database               | 2.2.224 | Banco de dados SQL em memória         |
| HTTP Client         | HttpClient                | 4.5.14  | Cliente HTTP para requisições externas|
| HTML Parser         | JSoup                     | 1.16.2  | Parser HTML para extração de conteúdo |
| Migration           | Flyway                    | 9.19.1  | Ferramenta de migração de banco       |
| Documentação API    | SpringDoc OpenAPI         | 2.3.0   | Documentação automática de API        |
| Mapeamento Objetos  | Jackson                   | 2.15.2  | Serialização/Desserialização JSON     |
| Validação           | Hibernate Validator       | 8.0.1   | Validação de beans e parâmetros       |
| Segurança           | Spring Security           | 6.1.2   | Framework de segurança                |
| Logging             | SLF4J + Logback           | 2.0.9   | Framework de logging                  |
| Monitoramento       | Spring Actuator           | 3.4.1   | Health check e métricas               |

### Ferramentas de Build e Deploy

| Componente        | Tecnologia          | Versão  | Descrição                         |
|-------------------|---------------------|---------|-----------------------------------|
| Build Tool        | Maven               | 3.9.5   | Gerenciador de dependências       |
| Container         | Docker              | 24.0.5  | Containerização da aplicação      |
| Orquestração      | Docker Compose      | 2.20.2  | Orquestração de containers        |
| CI/CD             | GitHub Actions      | N/A     | Automação de build e deploy       |
| Code Coverage     | JaCoCo              | 0.8.10  | Análise de cobertura de código    |

### Ferramentas de Teste

| Componente        | Tecnologia        | Versão | Descrição                         |
|-------------------|-------------------|--------|-----------------------------------|
| Framework de Teste| JUnit Jupiter     | 5.10.0 | Framework de testes unitários     |
| Mock Framework    | Mockito           | 5.4.0  | Framework para mockar componentes |
| Test Containers   | Testcontainers    | 1.18.3 | Containers para testes integrados |
| Assertion Library | AssertJ           | 3.24.2 | Biblioteca para assertivas        |
| Web Test          | Spring Test       | 6.0.11 | Testes de componentes web         |
| REST Assured      | REST Assured      | 5.3.1  | Testes de API REST                |

## Detalhes da API

### Endpoints

| Método | URL             | Descrição                      | Request Body            | Response                            |
|--------|-----------------|--------------------------------|-------------------------|-------------------------------------|
| POST   | `/crawl`        | Iniciar nova busca por termo   | `{"keyword": "termo"}`  | `{"id": "abcd1234"}`               |
| GET    | `/crawl/{id}`   | Consultar resultados da busca  | N/A                     | `{"id": "...", "urls": [...], "status": "..."}` |
| GET    | `/health`       | Verificar status da aplicação  | N/A                     | `{"status": "UP"}`                  |
| GET    | `/swagger-ui.html` | Documentação Swagger        | N/A                     | UI HTML                             |
| GET    | `/api-docs`     | Documentação OpenAPI JSON      | N/A                     | JSON Schema OpenAPI                 |

### Modelos de Dados

#### CrawlRequest
```json
{
  "keyword": "string (min: 4, max: 32)"
}
```

#### CrawlResponse
```json
{
  "id": "string (8 caracteres alfanuméricos)"
}
```

#### CrawlResult
```json
{
  "id": "string",
  "status": "string (active|done)",
  "urls": [
    "string (URLs encontrados)"
  ]
}
```

### Códigos de Status

| Código | Descrição                                 | Situação                                 |
|--------|-----------------------------------------|------------------------------------------|
| 200    | OK                                      | Requisição processada com sucesso        |
| 400    | Bad Request                             | Erro de validação no termo de busca      |
| 404    | Not Found                               | ID de busca não encontrado              |
| 415    | Unsupported Media Type                  | Content-Type inválido                    |
| 500    | Internal Server Error                   | Erro interno do servidor                 |

## Modelo de Banco de Dados

### Entidades

#### CrawlTaskEntity

| Campo         | Tipo       | Descrição                         | Restrições              |
|---------------|------------|----------------------------------|-------------------------|
| id            | String     | Identificador único da tarefa     | PK, 8 chars alfanum.    |
| keyword       | String     | Termo de busca                    | NotNull, 4-32 chars     |
| baseUrl       | String     | URL base para crawling            | NotNull, URL válida     |
| status        | Enum       | Status da tarefa (ACTIVE, DONE)   | NotNull                 |
| createdAt     | Timestamp  | Data/hora de criação              | NotNull, Auto          |
| updatedAt     | Timestamp  | Data/hora de atualização          | NotNull, Auto          |

#### CrawlResultEntity

| Campo         | Tipo       | Descrição                         | Restrições              |
|---------------|------------|----------------------------------|-------------------------|
| id            | Long       | Identificador único do resultado  | PK, Auto-increment      |
| taskId        | String     | ID da tarefa relacionada          | FK -> CrawlTaskEntity   |
| url           | String     | URL encontrada                    | NotNull, URL válida     |
| foundAt       | Timestamp  | Data/hora de descoberta           | NotNull, Auto          |

#### VisitedUrlEntity

| Campo         | Tipo       | Descrição                         | Restrições              |
|---------------|------------|----------------------------------|-------------------------|
| id            | Long       | Identificador único               | PK, Auto-increment      |
| url           | String     | URL visitada                      | NotNull, URL válida, Unique |
| visitedAt     | Timestamp  | Data/hora da visita               | NotNull, Auto          |
| httpStatus    | Integer    | Código HTTP retornado             | Nullable                |

### Diagramas ER

```sql
CREATE TABLE crawl_task (
    id VARCHAR(8) PRIMARY KEY,
    keyword VARCHAR(32) NOT NULL,
    base_url VARCHAR(255) NOT NULL,
    status VARCHAR(10) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

CREATE TABLE crawl_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(8) NOT NULL,
    url VARCHAR(2048) NOT NULL,
    found_at TIMESTAMP NOT NULL,
    FOREIGN KEY (task_id) REFERENCES crawl_task(id)
);

CREATE TABLE visited_url (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    url VARCHAR(2048) NOT NULL UNIQUE,
    visited_at TIMESTAMP NOT NULL,
    http_status INT
);

CREATE INDEX idx_crawl_result_task_id ON crawl_result(task_id);
CREATE INDEX idx_visited_url ON visited_url(url);
```

## Filas e Mensageria

### Configuração RabbitMQ

| Recurso         | Nome                | Tipo      | Características                      |
|-----------------|---------------------|-----------|-------------------------------------|
| Exchange        | crawler.exchange    | direct    | durable=true, auto-delete=false     |
| Queue           | crawler.tasks       | queue     | durable=true, exclusive=false       |
| Dead Letter     | crawler.dead-letter | queue     | durable=true, ttl=24h               |
| Binding         | crawler.tasks       | binding   | routing-key=task                    |

### Formato de Mensagens

#### Task Message
```json
{
  "taskId": "abcd1234",
  "keyword": "termo",
  "baseUrl": "http://example.com",
  "timestamp": "2025-07-15T10:30:00Z"
}
```

## Configurações

### Parâmetros de Configuração

| Parâmetro                    | Descrição                            | Padrão                | Observações                    |
|------------------------------|--------------------------------------|----------------------|--------------------------------|
| `server.port`                | Porta da API HTTP                    | 4567                 | Configurável                   |
| `spring.rabbitmq.host`       | Host do RabbitMQ                     | localhost            | Em Docker: rabbitmq            |
| `spring.rabbitmq.port`       | Porta do RabbitMQ                    | 5672                 | AMQP                           |
| `spring.rabbitmq.username`   | Usuário do RabbitMQ                  | guest                | Produção: alterar              |
| `spring.rabbitmq.password`   | Senha do RabbitMQ                    | guest                | Produção: alterar              |
| `spring.datasource.url`      | URL do banco de dados                | jdbc:h2:mem:crawler  | Modo em memória                |
| `crawler.base-url`           | URL base para crawling               | http://example.com   | Configurável                   |
| `crawler.max-depth`          | Profundidade máxima de crawling      | 3                    | Configurável                   |
| `crawler.max-urls`           | Número máximo de URLs por busca      | 100                  | Configurável                   |
| `crawler.threads`            | Threads para crawling paralelo       | 10                   | Configurável                   |
| `crawler.timeout`            | Timeout para requisições HTTP (ms)   | 5000                 | Configurável                   |

### Perfis de Execução

| Perfil          | Descrição                                  | Configurações Específicas                    |
|-----------------|--------------------------------------------|--------------------------------------------|
| `default`       | Perfil de desenvolvimento local            | H2 em memória, log detalhado                |
| `docker`        | Perfil para execução em Docker             | URLs adaptadas para Docker network          |
| `test`          | Perfil para execução de testes             | Database em memória, sem dependências externas |
| `prod`          | Perfil de produção                         | Logs otimizados, métricas ativadas          |

## Requisitos de Performance

### Métricas de Performance

| Métrica                        | Objetivo                         | Medição                           |
|--------------------------------|----------------------------------|-----------------------------------|
| Tempo médio de resposta API    | < 200ms (p95)                    | Spring Actuator metrics           |
| Tempo de processamento         | < 2min para 100 URLs             | Logs + métricas                   |
| Taxa de requisições            | > 100 req/s                      | Load testing (JMeter)             |
| Uso de memória                 | < 512MB                          | JVM metrics                        |
| Uso de CPU                     | < 50% (4 cores)                  | Container metrics                  |

### Limites e Throttling

| Recurso                        | Limite                           | Razão                             |
|--------------------------------|----------------------------------|-----------------------------------|
| Conexões HTTP concorrentes     | 100                              | Evitar sobrecarga                 |
| Rate limit por IP              | 10 req/s                         | Prevenir abuso                    |
| Tamanho máximo do crawler      | 100 URLs por busca               | Limitar recursos                  |
| Tempo máximo de processamento  | 5 minutos                        | SLA                               |
| Retry em falhas                | 3 tentativas, backoff exponencial| Resiliência                       |

## Requisitos de Segurança

### Medidas de Segurança

| Medida                         | Implementação                     | Objetivo                          |
|--------------------------------|----------------------------------|-----------------------------------|
| Validação de entrada           | Bean Validation                  | Prevenir injeção/XSS              |
| Rate limiting                  | Bucket4j                         | Prevenir DDoS                     |
| CORS                           | Spring Security                  | Controle de acesso cross-origin   |
| Sanitização de saída           | OWASP HTML Sanitizer             | Prevenir XSS                      |
| Logs de auditoria              | AOP + SLF4J                      | Rastreabilidade                   |
| Crawler Policy                 | robots.txt + user-agent          | Respeito a políticas de sites     |
| Configurações seguras          | HTTPS, headers de segurança      | Prevenir ataques comuns           |

## Esquemas de Compilação e Build

### Ciclo de Build

1. **Compilação**: `mvn compile`
2. **Testes**: `mvn test`
3. **Empacotamento**: `mvn package`
4. **Verificação**: `mvn verify`
5. **Construção da imagem Docker**: `docker build .`

### Estrutura do Dockerfile

```dockerfile
# Multi-stage build
FROM maven:3.9.5-eclipse-temurin-17-alpine AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn package -DskipTests

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY --from=build /app/target/web-crawler-api.jar .
EXPOSE 4567
ENTRYPOINT ["java", "-jar", "web-crawler-api.jar"]
```

## Considerações Operacionais

### Monitoramento

| Métrica               | Endpoint                       | Descrição                           |
|-----------------------|--------------------------------|-------------------------------------|
| Health Check          | `/actuator/health`             | Status geral da aplicação           |
| Métricas JVM          | `/actuator/metrics`            | Métricas de JVM (memória, CPU)      |
| HTTP Metrics          | `/actuator/metrics/http.*`     | Métricas de requisições HTTP        |
| RabbitMQ Health       | `/actuator/health/rabbit`      | Status do RabbitMQ                  |
| Database Health       | `/actuator/health/db`          | Status do banco de dados            |
| Info                  | `/actuator/info`               | Informações da aplicação            |

### Logs

Os logs são estruturados em formato JSON e incluem:

| Campo                | Descrição                                   | Exemplo                           |
|----------------------|---------------------------------------------|-----------------------------------|
| timestamp            | Data/hora do evento                         | 2025-07-15T12:34:56.789Z          |
| level                | Nível de log (INFO, WARN, ERROR)            | INFO                              |
| thread               | Thread que gerou o log                      | http-nio-4567-exec-1              |
| logger               | Nome do logger                              | com.mulato.api.service.CrawlService |
| message              | Mensagem de log                             | "Started crawl task: abcd1234"     |
| exception            | Stacktrace (se houver)                      | "java.lang.Exception: ..."        |
| traceId              | ID de rastreamento                          | 4f8c24e19edf11bd                  |
| spanId               | ID do span (para trace distribuído)         | 4f8c24e19edf11bd                  |

### Backup e Recuperação

| Item                 | Estratégia                                 | Frequência                        |
|----------------------|-------------------------------------------|-----------------------------------|
| Banco de dados       | Snapshot + log de transações              | Diário + log contínuo             |
| Configurações        | Versionado em Git                         | A cada mudança                     |
| Logs                 | Rotação + armazenamento externo           | Rotação diária, retenção 30 dias  |
| Docker images        | Registry com tags de versão               | A cada release                     |

## Detalhes de Implementação

### Algoritmo do Web Crawler

1. **Inicialização**:
   - Receber termo de busca e criar ID único
   - Configurar URL base e profundidade máxima
   - Inicializar conjuntos de URLs visitados e a visitar

2. **Processamento**:
   - Para cada URL na fila de URLs a visitar:
     - Verificar se já foi visitado
     - Verificar se está no mesmo domínio base
     - Fazer requisição HTTP e verificar código de status
     - Parsear HTML com JSoup
     - Buscar termo no conteúdo
     - Se encontrado, adicionar URL aos resultados
     - Extrair novos links e adicionar à fila (respeitando profundidade)
   
3. **Finalização**:
   - Marcar tarefa como concluída quando não houver mais URLs a visitar
   - Atualizar status da tarefa no banco de dados

### Diagrama de Classes Simplificado

```
com.mulato.api
├── controller
│   └── ApiHttpController
├── model
│   ├── CrawlRequest
│   ├── CrawlResponse
│   └── CrawlResult
├── entity
│   ├── CrawlTaskEntity
│   ├── CrawlResultEntity
│   └── VisitedUrlEntity
├── repository
│   ├── CrawlTaskRepository
│   ├── CrawlResultRepository
│   └── VisitedUrlRepository
├── service
│   ├── CrawlService
│   ├── WebCrawlerService
│   ├── CrawlMessageService
│   └── CrawlPersistenceService
├── listener
│   └── CrawlTaskListener
├── config
│   ├── RabbitConfig
│   ├── OpenApiConfig
│   └── WebConfig
└── exception
    └── GlobalExceptionHandler
```

## Processos de Qualidade

### Padrões de Código

| Padrão               | Ferramenta                      | Configuração                        |
|----------------------|---------------------------------|-------------------------------------|
| Code Style           | Google Java Style               | `.editorconfig`                     |
| Análise Estática     | SonarQube                       | Integrado com CI/CD                 |
| Code Coverage        | JaCoCo                          | Mínimo 80% coverage                 |
| Code Review          | Pull Request + GitHub Actions    | Aprovação obrigatória               |

### Testes Automatizados

| Tipo de Teste        | Framework                       | Escopo                              |
|----------------------|---------------------------------|-------------------------------------|
| Unitário             | JUnit 5 + Mockito              | Classes e métodos individuais       |
| Integração           | Spring Test                     | Comunicação entre componentes       |
| API                  | REST Assured + TestContainers   | Endpoints HTTP                      |
| End-to-End           | Selenium WebDriver              | Fluxos completos                    |
| Performance          | JMeter                          | Carga e stress                      |

## Integração e Deploy

### CI/CD Pipeline

```
GitHub Push → GitHub Actions → Build → Test → Package → Docker Build → Registry → Deploy
```

### Ambientes

| Ambiente             | Propósito                                  | Configuração                        |
|----------------------|--------------------------------------------|-------------------------------------|
| Local                | Desenvolvimento individual                 | H2, RabbitMQ local/Docker           |
| Dev                  | Integração contínua                        | H2, RabbitMQ compartilhado          |
| Staging              | Testes pré-produção                        | PostgreSQL, RabbitMQ                |
| Produção             | Ambiente final                             | PostgreSQL, RabbitMQ cluster        |

## Apêndice: Convenções

### Padrões de Nomenclatura

| Item                 | Padrão                                    | Exemplo                            |
|----------------------|-------------------------------------------|-----------------------------------|
| Classes              | PascalCase                                | WebCrawlerService                  |
| Métodos              | camelCase                                 | findCrawlResultById                |
| Variáveis            | camelCase                                 | crawlTask                          |
| Constantes           | UPPER_SNAKE_CASE                          | MAX_URLS_PER_TASK                  |
| Pacotes              | lowercase                                 | com.mulato.api.service             |
| Tabelas              | snake_case                                | crawl_result                       |
| Colunas              | snake_case                                | created_at                         |

### Convenção de Commits

```
<tipo>(<escopo>): <descrição>

<corpo>

<rodapé>
```

Onde:
- **tipo**: feat, fix, docs, style, refactor, test, chore
- **escopo**: controller, service, model, etc.
- **descrição**: resumo conciso da mudança
- **corpo**: detalhes adicionais
- **rodapé**: referências a issues, breaking changes

Exemplo:
```
feat(crawler): implementar limite de profundidade

Adiciona configuração para limitar a profundidade máxima do crawler.
Inclui testes para validar o comportamento.

Resolve: #123
```
