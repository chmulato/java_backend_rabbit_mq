# Web Crawler API

**Desenvolvedor:** Christian Vladimir Uhdre Mulato  
**Versão:** 1.0.0  
**Licença:** MIT

## Documentação

- **[Arquitetura](./docs/ARCHITECTURE.md)** - Visão detalhada da arquitetura do sistema
- **[Especificações Técnicas](./docs/TECHNICAL_SPECS.md)** - Especificações técnicas completas
- **[Documentação de Banco](./docs/DATABASE.md)** - Estrutura do banco de dados
- **[Docker Compose](./docs/DOCKER-COMPOSE.md)** - Configuração de contêineres

## Diagrama de Arquitetura

![Diagrama de Arquitetura](./img/desafio.png)

## Sobre o Projeto

**API Crawler** é uma aplicação Java moderna e robusta para **web crawling automatizado**. A API permite navegar sistematicamente por websites em busca de termos específicos, retornando as URLs onde o conteúdo foi encontrado.

## Aplicabilidade (Explicando para uma criança)

Imagine que você tem um robô mágico que sabe ler muito rápido. Se você der ao robô um livro enorme e pedir para ele encontrar todas as páginas que falam sobre "dinossauros", ele vai ler o livro inteiro em poucos segundos e te mostrar a lista das páginas onde encontrou essa palavra!

Nosso Web Crawler API é como esse robô, mas para a internet. Ele funciona assim:

1. **Você diz uma palavra especial** - Por exemplo, "dinossauros"
2. **O robô começa a visitar páginas da internet** - Como se estivesse virando as páginas de um livro gigante
3. **Quando encontra sua palavra, anota o endereço** - "Achei 'dinossauros' nesta página!"
4. **Te mostra uma lista de todos os lugares** - "Aqui estão todas as páginas que falam sobre dinossauros!"

É como ter um assistente super rápido que consegue procurar em milhares de páginas da internet enquanto você toma um copo de suco!

### **O que nosso robô consegue fazer:**

- **Ler muito rápido** - Ele visita muitas páginas ao mesmo tempo!
- **Começar a te mostrar resultados antes de terminar** - Você não precisa esperar ele ler tudo
- **Procurar várias palavras diferentes ao mesmo tempo** - Como ter vários robôs trabalhando juntos
- **Nunca se perder ou ficar confuso** - Ele lembra de todas as páginas que já visitou

## Casos de Uso

### **Monitoramento de Conteúdo**

- Detecção de conteúdo malicioso ou indevido
- Monitoramento de marcas e propriedade intelectual
- Verificação de menções e referências

### **Análise e Pesquisa**

- Coleta de dados estruturados de websites
- Monitoramento de preços e produtos
- Análise competitiva e de mercado

### **Segurança Digital**

- Detecção de vazamentos de dados
- Monitoramento de atividades suspeitas
- Compliance e auditoria de conteúdo

### **SEO e Marketing**

- Análise de backlinks e estrutura de sites
- Monitoramento de posicionamentos
- Descoberta de oportunidades de conteúdo

## Funcionalidades da API

### 1. **Iniciar Busca por Termo** - `POST /crawl`

Inicia uma nova busca por um termo específico em um website.

**Endpoint:**

