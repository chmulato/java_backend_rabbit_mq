# Changelog

Todas as mudanças importantes neste projeto serão documentadas neste arquivo.

O formato é baseado em [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
e este projeto segue [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]

### Planejado
- Suporte a JavaScript rendering (Puppeteer/Selenium)
- Plugin system para custom extractors
- GraphQL API endpoint
- Kubernetes Helm charts
- Multi-tenant support
- Machine Learning content classification

## [1.0.0] - 2025-07-15

### Adicionado
- **API REST completa** com endpoints POST /crawl e GET /crawl/{id}
- **Processamento assíncrono** com RabbitMQ
- **Múltiplas buscas simultâneas** independentes
- **Resultados parciais** em tempo real
- **Validação inteligente** de termos (4-32 caracteres)
- **Sistema de IDs únicos** (8 caracteres alfanuméricos)
- **Crawling seguro** com validação de URL base
- **Controle de URLs visitadas** para evitar loops
- **Containerização Docker** completa
- **Documentação Swagger/OpenAPI** automática
- **Testes automatizados** com >90% cobertura
- **Spring Boot 3.x** com Java 17
- **H2 Database** para desenvolvimento
- **Flyway** para migrações de banco
- **JSoup** para parsing HTML
- **Spring Boot Actuator** para monitoramento
- **Logs estruturados** para auditoria

### Tecnologias
- Java 17
- Spring Boot 3.4.1
- RabbitMQ 3
- H2 Database
- JSoup 1.16.1
- JUnit 5
- Mockito
- Docker & Docker Compose
- Maven 3.6+

### Documentação
- README.md completo
- Documentação técnica detalhada
- Guias de instalação e uso
- Especificações da API
- Arquitetura RabbitMQ
- Configuração Docker Compose

### Qualidade
- Testes unitários abrangentes
- Testes de integração E2E
- Cobertura de código >90%
- Validação de todos os requisitos funcionais
- Tratamento robusto de erros
- Logs detalhados para debugging

### Deploy
- Dockerfile otimizado
- Docker Compose para desenvolvimento
- Configurações de produção
- Health checks automatizados
- Monitoramento com Actuator

---

## Tipos de Mudanças

- `Added` - para novas funcionalidades
- `Changed` - para mudanças em funcionalidades existentes
- `Deprecated` - para funcionalidades que serão removidas
- `Removed` - para funcionalidades removidas
- `Fixed` - para correções de bugs
- `Security` - para correções de vulnerabilidades

## Links

- [Repository](https://github.com/chmulato/web-crawler-api)
- [Issues](https://github.com/chmulato/web-crawler-api/issues)
- [Releases](https://github.com/chmulato/web-crawler-api/releases)
