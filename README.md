# Web Crawler API - Desafio Java Axur

**Desenvolvedor:** Christian Vladimir Uhdre Mulato  
**Posição:** Desenvolvedor Java Sênior  
**Empresa:** Axur  

## Descrição

Aplicação Java Spring Boot desenvolvida como parte do teste técnico para a posição de Backend Software Developer na Axur. A aplicação realiza web crawling e busca de termos específicos em websites, oferecendo uma API REST para interação e processamento assíncrono.

## Sobre o Desafio

Este projeto implementa a solução para o teste técnico da Axur, atendendo aos seguintes requisitos:

- **API HTTP** na porta 4567 com endpoints para iniciar busca e consultar resultados
- **Web Crawling** de websites com busca de termos específicos  
- **Processamento assíncrono** utilizando RabbitMQ
- **Persistência** em banco H2 com migrações Flyway
- **Containerização** com Docker e Docker Compose
- **Cobertura de testes** completa para todos os 8 requisitos (100%)
- **Configuração UTF-8** completa para suporte internacional

## ⭐ Destaque: Testes Automatizados Completos

Este projeto possui **cobertura de testes automatizados para 100% dos requisitos** do desafio técnico:

### 🎯 Cobertura por Requisito

| Requisito | Status | Arquivo de Teste |
|-----------|--------|------------------|
| 1. API HTTP | ✅ | `ApiHttpTest.java` |
| 2. Validação do termo | ✅ | `TermValidationTest.java` |
| 3. ID da busca | ✅ | `CrawlIdGenerationTest.java` |
| 4. URL base | ✅ | `BaseUrlConfigurationTest.java` |
| 5. Múltiplas buscas | ✅ | `MultipleCrawlsTest.java` |
| 6. Resultados parciais | ✅ | `PartialResultsTest.java` |
| 7. Estrutura do projeto | ✅ | `ProjectStructureTest.java` |
| 8. Compilação e execução | ✅ | `CompilationExecutionTest.java` |

### 📊 Estatísticas de Testes

- **10 arquivos de teste** criados
- **50+ métodos de teste** implementados  
- **8/8 requisitos cobertos** (100%)
- **Cenários**: Positivos, negativos, edge cases, concorrência, performance
- **Tipos**: Unitários, integração, E2E com massa de dados realista

**📖 Documentação detalhada:** [TESTES-COBERTURA-COMPLETA.md](docs/TESTES-COBERTURA-COMPLETA.md)

## Funcionalidades

### Web Crawling

- Busca case-insensitive em todo o conteúdo HTML
- Segue links relativos e absolutos na mesma base URL
- Controle de URLs visitadas para evitar loops
- Timeout configurável para requisições
- Delay entre requisições para evitar sobrecarga

### Processamento Assíncrono

- Utiliza RabbitMQ para processamento em background
- Suporte a múltiplas buscas simultâneas
- Resultados parciais disponíveis durante o processamento

### Persistência

- Armazenamento das tarefas de crawling no banco H2
- Histórico de URLs visitadas e encontradas
- Estatísticas detalhadas de cada busca
- Consulta de resultados em tempo real

### Monitoramento

#### Actuator Endpoints

- `/actuator/health`: Status da aplicação
- `/actuator/info`: Informações da aplicação
- `/actuator/metrics`: Métricas da aplicação

#### Console H2

Durante o desenvolvimento, você pode acessar o console H2 em `http://localhost:4567/h2-console` para visualizar os dados:

- **JDBC URL**: `jdbc:h2:mem:testdb`
- **User Name**: `sa`
- **Password**: (deixe vazio)

## Tecnologias Utilizadas

- **Java 17** com features modernas
- **Spring Boot 3.4.1** com configuração avançada
- **Spring Data JPA** (para persistência)
- **H2 Database** (banco de dados em memória)
- **Flyway** (migração de banco de dados)
- **RabbitMQ** (para processamento assíncrono)
- **JSoup** (para parsing HTML)
- **SpringDoc OpenAPI** (documentação da API)
- **JaCoCo** (para cobertura de código)
- **JUnit 5** (para testes automatizados)
- **Docker** (para containerização)