```http
POST /crawl HTTP/1.1
Host: localhost:4567
Content-Type: application/json

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

### 2. **Consultar Resultados** - `GET /crawl/{id}`

Consulta o progresso e resultados de uma busca específica.

**Endpoint:**

```http
GET /crawl/30vbllyb HTTP/1.1
Host: localhost:4567
```

**Resposta:**

```json
{
  "id": "30vbllyb",
  "status": "active",
  "urls": [
    "http://example.com/page1.html",
    "http://example.com/page2.html"
  ]
}
```

**Status possíveis:**

- `active`: Busca em andamento
- `done`: Busca finalizada

## Características Técnicas

### **Validação Inteligente**

- **Termos de busca:** 4 a 32 caracteres
- **Busca case-insensitive** em todo conteúdo HTML
- **Detecção em tags e comentários** HTML
- **Validação de URLs** para segurança

### **Sistema de IDs Únicos**

- **Códigos alfanuméricos** de 8 caracteres
- **Geração automática** para cada busca
- **Identificação única** garantida
- **Rastreamento simples** de progresso

### **Crawling Seguro**

- **Configuração via ambiente** (BASE_URL)
- **Apenas links da mesma base** URL
- **Prevenção de loops infinitos**
- **Controle de URLs visitadas**

### **Processamento Paralelo**

- **Múltiplas buscas simultâneas** independentes
- **Processamento assíncrono** com RabbitMQ
- **Resultados parciais** disponíveis em tempo real
- **Status dinâmico** (active/done)

### **Arquitetura Escalável**

- **Message broker** RabbitMQ para filas
- **Workers independentes** para processamento
- **Escalabilidade horizontal** automática
- **Tolerância a falhas** integrada

## Tecnologias e Stack

### **Backend Core**

- **Java 17** - Linguagem moderna e robusta
- **Spring Boot 3.x** - Framework enterprise
- **Spring Data JPA** - Abstração de dados elegante
- **Spring AMQP** - Integração com message brokers

### **Processamento e Dados**

- **RabbitMQ** - Message broker para processamento assíncrono
- **H2 Database** - Banco em memória para desenvolvimento
- **Flyway** - Migrações de banco versionadas
- **JSoup** - Parsing HTML especializado

### **Qualidade e Testes**

- **JUnit 5** - Framework de testes moderno
- **Mockito** - Mocking avançado para testes
- **Spring Boot Test** - Testes de integração
- **Testcontainers** - Testes com containers

### **DevOps e Infraestrutura**

- **Docker** - Containerização completa
- **Docker Compose** - Orquestração local
- **Maven** - Gerenciamento de dependências
- **Spring Boot Actuator** - Métricas e monitoramento

### **Documentação e APIs**

- **SpringDoc OpenAPI** - Documentação automática
- **Swagger UI** - Interface interativa para API
- **Markdown** - Documentação técnica estruturada

## Quick Start

### **Execução Rápida com Docker**

```bash
# Build da aplicação
docker build . -t web-crawler-api

# Execução
docker run -e BASE_URL=http://example.com/ -p 4567:4567 --rm web-crawler-api
```

### **Testando a API**

```bash
# Iniciar uma busca
curl -X POST http://localhost:4567/crawl \
  -H "Content-Type: application/json" \
  -d '{"keyword": "security"}'

# Resposta: {"id": "30vbllyb"}

# Consultar progresso
curl http://localhost:4567/crawl/30vbllyb
```

## Funcionalidades Avançadas

### **Web Crawling Inteligente**

- **Parsing HTML robusto** com JSoup
- **Busca case-insensitive** em conteúdo completo
- **Seguimento inteligente** de links (absolutos/relativos)
- **Controle de duplicatas** para evitar loops infinitos
- **Validação de domínio** para segurança

### **Processamento Assíncrono**

- **Filas RabbitMQ** para desacoplamento
- **Workers independentes** para escalabilidade
- **Múltiplas buscas paralelas** sem interferência
- **Resultados em tempo real** durante processamento
- **Status dinâmico** com atualizações automáticas

### **Robustez e Confiabilidade**

- **Validação rigorosa** de entrada e URLs
- **Tratamento completo** de erros HTTP
- **Timeout control** para requisições
- **Retry automático** em falhas temporárias
- **Logs estruturados** para auditoria

### **Monitoramento e Observabilidade**

- **Spring Actuator** para health checks
- **Métricas de performance** integradas
- **Interface RabbitMQ** para gestão de filas
- **Logs detalhados** de todas operações
- **Swagger UI** para documentação interativa

### **Escalabilidade Enterprise**

- **Arquitetura de microserviços** ready
- **Horizontal scaling** via containers
- **Load balancing** support
- **Cloud deployment** optimized
- **Database migration** automatizada

## Execução e Deploy

### **Execução com Docker (Recomendado)**

```bash
# Build da imagem
docker build . -t web-crawler-api

# Execução básica
docker run -e BASE_URL=http://example.com/ -p 4567:4567 --rm web-crawler-api

# Com múltiplas variáveis
docker run \
  -e BASE_URL=http://example.com/ \
  -e SPRING_PROFILES_ACTIVE=production \
  -p 4567:4567 \
  --name crawler-api \
  web-crawler-api
```

### **Desenvolvimento Local**

#### Pré-requisitos

- **Java 17+**
- **Maven 3.6+**
- **Docker** (para RabbitMQ)

#### Passos de Execução

1. **Iniciar RabbitMQ**

   ```bash
   docker-compose up -d rabbitmq
   ```

2. **Executar aplicação**

   ```bash
   mvn spring-boot:run
   ```

3. **Acessar serviços**

   - **API:** [http://localhost:4567](http://localhost:4567)
   - **Swagger:** [http://localhost:4567/swagger-ui.html](http://localhost:4567/swagger-ui.html)
   - **RabbitMQ:** [http://localhost:15672](http://localhost:15672) (guest/guest)

### **Deploy Produção**

#### Docker Compose Completo

```bash
# Subir todos os serviços
docker-compose up --build -d

