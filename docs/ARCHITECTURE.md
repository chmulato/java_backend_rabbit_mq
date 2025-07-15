# Arquitetura do Sistema Web Crawler API

## Visão Geral

A Web Crawler API é uma aplicação Java que implementa um web crawler assíncrono para busca de termos em páginas web. A arquitetura foi projetada para ser escalável, resiliente e de alta performance, utilizando conceitos modernos de desenvolvimento de software como microsserviços, processamento assíncrono e design orientado a domínio.

## Arquitetura de Alto Nível

O sistema é composto pelos seguintes componentes principais:

```
┌───────────────────┐     ┌───────────────────┐     ┌───────────────────┐
│                   │     │                   │     │                   │
│  API HTTP (REST)  │────▶│   Message Broker  │────▶│  Web Crawler      │
│                   │     │   (RabbitMQ)      │     │  Worker           │
│                   │     │                   │     │                   │
└─────────┬─────────┘     └───────────────────┘     └─────────┬─────────┘
          │                                                   │
          │                                                   │
          ▼                                                   ▼
    ┌───────────────────────────────────────────────────────────────────┐
    │                                                                   │
    │                      Banco de Dados (H2)                          │
    │                                                                   │
    └───────────────────────────────────────────────────────────────────┘
```

## Camadas da Aplicação

A aplicação segue uma arquitetura em camadas bem definida:

### 1. Camada de Apresentação (API)

- **Controladores REST**: Endpoints HTTP para interação com o sistema
- **DTOs e Validadores**: Objetos de transferência de dados e validação de entrada
- **Mapeamento de Exceções**: Tratamento centralizado de erros e exceções
- **Documentação Swagger/OpenAPI**: Documentação automática da API

### 2. Camada de Aplicação (Serviços)

- **Serviços de Negócio**: Implementação das regras de negócio
- **Serviços de Mensageria**: Produtores e consumidores de mensagens
- **Serviços de Persistência**: Operações de banco de dados
- **Serviços de Utilidades**: Helpers e utilitários

### 3. Camada de Domínio (Modelo)

- **Entidades**: Representação dos objetos de domínio
- **Modelos**: Objetos de valor e modelos de negócio
- **Especificações**: Regras de negócio reusáveis
- **Eventos**: Eventos de domínio para comunicação entre componentes

### 4. Camada de Infraestrutura

- **Repositórios**: Implementações de persistência
- **Configurações**: Configurações de infraestrutura (banco, mensageria, etc.)
- **Integrações**: Clientes HTTP e outras integrações
- **Adaptadores**: Adaptadores para serviços externos

## Fluxo de Processamento

O fluxo de processamento principal da aplicação segue estas etapas:

1. **Recebimento da Requisição**:
   - Cliente envia requisição HTTP POST para `/crawl` com termo de busca
   - Validação do termo de busca (min 4, max 32 caracteres)
   - Geração de ID único para a busca (8 caracteres alfanuméricos)

2. **Enfileiramento da Tarefa**:
   - Criação de tarefa de busca no banco de dados
   - Envio de mensagem para fila RabbitMQ
   - Retorno imediato do ID para o cliente

3. **Processamento Assíncrono**:
   - Worker consome mensagem da fila
   - Execução do processo de web crawling
   - Atualização incremental dos resultados no banco

4. **Consulta de Resultados**:
   - Cliente consulta resultados via GET `/crawl/{id}`
   - Retorno dos URLs encontrados e status da busca

## Componentes Principais

### API HTTP (REST)

A API HTTP expõe os endpoints para interação com o sistema:

- `POST /crawl` - Inicia uma nova busca
- `GET /crawl/{id}` - Consulta os resultados de uma busca

### Message Broker (RabbitMQ)

O RabbitMQ é utilizado como broker de mensagens para processamento assíncrono:

- **Exchange**: `crawler.exchange` (tipo: direct)
- **Queue**: `crawler.tasks` (durável, com retry)
- **Binding**: `crawler.tasks` -> `crawler.exchange` com routing key `task`

### Web Crawler Worker

O worker é responsável por processar as tarefas de crawling:

- Consumo de mensagens da fila `crawler.tasks`
- Execução do algoritmo de crawling
- Processamento recursivo de URLs
- Atualização de resultados parciais

### Banco de Dados (H2)

O banco de dados armazena:

- **Tarefas de Crawling**: Informações sobre buscas em andamento/concluídas
- **Resultados de Crawling**: URLs encontrados em cada busca
- **URLs Visitados**: Cache de URLs já visitados para evitar reprocessamento

## Aspectos Técnicos

### Concorrência e Paralelismo

- Processamento paralelo de URLs com threadpools configuráveis
- Controle de concorrência nas operações de banco de dados
- Uso de estruturas de dados thread-safe

### Resiliência e Tolerância a Falhas

- Reinicialização automática de tarefas falhas
- Circuit breaker para chamadas HTTP externas
- Timeouts configuráveis para operações de longa duração

### Monitoramento e Observabilidade

- Endpoints de health check (Spring Actuator)
- Logging estruturado com correlação de IDs
- Métricas de performance e utilização de recursos

## Decisões de Arquitetura

### Escolha do RabbitMQ

O RabbitMQ foi escolhido como broker de mensagens devido a:

- Suporte robusto a diferentes padrões de mensageria
- Garantia de entrega de mensagens mesmo em caso de falhas
- Facilidade de integração com Spring AMQP
- Suporte a filas de prioridade e dead-letter queues

### Banco de Dados H2

O H2 foi escolhido como banco de dados para:

- Facilidade de desenvolvimento e testes
- Suporte a modo em memória e persistência em arquivo
- Compatibilidade com JPA/Hibernate
- Console web para administração e debug

Em ambiente de produção, poderia ser substituído por PostgreSQL ou MySQL.

### Arquitetura Orientada a Eventos

O sistema utiliza uma arquitetura orientada a eventos para:

- Desacoplamento entre componentes
- Escalabilidade horizontal
- Processamento assíncrono
- Maior resiliência a falhas

## Considerações de Escalabilidade

Para escalar o sistema horizontalmente, pode-se:

- Adicionar mais instâncias da aplicação (Spring Boot)
- Configurar cluster do RabbitMQ
- Implementar sharding no banco de dados
- Utilizar cache distribuído (Redis, Hazelcast)

## Diagrama de Sequência (Fluxo Principal)

```
┌─────┐          ┌─────┐          ┌──────────┐          ┌────────┐          ┌────────┐
│     │          │     │          │          │          │        │          │        │
│Client│          │ API │          │RabbitMQ  │          │Crawler │          │Database│
│     │          │     │          │          │          │        │          │        │
└──┬──┘          └──┬──┘          └────┬─────┘          └───┬────┘          └───┬────┘
   │                │                  │                    │                   │
   │ POST /crawl    │                  │                    │                   │
   │───────────────>│                  │                    │                   │
   │                │                  │                    │                   │
   │                │ Save task        │                    │                   │
   │                │──────────────────────────────────────────────────────────>│
   │                │                  │                    │                   │
   │                │ Send message     │                    │                   │
   │                │─────────────────>│                    │                   │
   │                │                  │                    │                   │
   │ Return ID      │                  │                    │                   │
   │<───────────────│                  │                    │                   │
   │                │                  │                    │                   │
   │                │                  │ Consume message    │                   │
   │                │                  │───────────────────>│                   │
   │                │                  │                    │                   │
   │                │                  │                    │ Read task         │
   │                │                  │                    │─────────────────> │
   │                │                  │                    │                   │
   │                │                  │                    │ Process URLs      │
   │                │                  │                    │─────────────────> │
   │                │                  │                    │                   │
   │                │                  │                    │ Update results    │
   │                │                  │                    │─────────────────> │
   │                │                  │                    │                   │
   │ GET /crawl/{id}│                  │                    │                   │
   │───────────────>│                  │                    │                   │
   │                │                  │                    │                   │
   │                │ Get results      │                    │                   │
   │                │──────────────────────────────────────────────────────────>│
   │                │                  │                    │                   │
   │ Return results │                  │                    │                   │
   │<───────────────│                  │                    │                   │
   │                │                  │                    │                   │
```

## Considerações Futuras

Para evolução do sistema, considera-se:

1. **Suporte a JavaScript**: Implementar um crawler que execute JavaScript para páginas dinâmicas
2. **API GraphQL**: Adicionar endpoint GraphQL para consultas mais flexíveis
3. **Escalabilidade**: Migrar para arquitetura de microsserviços com Kubernetes
4. **Machine Learning**: Incorporar análise semântica para melhorar resultados
5. **Indexação**: Integrar com Elasticsearch para pesquisa full-text dos conteúdos