## Atendimento aos Requisitos do Desafio

### API HTTP (Porta 4567)

- **POST /crawl**: Inicia nova busca com keyword
- **GET /crawl/{id}**: Consulta resultados de busca
- **GET /crawl/{id}/stats**: Estatísticas detalhadas da busca

### ✅ Processamento Assíncrono

- Utiliza **RabbitMQ** para processamento em background
- Suporte a múltiplas buscas simultâneas
- Resultados disponíveis durante o processamento

### Containerização

- **Dockerfile** para build da aplicação
- **docker-compose.yml** com RabbitMQ integrado
- Configurações UTF-8 completas

### Qualidade de Código

- **Cobertura de testes**: 100% dos requisitos do desafio + 80% mínimo (JaCoCo)
- **Testes automatizados**: JUnit 5 + Mockito + integração E2E
- **Validações**: Spring Validation para entrada
- **Logs estruturados**: Logback com rotação

### Funcionalidades Extras Implementadas

- **Persistência**: H2 + Flyway para migrações
- **Monitoramento**: Spring Actuator endpoints
- **Massa de dados**: Dados de exemplo para demonstração
- **Documentação**: README + docs técnicas

## Requisitos

- Docker
- Docker Compose (opcional, para desenvolvimento local)

## Configuração

### Variáveis de Ambiente

- `BASE_URL`: URL base do website a ser analisado (padrão: `http://localhost:8080`)

### Banco de Dados

A aplicação utiliza H2 como banco de dados em memória com as seguintes configurações:

- **URL**: `jdbc:h2:mem:testdb`
- **Console H2**: Disponível em `http://localhost:4567/h2-console`
- **Usuário**: `sa`
- **Senha**: (vazia)

### Flyway

As migrações do banco de dados são gerenciadas pelo Flyway e estão localizadas em `src/main/resources/db/migration/`.

### Propriedades da Aplicação

As configurações podem ser ajustadas no arquivo `application.yml`:

```yaml
app:
  base-url: ${BASE_URL:http://localhost:8080}
  crawler:
    max-pages: 1000
    timeout: 30000
    user-agent: "Web Crawler 1.0"
    delay: 100
  search:
    min-keyword-length: 4
    max-keyword-length: 32
    id-length: 8
    max-results: 100
```

## Execução

### Docker Compose (Recomendado)

```bash
# 1. Criar arquivo de configuração
cp .env.example .env

# 2. Ajustar URL base se necessário (opcional)
# Edite o arquivo .env: BASE_URL=http://hiring.axreng.com/

# 3. Iniciar todos os serviços
docker-compose up -d

# 4. Verificar logs
docker-compose logs -f

# 5. Parar os serviços
docker-compose down
```

**URLs após inicialização:**

- **Aplicação**: <http://localhost:4567>
- **Swagger UI**: <http://localhost:4567/swagger-ui.html>
- **OpenAPI Docs**: <http://localhost:4567/api-docs>
- **RabbitMQ Management**: <http://localhost:15672> (guest/guest)
- **Health Check**: <http://localhost:4567/actuator/health>
- **H2 Console**: <http://localhost:4567/h2-console> (sa/vazio)

### Setup Automatizado (Scripts)

Para facilitar a inicialização, você pode usar os scripts de automação:

**Linux/macOS:**

```bash
# Configurar e iniciar ambiente completo automaticamente
chmod +x setup-dev.sh
./setup-dev.sh
```

**Windows (PowerShell):**

```powershell
# Configurar e iniciar ambiente completo automaticamente
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
.\setup-dev.ps1
```

Os scripts automaticamente:

1. Verificam se Docker está rodando
2. Criam arquivo `.env` se não existir
3. Fazem build da aplicação
4. Iniciam todos os serviços via Docker Compose
5. Verificam se os serviços estão saudáveis
6. Exibem URLs de acesso e informações úteis

### Desenvolvimento Local (sem Docker)