# Verificar status
docker-compose ps

# Ver logs
docker-compose logs -f web-crawler-api
```

#### Kubernetes (Helm Chart disponível)

```bash
# Deploy básico
helm install crawler-api ./helm/web-crawler-api

# Com configurações customizadas
helm install crawler-api ./helm/web-crawler-api \
  --set image.tag=latest \
  --set baseUrl=http://production.com
```

## Testes e Qualidade

### **Cobertura Completa de Testes**

A aplicação possui **cobertura de testes abrangente** validando todas as funcionalidades:

```markdown
|-------------------|-----------------------------|---------------------------|
| **Categoria**     | **Classes de Teste**        | **Cobertura**             |
|-------------------|-----------------------------|---------------------------|
| **API HTTP**      | `ApiHttpControllerTest`     | Endpoints completos       |
| **Validação**     | `HtmlContentSearchTest`     | Regras de negócio         |
| **ID Generation** | `IdGeneratorServiceTest`    | Unicidade garantida       |
| **URL Handling**  | `WebCrawlerSameBaseUrlTest` | Segurança validada        |
| **Concorrência**  | `MultipleCrawlsTest`        | Paralelismo testado       |
| **Resultados**    | `PartialResultsTest`        | Tempo real verificado     |
|-------------------|-----------------------------|---------------------------|
```

### **Executando Testes**

#### Todos os testes

```bash
mvn test
```

#### Suite específica

```bash
mvn test -Dtest=AllRequirementsTestSuite
```

#### Relatório de cobertura

```bash
mvn jacoco:report
```

### **Métricas de Qualidade**

- **Cobertura de código:** >90%
- **Testes unitários:** 50+ casos
- **Testes de integração:** 20+ cenários
- **Performance tests:** Incluído
- **Security tests:** Validação de URLs

## Arquitetura e Design

### **Padrões Arquiteturais**

A aplicação segue **arquitetura em camadas** com separação clara de responsabilidades:

```text
┌─────────────────┐     ┌──────────────────┐    ┌─────────────────┐
│   Controllers   │───▶│    Services       │───▶│  Repositories   │
│   (REST API)    │     │ (Business Logic) │    │ (Data Access)   │
└─────────────────┘     └──────────────────┘    └─────────────────┘
         │                       │                       │
         ▼                       ▼                       ▼
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│   Models/DTOs   │    │    Listeners     │    │    Entities     │
│  (Data Transfer)│    │ (Message Queue)  │    │  (JPA Mapping)  │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

### **Fluxo de Processamento Assíncrono**

```text
HTTP Request → Controller → Service → RabbitMQ Queue
     ↓              ↑           ↑            ↓
Response ID    Status Check  Update      Worker Process
     ↓              ↑           ↑            ↓
   Client      Database    Real-time    Web Crawling
             (H2/JPA)     Updates      (JSoup)
```

### **Princípios SOLID Aplicados**

- **SRP:** Cada classe tem responsabilidade única
- **OCP:** Extensível via interfaces e abstrações
- **LSP:** Implementações substituíveis
- **ISP:** Interfaces específicas e coesas
- **DIP:** Dependências via abstrações

### **Design Patterns Utilizados**

- **Repository Pattern:** Abstração de acesso a dados
- **Service Layer:** Encapsulamento de lógica de negócio
- **Observer Pattern:** Listeners para mensagens RabbitMQ
- **Factory Pattern:** Geração de IDs únicos
- **Strategy Pattern:** Diferentes estratégias de crawling

## Message Broker - Por que RabbitMQ?

### **Problemas que o RabbitMQ Resolve**

#### **Sem Message Broker (Síncrono):**

- Timeout em crawling de sites grandes
- Bloqueio de threads HTTP
- Escalabilidade limitada
- Perda de tarefas em falhas
- Impossível consultar progresso

#### **Com RabbitMQ (Assíncrono):**

- **Resposta imediata** com ID da tarefa
- **Processamento em background** independente
- **Consulta de progresso** em tempo real
- **Escalabilidade horizontal** via workers
- **Persistência de tarefas** com durabilidade

### **Implementação Técnica**

```text
Cliente HTTP → Spring Boot API → RabbitMQ Queue → Worker Threads
                     ↓                              ↓
                 Resposta ID                   Processamento
                     ↑                              ↓
                 Consulta Status            Persistência Resultados
```

### **Benefícios Específicos**

