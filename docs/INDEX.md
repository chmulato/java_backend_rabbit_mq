# Documentação Técnica - Web Crawler API

**Projeto:** Web Crawler A### Como Usar Esta Documentação

1. **[README.md](../README.md)** - Comece aqui! Visão geral completa e instruções de execução
2. **[ARCHITECTURE.md](ARCHITECTURE.md)** - Documentação técnica da arquitetura
3. **[TECHNICAL_SPECS.md](TECHNICAL_SPECS.md)** - Especificações técnicas detalhadas
4. **[TESTES-COBERTURA-COMPLETA.md](TESTES-COBERTURA-COMPLETA.md)** - Documentação completa dos testes**Desenvolvedor:** Christian Vladimir Uhdre Mulato  

## Índice de Documentações

### Requisitos e Especificações

- **[docs/ARCHITECTURE.md](ARCHITECTURE.md)** - Documentação técnica da arquitetura
- **[docs/TECHNICAL_SPECS.md](TECHNICAL_SPECS.md)** - Especificações técnicas detalhadas

### Banco de Dados

- **[DATABASE.md](DATABASE.md)** - Estrutura completa do banco, migrações e massa de dados

### API e Documentação

- **[SWAGGER-API.md](SWAGGER-API.md)** - Documentação da API com OpenAPI/Swagger

### Containerização

- **[DOCKER-COMPOSE.md](DOCKER-COMPOSE.md)** - Guia completo do Docker Compose

### Scripts de Automação

O projeto inclui scripts de automação para facilitar o setup:

- **`setup-dev.sh`** / **`setup-dev.ps1`** - Setup completo do ambiente de desenvolvimento
- **`build-docker.sh`** / **`build-docker.ps1`** - Build automatizado das imagens Docker
- **`manage-data.sh`** / **`manage-data.ps1`** - Gerenciamento da massa de dados

## Estrutura do Projeto

```text
java_backend_rabbit_mq/
├── README.md                           # Documentação principal
├── docs/                               # Documentações técnicas
│   ├── INDEX.md                        # Este arquivo - índice de documentações
│   ├── ARCHITECTURE.md                 # Documentação técnica da arquitetura
│   ├── TECHNICAL_SPECS.md              # Especificações técnicas detalhadas
│   ├── DATABASE.md                     # Estrutura do banco e massa de dados
│   ├── SWAGGER-API.md                  # Documentação da API OpenAPI/Swagger
│   ├── DOCKER-COMPOSE.md               # Guia completo do Docker Compose
├── src/                                # Código fonte
│   ├── main/java/                      # Código principal da aplicação
│   └── test/java/                      # Testes automatizados (100% cobertura)
│       ├── controller/                 # Testes dos controllers (Requisito 1)
│       ├── validation/                 # Testes de validação (Requisito 2)
│       ├── model/                      # Testes de geração de ID (Requisito 3)
│       ├── service/                    # Testes de serviços (Requisitos 4,5,6)
│       └── integration/                # Testes de integração (Requisitos 7,8)
├── docker-compose.yml                  # Orquestração Docker principal
├── Dockerfile                          # Build da aplicação Java
└── pom.xml                             # Dependências e configuração Maven
```

## Stack Tecnológica

- **Java 17** com features modernas
- **Spring Boot 3.4.1** (Web, Data JPA, Actuator)
- **H2 Database** (banco em memória)
- **Flyway** (migrações de banco)
- **RabbitMQ** (processamento assíncrono)
- **JSoup** (parsing HTML)
- **SpringDoc OpenAPI** (documentação Swagger)
- **JUnit 5** (testes automatizados)
- **JaCoCo** (cobertura de código)
- **Docker** (containerização)

## Como Usar Esta Documentação

1. **[README.md](../README.md)** - Comece aqui! Visão geral completa e instruções de execução
2. **[docs/ARCHITECTURE.md](ARCHITECTURE.md)** - Documentação técnica da arquitetura
3. **[TESTES-COBERTURA-COMPLETA.md](TESTES-COBERTURA-COMPLETA.md)** - **NOVO!** Documentação completa dos testes para
   todos os requisitos
4. **[DATABASE.md](DATABASE.md)** - Estrutura do banco de dados e massa de dados
5. **[SWAGGER-API.md](SWAGGER-API.md)** - Documentação da API com OpenAPI/Swagger
6. **[DOCKER-COMPOSE.md](DOCKER-COMPOSE.md)** - Execução em containers Docker
7. **[UTF8-CONFIG.md](UTF8-CONFIG.md)** - Configurações de encoding UTF-8