```bash
# 1. Iniciar apenas RabbitMQ
docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management

# 2. Executar aplicação localmente
mvn spring-boot:run
```

### Produção (Docker standalone)

```bash
# Build da aplicação
docker build . -t axreng/backend

# Execução
docker run -e BASE_URL=http://hiring.axreng.com/ -p 4567:4567 --rm axreng/backend
```

## API

A API está totalmente documentada com **OpenAPI 3.0** (Swagger). Após iniciar a aplicação, você pode:

- **Explorar a API**: Acesse <http://localhost:4567/swagger-ui.html>
- **Ver especificação OpenAPI**: Acesse <http://localhost:4567/api-docs>
- **Testar endpoints**: Use a interface interativa do Swagger UI

### Endpoints Disponíveis

### Iniciar Busca

**POST** `/crawl`

```json
{
  "keyword": "security"
}
```

**Resposta:**

```json
{
  "id": "30vbllyb"
}
```

### Consultar Resultados

**GET** `/crawl/{id}`

**Resposta:**

```json
{
  "id": "30vbllyb",
  "status": "active",
  "urls": [
    "http://hiring.axreng.com/index2.html",
    "http://hiring.axreng.com/htmlman1/chcon.1.html"
  ]
}
```

### Consultar Estatísticas

**GET** `/crawl/{id}/stats`

**Resposta:**

```json
{
  "id": "30vbllyb",
  "keyword": "security",
  "status": "active",
  "total_urls_found": 15,
  "total_pages_processed": 50,
  "start_time": "2025-07-09T12:00:00",
  "end_time": null
}
```

### Status da Busca

- `active`: Busca em andamento
- `done`: Busca concluída

## Testes

### Executar Testes

```bash
# Executar todos os testes
mvn test

# Executar testes com cobertura
mvn clean test jacoco:report

# Verificar cobertura mínima
mvn clean test jacoco:check
```

### Relatório de Cobertura

O relatório de cobertura JaCoCo será gerado em `target/site/jacoco/index.html`

## Monitoramento e Logs

### Endpoints de Monitoramento

- `/actuator/health`: Status da aplicação
- `/actuator/info`: Informações da aplicação
- `/actuator/metrics`: Métricas da aplicação

### Logs

A aplicação gera logs detalhados para debugging e monitoramento em diferentes arquivos:

**Arquivos de Log:**

- `logs/application.log`: Logs gerais da aplicação
- `logs/crawl.log`: Logs específicos de crawling
- `logs/test-execution.log`: Logs dos testes unitários

**Configuração de Log:**

- Rotação automática por tamanho (10MB para aplicação, 5MB para testes)
- Retenção de 30 dias para aplicação, 5 dias para testes
- Diferentes níveis de log para diferentes componentes

**Exemplo de logs:**

```text
2025-07-09 10:30:00 [main] INFO  com.mulato.axur.service.CrawlService - Starting crawl for task: abc12345
2025-07-09 10:30:01 [crawler-1] DEBUG com.mulato.axur.service.WebCrawlerService - Found keyword 'security' in URL: http://example.com/page1
2025-07-09 10:30:05 [crawler-1] INFO  com.mulato.axur.service.WebCrawlerService - Crawl completed for task: abc12345 - Pages processed: 50, URLs found: 5
```

## Arquitetura

### Componentes Principais

1. **CrawlController**: API REST endpoints
2. **CrawlService**: Gerenciamento de tarefas de crawling
3. **WebCrawlerService**: Lógica de web crawling
4. **CrawlTaskPersistenceService**: Persistência de dados
5. **CrawlMessageService**: Envio de mensagens para RabbitMQ
6. **CrawlTaskListener**: Processamento de mensagens do RabbitMQ

### Estrutura do Banco de Dados

**Tabelas:**

- `crawl_tasks`: Armazena informações das tarefas de crawling
- `crawl_urls`: Armazena URLs encontradas durante o crawling
- `visited_urls`: Controla URLs já visitadas para evitar loops

### Fluxo de Execução