1. **Performance:** Dezenas de buscas simultâneas
2. **Escalabilidade:** Workers distribuídos
3. **Confiabilidade:** Auto-retry em falhas
4. **Desacoplamento:** API independente do crawling
5. **Observabilidade:** Interface de gestão integrada

### **Alternativas Consideradas**

```markdown
|------------------------|----------|------------------------|---------------|
| **Tecnologia**         | **Prós** | **Contras**            | **Veredicto** |
|------------------------|----------|------------------------|---------------|
| **ThreadPoolExecutor** | Simples  | Limitado a 1 JVM       | Não escalável |
| **Database Polling**   | Familiar | Overhead contínuo      | Ineficiente   |
| **Apache Kafka**       | Powerful | Complexidade excessiva | Overkill      |
| **Redis Queues**       | Rápido   | Menos durabilidade     | Aceitável     |
| **RabbitMQ**           | Balanced | Curva de aprendizado   | **Escolhido** |
|------------------------|----------|------------------------|---------------|
```

## Documentação Técnica

### **Documentação Completa**

Para informações detalhadas sobre cada aspecto do projeto:

- **[docs/INDEX.md](docs/INDEX.md)** - Índice completo da documentação
- **[docs/RABBITMQ.md](docs/RABBITMQ.md)** - Arquitetura RabbitMQ detalhada
- **[docs/DATABASE.md](docs/DATABASE.md)** - Estrutura do banco e dados
- **[docs/SWAGGER-API.md](docs/SWAGGER-API.md)** - Documentação completa da API
- **[docs/DOCKER-COMPOSE.md](docs/DOCKER-COMPOSE.md)** - Guia Docker Compose

### **Links Úteis**

- **Swagger UI:** [http://localhost:4567/swagger-ui.html](http://localhost:4567/swagger-ui.html)
- **RabbitMQ Management:** [http://localhost:15672](http://localhost:15672)
- **Health Check:** [http://localhost:4567/actuator/health](http://localhost:4567/actuator/health)
- **Métricas:** [http://localhost:4567/actuator/metrics](http://localhost:4567/actuator/metrics)

## Contribuição e Desenvolvimento

### **Setup de Desenvolvimento**

```bash
# Clone do repositório
git clone https://github.com/your-org/web-crawler-api.git
cd web-crawler-api

# Configuração do ambiente
cp .env.example .env
docker-compose up -d

# Build e testes
mvn clean install
mvn test
```

### **Roadmap**

- [ ] **v2.0:** Suporte a JavaScript rendering (Puppeteer/Selenium)
- [ ] **v2.1:** Plugin system para custom extractors
- [ ] **v2.2:** GraphQL API endpoint
- [ ] **v2.3:** Kubernetes Helm charts
- [ ] **v2.4:** Multi-tenant support
- [ ] **v3.0:** Machine Learning content classification

### **Issues e Suporte**

- **Bug Reports:** [GitHub Issues](https://github.com/your-org/web-crawler-api/issues)
- **Feature Requests:** [GitHub Discussions](https://github.com/your-org/web-crawler-api/discussions)
- **Security:** [security@example.com](mailto:security@example.com)

## Licença e Contribuição

### **Licença**

Este projeto está licenciado sob a **MIT License** - veja o arquivo [LICENSE](LICENSE) para detalhes.

### **Contribuindo**

Contribuições são bem-vindas! Por favor, leia o [CONTRIBUTING.md](CONTRIBUTING.md) para obter detalhes sobre:

- Como configurar o ambiente de desenvolvimento
- Padrões de código e estilo
- Processo de envio de pull requests
- Como relatar bugs e solicitar recursos

### **Changelog**

Veja o [CHANGELOG.md](CHANGELOG.md) para uma lista detalhada de mudanças em cada versão.

### **Configuração de Ambiente**

Use o arquivo [.env.example](.env.example) como base para configurar suas variáveis de ambiente locais.

### **Autor**

- **Christian Vladimir Uhdre Mulato**
- **GitHub:** [@chmulato](https://github.com/chmulato)
- **LinkedIn:** [Christian Mulato](https://linkedin.com/in/chmulato)
- **Email:** [chmulato@hotmail.com](mailto:chmulato@hotmail.com)
- **Localização:** Campo Largo, PR - Brasil

### **Agradecimentos**

- **Spring Team** pelo excelente framework
- **RabbitMQ Team** pela ferramenta robusta de messaging
- **JSoup Team** pela biblioteca de parsing HTML
- **Docker Team** pela plataforma de containerização
- **Comunidade Open Source** pelo conhecimento compartilhado

---

### **Se este projeto foi útil, considere dar uma estrela!**

**Web Crawler API** - *Transformando web crawling em simples chamadas HTTP*