## Navegação Rápida

### Execução Rápida

#### Opção 1: Script Automatizado (Recomendado)

```bash
# Linux/macOS
chmod +x setup-dev.sh
./setup-dev.sh

# Windows PowerShell
Set-ExecutionPolicy -ExecutionPolicy RemoteSigned -Scope CurrentUser
.\setup-dev.ps1
```

#### Opção 2: Docker Compose Manual

```bash
# 1. Clone e entre no diretório
cd web-crawler-api

# 2. Execute com Docker Compose
docker-compose up -d

# 3. Acesse a aplicação
curl -X POST http://localhost:4567/crawl -H "Content-Type: application/json" -d '{"keyword":"security"}'
```

### Execução e Testes

#### **Status Atual: APLICAÇÃO 100% FUNCIONAL**

A aplicação está completamente funcional e testada em **duas modalidades**:

1. **Execução Local** - RabbitMQ via Docker + App via Maven
2. **Execução Containerizada** - App + RabbitMQ via Docker Compose

#### Funcionalidades Confirmadas

- **API REST**: Endpoints `/crawl` (POST e GET) funcionando
- **RabbitMQ**: Conectado e processando mensagens assíncronas
- **Database H2**: Migrações Flyway aplicadas com sucesso
- **OpenAPI/Swagger**: Documentação completa disponível
- **Validação**: Bean validation com mensagens de erro adequadas
- **Processamento Assíncrono**: Listener RabbitMQ ativo e funcional
- **Estrutura**: Todos os 8 requisitos do desafio atendidos

#### URLs Disponíveis (Aplicação Rodando)

- **API Principal**: <http://localhost:4567/crawl>
- **Swagger UI**: <http://localhost:4567/swagger-ui.html>
- **OpenAPI JSON**: <http://localhost:4567/api-docs>
- **H2 Console**: <http://localhost:4567/h2-console>
- **Health Check**: <http://localhost:4567/actuator/health>
- **RabbitMQ Management**: <http://localhost:15672> (guest/guest)

#### Como Executar

##### Opção 1: Execução Local (Recomendado para desenvolvimento)

```bash
# 1. Iniciar RabbitMQ via Docker
docker-compose up -d rabbitmq

# 2. Verificar se RabbitMQ está saudável
docker ps | grep rabbitmq

# 3. Executar aplicação Spring Boot localmente
cd c:\dev\web_crawler_api
set BASE_URL=http://example.com
mvn spring-boot:run
```

##### Opção 2: Execução Containerizada (Produção)

```bash
# Executar aplicação completa com Docker Compose
docker-compose up -d

# Verificar status dos containers
docker-compose ps

# Ver logs da aplicação
docker-compose logs -f app
```

#### Teste Rápido da API

```bash
# Iniciar uma nova busca
curl -X POST http://localhost:4567/crawl \
  -H "Content-Type: application/json" \
  -d '{"keyword":"security"}'

# Resposta esperada: {"id":"abcd1234"}

# Consultar resultados
curl http://localhost:4567/crawl/abcd1234

# Health check
curl http://localhost:4567/actuator/health
```

### Comandos Úteis

#### Gerenciamento de Serviços

```bash
# Executar aplicação completa
docker-compose up -d

# Ver logs da aplicação
docker-compose logs -f app

# Ver logs do RabbitMQ
docker-compose logs -f rabbitmq

# Parar todos os serviços
docker-compose down

# Parar e remover volumes
docker-compose down -v
```

#### Execução de Testes

```bash
# Executar TODOS os testes
mvn test

# Executar testes com cobertura
mvn clean test jacoco:report

# Executar teste específico
mvn test -Dtest="ApiHttpTest"

# Via Docker
docker run --rm web-crawler-api/backend mvn test
```

#### Desenvolvimento

```bash
# Executar aplicação localmente (sem Docker)
mvn spring-boot:run

# Build da imagem Docker
docker build . -t web-crawler-api/backend

# Verificar estrutura do projeto
tree src/ || find src/ -type f -name "*.java"
```

#### Teste da API

```bash
# Testar API manualmente
curl -X POST http://localhost:4567/crawl \
  -H "Content-Type: application/json" \
  -d '{"keyword":"security"}'

# Verificar resultado
curl http://localhost:4567/crawl/{id}

# Health check
curl http://localhost:4567/actuator/health
```