1. Cliente envia POST `/crawl` com keyword
2. Sistema gera ID único e cria tarefa
3. Tarefa é enviada para fila RabbitMQ
4. Worker processa tarefa em background
5. Cliente consulta resultados via GET `/crawl/{id}`

## Limitações

- Máximo de 1000 páginas por busca
- Timeout de 30 segundos por requisição
- Delay de 100ms entre requisições
- Cobertura de código mínima de 80%

## Desenvolvimento

### Configuração do Ambiente

1. **Clone o repositório**
2. **Configure o RabbitMQ** (se executando localmente)
3. **Execute as migrações** (automático com Flyway)
4. **Inicie a aplicação**

### Migrações do Banco de Dados

As migrações são executadas automaticamente pelo Flyway na inicialização. Para criar novas migrações:

1. Crie um arquivo em `src/main/resources/db/migration/`
2. Nomeie seguindo o padrão: `V{version}__{description}.sql`
3. Exemplo: `V5__Add_new_column.sql`

### Estrutura do Projeto

```text
axreng-test/
├── README.md                           # Documentação principal do projeto
├── docker-compose.yml                  # Orquestração Docker principal (App + RabbitMQ)
desenvolvimento
├── Dockerfile                          # Build da aplicação Java
├── pom.xml                             # Dependências e configuração Maven
├── manage-data.sh                      # Script para gerenciar massa de dados
├── setup-dev.sh                        # Script de setup para Linux/macOS
├── setup-dev.ps1                       # Script de setup para Windows (PowerShell)
│
├── docs/                               # Documentações técnicas
│   ├── INDEX.md                        # Índice das documentações
│   ├── DESAFIO_JAVA_AXUR.md            # Requisitos originais do teste técnico
│   ├── DESAFIO_JAVA_AXUR.pdf           # Documento original (PDF)
│   ├── DATABASE.md                     # Estrutura do banco e massa de dados
│   ├── DOCKER-COMPOSE.md               # Guia do Docker Compose
│   └── UTF8-CONFIG.md                  # Configurações UTF-8 implementadas
│
├── logs/                               # Arquivos de log da aplicação
│   └── README.md                       # Documentação sobre logs
│
├── src/                                # Código fonte
│   ├── main/                           # Código principal da aplicação
│   │   ├── java/
│   │   │   └── com/mulato/axur/        # Pacote principal
│   │   │       ├── Main.java           # Classe principal Spring Boot
│   │   │       ├── config/             # Configurações Spring
│   │   │       │   └── RabbitConfig.java
│   │   │       ├── controller/         # Controllers REST
│   │   │       │   └── CrawlController.java
│   │   │       ├── entity/             # Entidades JPA
│   │   │       │   ├── CrawlTaskEntity.java
│   │   │       │   ├── CrawlResultEntity.java
│   │   │       │   └── VisitedUrlEntity.java
│   │   │       ├── exception/          # Tratamento de exceções
│   │   │       │   └── GlobalExceptionHandler.java
│   │   │       ├── listener/           # Listeners RabbitMQ
│   │   │       │   └── CrawlTaskListener.java
│   │   │       ├── model/              # DTOs e modelos
│   │   │       │   ├── CrawlRequest.java
│   │   │       │   ├── CrawlResponse.java
│   │   │       │   ├── CrawlResult.java
│   │   │       │   └── CrawlTask.java
│   │   │       ├── repository/         # Repositórios JPA
│   │   │       │   ├── CrawlTaskRepository.java
│   │   │       │   ├── CrawlResultRepository.java
│   │   │       │   └── VisitedUrlRepository.java
│   │   │       └── service/            # Lógica de negócio
│   │   │           ├── CrawlService.java
│   │   │           ├── CrawlMessageService.java
│   │   │           ├── CrawlPersistenceService.java
│   │   │           ├── IdGeneratorService.java
│   │   │           └── WebCrawlerService.java
│   │   │
│   │   └── resources/                  # Recursos da aplicação
│   │       ├── application.yml         # Configuração principal
│   │       ├── application-docker.yml  # Configuração para Docker
│   │       ├── logback-spring.xml      # Configuração de logs
│   │       └── db/                     # Banco de dados
│   │           ├── migration/          # Migrações Flyway
│   │           │   ├── V1__Create_crawl_tables.sql
│   │           │   ├── V2__Insert_test_data.sql
│   │           │   └── V3__Insert_realistic_test_data.sql
│   │           └── sample-data.sql     # Dados adicionais para desenvolvimento
│   │
│   └── test/                           # Testes unitários
│       ├── java/
│       │   └── com/mulato/axur/        # Mesma estrutura do código principal
│       │       ├── controller/         # Testes dos controllers
│       │       │   └── CrawlControllerTest.java
│       │       ├── entity/             # Testes das entidades
│       │       │   ├── CrawlTaskEntityTest.java
│       │       │   ├── CrawlResultEntityTest.java
│       │       │   └── VisitedUrlEntityTest.java
│       │       ├── exception/          # Testes de tratamento de exceções
│       │       │   └── GlobalExceptionHandlerTest.java
│       │       ├── listener/           # Testes dos listeners
│       │       │   └── CrawlTaskListenerTest.java
│       │       ├── model/              # Testes dos modelos
│       │       │   ├── CrawlRequestTest.java
│       │       │   ├── CrawlResponseTest.java
│       │       │   ├── CrawlResultTest.java
│       │       │   └── CrawlTaskTest.java
│       │       ├── repository/         # Testes dos repositórios
│       │       │   ├── CrawlResultRepositoryTest.java
│       │       │   └── VisitedUrlRepositoryTest.java
│       │       ├── service/            # Testes dos serviços
│       │       │   ├── CrawlServiceTest.java
│       │       │   ├── CrawlServiceLogTest.java
│       │       │   ├── CrawlMessageServiceTest.java
│       │       │   ├── CrawlPersistenceServiceTest.java
│       │       │   ├── IdGeneratorServiceTest.java
│       │       │   ├── WebCrawlerServiceTest.java
│       │       │   ├── LoggingConfigurationTest.java
│       │       │   └── LoggingIntegrationTest.java
│       │       └── util/               # Utilitários de teste
│       │           └── LogCapture.java
│       │
│       └── resources/                  # Recursos para testes
│           ├── application.yml         # Configuração para testes
│           └── logback-test.xml        # Configuração de logs para testes
│
└── target/                             # Arquivos gerados (Maven)
    ├── classes/                        # Classes compiladas
    ├── test-classes/                   # Classes de teste compiladas
    ├── site/jacoco/                    # Relatórios de cobertura JaCoCo
    └── surefire-reports/               # Relatórios de testes
```

