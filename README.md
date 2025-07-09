# Web Crawler API - Desafio Java Axur

**Desenvolvedor:** Christian Vladimir Uhdre Mulato

## Descrição

Aplicação Java para navegar por um website em busca de um termo fornecido pelo usuário e listar as URLs onde o termo foi encontrado. Desenvolvida como solução para o teste técnico de Backend Developer.

## Requisitos Atendidos

### 1. API HTTP

A aplicação fornece uma API HTTP na porta 4567 com os seguintes endpoints:

**POST /crawl**: Inicia uma nova busca por um termo

```http
POST /crawl HTTP/1.1
Host: localhost:4567
Content-Type: application/json
Body: {"keyword": "security"}

Resposta:
200 OK
Content-Type: application/json
Body: {"id": "30vbllyb"}
```

**GET /crawl/{id}**: Consulta resultados de busca

```http
GET /crawl/30vbllyb HTTP/1.1
Host: localhost:4567

Resposta:
200 OK
Content-Type: application/json
{
  "id": "30vbllyb",
  "status": "active",
  "urls": [
    "http://hiring.axreng.com/index2.html",
    "http://hiring.axreng.com/htmlman1/chcon.1.html"
  ]
}
```

### 2. Validação do Termo

- Mínimo de 4 e máximo de 32 caracteres
- Busca case insensitive em qualquer parte do conteúdo HTML (incluindo tags e comentários)
- Implementação validada por testes unitários específicos

### 3. ID da Busca

- Código alfanumérico de 8 caracteres gerado automaticamente
- Único para cada busca
- Implementado através de classe dedicada para geração de IDs

### 4. URL Base

- Configurada via variável de ambiente (BASE_URL)
- Implementação segue apenas links (absolutos e relativos) da mesma base URL
- Validação completa de URLs para garantir segurança e conformidade

### 5. Múltiplas Buscas Simultâneas

- Suporte para execução de múltiplas buscas em paralelo
- Informações sobre buscas ativas e concluídas são mantidas durante a execução
- Implementado através de processamento assíncrono

### 6. Resultados Parciais

- Durante uma busca em andamento, os resultados já encontrados são disponibilizados
- Status da busca ("active" ou "done") é atualizado conforme progresso
- Implementação testada para garantir disponibilidade dos resultados parciais

### 7. Estrutura do Projeto

- Respeitada a estrutura base fornecida
- Dockerfile e pom.xml mantidos sem modificações
- Código organizado em pacotes seguindo boas práticas

### 8. Compilação e Execução

Compilação e execução conforme especificado:

```bash
docker build . -t axreng/backend
docker run -e BASE_URL=http://hiring.axreng.com/ -p 4567:4567 --rm axreng/backend
```

## Testes Automatizados

A aplicação conta com testes completos para validar todos os requisitos:

```markdown
|-------------------------|-----------------------------------------------------|
| Requisito               | Classe de Teste                                     |
|-------------------------|-----------------------------------------------------|
| 1. API HTTP             | ApiHttpControllerTest, ConformidadeDesafioTest      |
| 2. Validação do termo   | HtmlContentSearchTest                               |
| 3. ID da busca          | IdGeneratorServiceTest                              |
| 4. URL base             | BaseUrlConfigurationTest, WebCrawlerSameBaseUrlTest |
| 5. Múltiplas buscas     | MultipleCrawlsTest                                  |
| 6. Resultados parciais  | PartialResultsTest                                  |
|-------------------------|-----------------------------------------------------|
```

## Tecnologias Utilizadas

- Java
- Spring Boot
- H2 Database (banco de dados em memória)
- RabbitMQ (para processamento assíncrono)
- JSoup (para parsing HTML)
- JUnit 5 (para testes automatizados)
- Docker

## Execução do Projeto

```bash
# Build da aplicação
docker build . -t axreng/backend

# Execução
docker run -e BASE_URL=http://hiring.axreng.com/ -p 4567:4567 --rm axreng/backend
```

## Arquitetura

A aplicação segue uma arquitetura de camadas:

1. Controller: Endpoints REST da API
2. Service: Lógica de negócio e gerenciamento de crawling
3. Repository: Persistência de dados
4. Model/Entity: Objetos de domínio e entidades de banco
5. Listener: Processamento assíncrono de mensagens