## Testes Automatizados Completos

Este projeto possui **cobertura de testes automatizados para 100% das funcionalidades**:

### Cobertura por Requisito

```markdown
|--------------------------|--------|---------------------------------|
| Funcionalidade           | Status | Arquivo de Teste                |
|--------------------------|--------|---------------------------------|
| API HTTP                 | OK     | `ApiHttpTest.java`              |
| Validação do termo       | OK     | `TermValidationTest.java`       |
| Geração de ID            | OK     | `CrawlIdGenerationTest.java`    |
| Configuração de URL base | OK     | `BaseUrlConfigurationTest.java` |
| Múltiplas buscas         | OK     | `MultipleCrawlsTest.java`       |
| Resultados parciais      | OK     | `PartialResultsTest.java`       |
| Estrutura do projeto     | OK     | `ProjectStructureTest.java`     |
| Compilação e execução    | OK     | `CompilationExecutionTest.java` |
|--------------------------|--------|---------------------------------|
```

### Estatísticas

- **10 arquivos de teste** criados
- **50+ métodos de teste** implementados
- **8/8 requisitos cobertos** (100%)
- **Tipos**: Unitários, integração, E2E
- **Cenários**: Positivos, negativos, edge cases, concorrência

### Execução Rápida dos Testes

```bash
# Todos os testes
mvn test

# Teste específico por requisito
mvn test -Dtest="ApiHttpTest"           # Requisito 1
mvn test -Dtest="TermValidationTest"    # Requisito 2
# ... etc

# Via Docker
docker run --rm web-crawler-api/backend mvn test
```

**Documentação completa:** [TESTES-COBERTURA-COMPLETA.md](TESTES-COBERTURA-COMPLETA.md)

## Destaques Técnicos

### Pontos de Destaque

1. **100% de Cobertura**: Todas as funcionalidades possuem testes automatizados
2. **Documentação Completa**: Swagger/OpenAPI, guias técnicos e documentação de testes
3. **Containerização**: Docker multi-stage build e Docker Compose com orquestração completa
4. **Scripts de Automação**: Setup automatizado para facilitar avaliação e desenvolvimento
5. **Configuração UTF-8**: Suporte internacional completo em todas as camadas
6. **Massa de Dados**: Dados realistas no sample-data.sql para testes robustos
7. **Monitoramento**: Actuator endpoints e console H2 para debug e monitoring

### Como Avaliar

1. **Execução Rápida**: Use os scripts `setup-dev.*` para inicialização automática
2. **Teste da API**: Acesse o Swagger UI em `http://localhost:4567/swagger-ui.html`
3. **Executar Testes**: Use `mvn test` para executar todos os testes automatizados
4. **Verificar Cobertura**: Use `mvn clean test jacoco:report` para relatório de cobertura
5. **Consultar Documentação**: Navegue pelos arquivos em `docs/` para detalhes técnicos

### Estrutura de Arquivos de Teste

```text
src/test/java/com/mulato/api/
├── AllRequirementsTestSuite.java       # Suite que executa todos os testes
├── controller/
│   └── ApiHttpTest.java                # Requisito 1: API HTTP
├── validation/
│   └── TermValidationTest.java         # Requisito 2: Validação do termo
├── model/
│   └── CrawlIdGenerationTest.java      # Requisito 3: ID da busca
├── service/
│   ├── BaseUrlConfigurationTest.java   # Requisito 4: URL base
│   ├── MultipleCrawlsTest.java         # Requisito 5: Múltiplas buscas
│   ├── PartialResultsTest.java         # Requisito 6: Resultados parciais
│   └── CrawlerBehaviorTest.java        # Testes comportamentais adicionais
└── integration/
    ├── ProjectStructureTest.java       # Requisito 7: Estrutura do projeto
    ├── CompilationExecutionTest.java   # Requisito 8: Compilação e execução
    └── EndToEndIntegrationTest.java    # Testes E2E completos
```

---

**Desenvolvido por:** Christian Vladimir Uhdre Mulato  
**Desenvolvedor:** Christian Vladimir Uhdre Mulato  
**Data:** Campo Largo, PR, 15 de Julho de 2025  
**Repositório:** [GitHub](https://github.com/chmulato/java_backend_rabbit_mq)