### Detalhes da Estrutura

#### Principais Diretórios

- **`src/main/java/`**: Código fonte da aplicação
- **`src/test/java/`**: Testes unitários e de integração
- **`src/main/resources/`**: Configurações e recursos
- **`docs/`**: Documentação técnica completa
- **`logs/`**: Arquivos de log da aplicação

#### Scripts de Automação

- **`setup-dev.sh`**: Script para inicializar ambiente no Linux/macOS
- **`setup-dev.ps1`**: Script para inicializar ambiente no Windows (PowerShell)
- **`manage-data.sh`**: Script para gerenciar massa de dados do H2
- **`docker-compose.yml`**: Orquestração completa (App + RabbitMQ)
- **`docker-compose.dev.yml`**: Apenas RabbitMQ para desenvolvimento local

#### Arquitetura em Camadas

1. **Controller Layer** (`controller/`): Endpoints REST
2. **Service Layer** (`service/`): Lógica de negócio
3. **Repository Layer** (`repository/`): Acesso a dados
4. **Entity Layer** (`entity/`): Mapeamento JPA
5. **Model Layer** (`model/`): DTOs e transferência de dados

#### Configurações

- **Maven**: `pom.xml` + `.mvn/maven.config` (UTF-8)
- **Spring Boot**: `application.yml` + `application-docker.yml`
- **Docker**: `Dockerfile` + `docker-compose.yml` + `docker-compose.dev.yml`
- **Logs**: `logback-spring.xml` + `logback-test.xml`
- **Banco**: Migrações Flyway em `db/migration/`
- **Ambiente**: `.env` + `.env.example`