# Web Crawler API - Desafio Java Axur

**Desenvolvedor:** Christian Vladimir Uhdre Mulato

## Descrição

Aplicação Java para navegar por um website em busca de um termo fornecido pelo usuário e listar as URLs onde o termo foi encontrado. Desenvolvida como solução para o teste técnico de Backend Developer.

## Requisitos Atendidos

### 1. API HTTP

A aplicação fornece uma API HTTP na porta 4567 com os seguintes endpoints:

**POST /crawl**: Inicia uma nova busca por um termo

```http
POST /crawl HTTP/1.1
Host: localhost:4567
Content-Type: application/json
Body: {"keyword": "security"}

Resposta:
200 OK
Content-Type: application/json
Body: {"id": "30vbllyb"}
```

**GET /crawl/{id}**: Consulta resultados de busca

```http
GET /crawl/30vbllyb HTTP/1.1
Host: localhost:4567

Resposta:
200 OK
Content-Type: application/json
{
  "id": "30vbllyb",
  "status": "active",
  "urls": [
    "http://hiring.axreng.com/index2.html",
    "http://hiring.axreng.com/htmlman1/chcon.1.html"
  ]
}
```

### 2. Validação do Termo

- Mínimo de 4 e máximo de 32 caracteres
- Busca case insensitive em qualquer parte do conteúdo HTML (incluindo tags e comentários)
- Implementação validada por testes unitários específicos

### 3. ID da Busca

- Código alfanumérico de 8 caracteres gerado automaticamente
- Único para cada busca
- Implementado através de classe dedicada para geração de IDs

### 4. URL Base

- Configurada via variável de ambiente (BASE_URL)
- Implementação segue apenas links (absolutos e relativos) da mesma base URL
- Validação completa de URLs para garantir segurança e conformidade

### 5. Múltiplas Buscas Simultâneas

- Suporte para execução de múltiplas buscas em paralelo
- Informações sobre buscas ativas e concluídas são mantidas durante a execução
- Implementado através de processamento assíncrono

### 6. Resultados Parciais

- Durante uma busca em andamento, os resultados já encontrados são disponibilizados
- Status da busca ("active" ou "done") é atualizado conforme progresso
- Implementação testada para garantir disponibilidade dos resultados parciais

### 7. Estrutura do Projeto

- Respeitada a estrutura base fornecida
- Dockerfile e pom.xml mantidos sem modificações
- Código organizado em pacotes seguindo boas práticas

### 8. Compilação e Execução

Compilação e execução conforme especificado:

```bash
docker build . -t axreng/backend
docker run -e BASE_URL=http://hiring.axreng.com/ -p 4567:4567 --rm axreng/backend
```

## Testes Automatizados

A aplicação conta com testes completos para validar todos os requisitos:

```markdown
|-----------------------|-----------------------------------------------------|
| Requisito             | Classe de Teste                                     |
|-----------------------|-----------------------------------------------------|
| 1. API HTTP           | ApiHttpControllerTest, ConformidadeDesafioTest      |
| 2. Validação do termo | HtmlContentSearchTest                               |
| 3. ID da busca        | IdGeneratorServiceTest                              |
| 4. URL base           | BaseUrlConfigurationTest, WebCrawlerSameBaseUrlTest |
| 5. Múltiplas buscas   | MultipleCrawlsTest                                  |
| 6. Resultados parciais| PartialResultsTest                                  |
|-----------------------|-----------------------------------------------------|
```

## Tecnologias Utilizadas

- Java
- Spring Boot
- H2 Database (banco de dados em memória)
- RabbitMQ (para processamento assíncrono)
- JSoup (para parsing HTML)
- JUnit 5 (para testes automatizados)
- Docker

## Execução do Projeto

```bash
# Build da aplicação
docker build . -t axreng/backend

# Execução
docker run -e BASE_URL=http://hiring.axreng.com/ -p 4567:4567 --rm axreng/backend
```

## Arquitetura

A aplicação segue uma arquitetura de camadas:

1. Controller: Endpoints REST da API
2. Service: Lógica de negócio e gerenciamento de crawling
3. Repository: Persistência de dados
4. Model/Entity: Objetos de domínio e entidades de banco
5. Listener: Processamento assíncrono de mensagens