#### Qualidade e Testes

- **Cobertura**: JaCoCo reports em `target/site/jacoco/`
- **Testes**: Estrutura espelhada em `src/test/`
- **Utilitários**: `LogCapture` para validação de logs
- **Dados**: Massa de dados em `db/migration/` e `sample-data.sql`

### Convenções de Código

- Usar Java 17 features
- Seguir padrões Spring Boot
- Documentar métodos públicos
- Escrever testes unitários
- Manter cobertura de código acima de 80%

### Trabalhando com o Banco H2

**Acesso ao Console:**

1. Inicie a aplicação
2. Acesse `http://localhost:4567/h2-console`
3. Use as credenciais: JDBC URL: `jdbc:h2:mem:testdb`, User: `sa`, Password: (vazio)

**Queries úteis:**

```sql
-- Ver todas as tarefas
SELECT * FROM crawl_tasks;

-- Ver URLs encontradas para uma tarefa específica
SELECT * FROM crawl_urls WHERE task_id = 'your-task-id';

-- Ver estatísticas de uma tarefa
SELECT 
    ct.id,
    ct.keyword,
    ct.status,
    COUNT(cu.id) as total_urls_found,
    COUNT(DISTINCT vu.url) as pages_processed
FROM crawl_tasks ct
LEFT JOIN crawl_urls cu ON ct.id = cu.task_id
LEFT JOIN visited_urls vu ON ct.id = vu.task_id
WHERE ct.id = 'your-task-id'
GROUP BY ct.id;
```

## Documentação Adicional

Documentações técnicas detalhadas estão disponíveis na pasta `docs/`:

- **[docs/DESAFIO_JAVA_AXUR.md](docs/DESAFIO_JAVA_AXUR.md)**: Requisitos originais do desafio
- **[docs/DATABASE.md](docs/DATABASE.md)**: Estrutura do banco de dados e massa de dados
- **[docs/SWAGGER-API.md](docs/SWAGGER-API.md)**: Documentação da API com OpenAPI/Swagger
- **[docs/DOCKER-COMPOSE.md](docs/DOCKER-COMPOSE.md)**: Guia de uso do Docker Compose
- **[docs/UTF8-CONFIG.md](docs/UTF8-CONFIG.md)**: Configurações UTF-8 implementadas

## Sobre o Desenvolvedor

**Nome:** Christian Vladimir Uhdre Mulato  
**Posição:** Candidato a Desenvolvedor Java Sênior  
**Empresa:** Axur  
**Data:** Janeiro 2025  

### Tecnologias Demonstradas

- **Java 17** com features modernas
- **Spring Boot 3.4.1** com configuração avançada
- **Arquitetura de microserviços** com mensageria
- **Testes unitários** com alta cobertura
- **Containerização** Docker + Docker Compose
- **Banco de dados** H2 + Flyway migrations
- **Documentação** técnica completa

### Decisões Arquiteturais

1. **RabbitMQ** para processamento assíncrono (escalabilidade)
2. **H2 + Flyway** para persistência e migrações (praticidade)
3. **Spring Actuator** para monitoramento (observabilidade)
4. **JaCoCo** para cobertura de testes (qualidade)
5. **UTF-8** completo para internacionalização (boas práticas)
6. **Docker Compose** para facilitar execução (DX)

---

**Nota:** Este projeto foi desenvolvido como solução para o teste técnico da Axur, demonstrando conhecimentos em desenvolvimento Java enterprise, arquitetura de software e boas práticas de desenvolvimento.

## Testes Automatizados

### Cobertura Completa de Todos os Requisitos

Este projeto possui **cobertura de testes automatizados para 100% dos requisitos** do desafio técnico. Foram criados testes específicos para cada um dos 8 requisitos obrigatórios, além de testes adicionais para comportamentos específicos e integração E2E.

### Testes por Requisito

```markdown
|------------------------------|---------------------------------|-----------------------------------------------------------|
| Requisito                    | Arquivo de Teste                | Cenários Cobertos                                         |
|------------------------------|---------------------------------|-----------------------------------------------------------|
| **1. API HTTP**              | `ApiHttpTest.java`              | POST /crawl, GET /crawl/{id}, formatos JSON, códigos HTTP |
| **2. Validação do termo**    | `TermValidationTest.java`       | 4-32 caracteres, case insensitive, rejeição de inválidos  |
| **3. ID da busca**           | `CrawlIdGenerationTest.java`    | 8 caracteres alfanuméricos, geração automática, unicidade |
| **4. URL base**              | `BaseUrlConfigurationTest.java` | Variável de ambiente, formato válido, resolução de links  |
| **5. Múltiplas buscas**      | `MultipleCrawlsTest.java`       | Execução paralela, estado independente, persistência      |
| **6. Resultados parciais**   | `PartialResultsTest.java`       | Disponibilidade durante processamento, incremento de URLs |
| **7. Estrutura do projeto**  | `ProjectStructureTest.java`     | Dockerfile, pom.xml, estrutura de diretórios              |
| **8. Compilação e execução** | `CompilationExecutionTest.java` | Porta 4567, comandos Docker, variáveis de ambiente        |
|------------------------------|---------------------------------|-----------------------------------------------------------|
```

### Testes Adicionais

- **`CrawlerBehaviorTest.java`**: Comportamentos específicos (case sensitivity, charset, edge cases)
- **`EndToEndIntegrationTest.java`**: Testes E2E com massa de dados realista do `sample-data.sql`
- **`AllRequirementsTestSuite.java`**: Documentação e organização da suíte completa

### Comandos de Execução

#### Executar todos os testes

```bash
mvn test
```

#### Executar por requisito específico

```bash
# Requisito 1 - API HTTP
mvn test -Dtest="ApiHttpTest"

# Requisito 2 - Validação do termo
mvn test -Dtest="TermValidationTest"

# Requisito 3 - ID da busca
mvn test -Dtest="CrawlIdGenerationTest"

# Requisito 4 - URL base
mvn test -Dtest="BaseUrlConfigurationTest"

# Requisito 5 - Múltiplas buscas simultâneas
mvn test -Dtest="MultipleCrawlsTest"

# Requisito 6 - Resultados parciais
mvn test -Dtest="PartialResultsTest"

# Requisito 7 - Estrutura do projeto
mvn test -Dtest="ProjectStructureTest"

# Requisito 8 - Compilação e execução
mvn test -Dtest="CompilationExecutionTest"
```

#### Executar testes adicionais

```bash
# Comportamentos específicos
mvn test -Dtest="CrawlerBehaviorTest"

# Integração E2E
mvn test -Dtest="EndToEndIntegrationTest"
```

#### Executar via Docker

```bash
docker build . -t axreng/backend
docker run --rm axreng/backend mvn test

# Com variável de ambiente específica
docker run -e BASE_URL=http://hiring.axreng.com/ --rm axreng/backend mvn test
```

### Relatório de Cobertura

```bash
# Executar testes com cobertura
mvn clean test jacoco:report

# Verificar cobertura mínima (80%)
mvn clean test jacoco:check
```

O relatório de cobertura JaCoCo será gerado em `target/site/jacoco/index.html`

### Documentação Detalhada dos Testes

Para documentação completa de todos os testes implementados, consulte:

- **[Cobertura Completa de Testes](docs/TESTES-COBERTURA-COMPLETA.md)**: Detalhamento de todos os testes por requisito

### Estatísticas de Testes

- **Total de arquivos de teste**: 10
- **Total de métodos de teste**: 50+
- **Requisitos cobertos**: 8/8 (100%)
- **Cenários**: Positivos, negativos, edge cases, concorrência, performance
- **Tipos**: Unitários, integração, E2E
- **Massa de dados**: sample-data.sql com dados realistas

---
