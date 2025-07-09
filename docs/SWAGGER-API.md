# Documentação da API com OpenAPI/Swagger

Este documento descreve como utilizar a documentação interativa da API gerada com **SpringDoc OpenAPI 3.0**.

## Acessos

Após iniciar a aplicação (`docker-compose up` ou `mvn spring-boot:run`):

- **Swagger UI**: <http://localhost:4567/swagger-ui.html>
- **OpenAPI Spec**: <http://localhost:4567/api-docs>
- **OpenAPI JSON**: <http://localhost:4567/api-docs.yaml>

## Funcionalidades da Documentação

### Exploração Interativa

O **Swagger UI** oferece:

- **Interface visual** para todos os endpoints
- **Exemplos de requisições** e respostas
- **Teste direto** dos endpoints ("Try it out")
- **Validação automática** dos parâmetros
- **Esquemas detalhados** dos modelos de dados

### Endpoints Documentados

#### **POST /crawl**

- **Descrição**: Inicia nova busca de crawling
- **Parâmetros**: `keyword` (4-32 caracteres)
- **Resposta**: ID único da tarefa (`abc12345`)

#### **GET /crawl/{id}**

- **Descrição**: Consulta resultados de uma busca
- **Parâmetros**: `id` (ID da tarefa)
- **Resposta**: Status e URLs encontradas

#### **GET /crawl/{id}/stats**

- **Descrição**: Estatísticas detalhadas da busca
- **Parâmetros**: `id` (ID da tarefa)
- **Resposta**: Métricas completas da execução

#### **GET /crawl/active**

- **Descrição**: Lista tarefas em execução
- **Resposta**: Array de tarefas ativas

## Como Testar a API

### 1. Usar Swagger UI (Recomendado)

1. Acesse <http://localhost:4567/swagger-ui.html>
2. Expanda o endpoint desejado
3. Clique em **"Try it out"**
4. Preencha os parâmetros necessários
5. Clique em **"Execute"**
6. Veja a resposta em tempo real

### 2. Usar cURL

```bash
# Iniciar busca
curl -X POST "http://localhost:4567/crawl" \
     -H "Content-Type: application/json" \
     -d '{"keyword": "security"}'

# Consultar resultado
curl -X GET "http://localhost:4567/crawl/abc12345"

# Ver estatísticas
curl -X GET "http://localhost:4567/crawl/abc12345/stats"

# Listar tarefas ativas
curl -X GET "http://localhost:4567/crawl/active"
```

### 3. Usar Postman/Insomnia

1. Importe a especificação OpenAPI de: <http://localhost:4567/api-docs>
2. Todos os endpoints serão criados automaticamente
3. Exemplos e esquemas incluídos

## Exemplos de Dados

### Requisição de Busca

```json
{
  "keyword": "security"
}
```

### Resposta de Início

```json
{
  "id": "abc12345"
}
```

### Resposta de Resultados

```json
{
  "id": "abc12345",
  "status": "active",
  "urls": [
    "http://hiring.axreng.com/index2.html",
    "http://hiring.axreng.com/htmlman1/chcon.1.html"
  ]
}
```

### Resposta de Estatísticas

```json
{
  "id": "abc12345",
  "keyword": "security",
  "status": "done",
  "total_urls_found": 15,
  "total_pages_processed": 50,
  "start_time": "2025-07-09T12:00:00",
  "end_time": "2025-07-09T12:05:30"
}
```

## Validações e Restrições

### Keyword

- **Mínimo**: 4 caracteres
- **Máximo**: 32 caracteres
- **Não pode ser vazio**: Campo obrigatório
- **Formato**: Qualquer string válida

### ID da Tarefa

- **Formato**: String alfanumérica
- **Tamanho**: Exatamente 8 caracteres
- **Geração**: Automática pelo sistema

### Status da Busca

- **`active`**: Busca em andamento
- **`done`**: Busca concluída

## Configurações OpenAPI

As configurações estão definidas em:

### application.yml

```yaml
springdoc:
  api-docs:
    path: /api-docs
    enabled: true
  swagger-ui:
    path: /swagger-ui.html
    enabled: true
    try-it-out-enabled: true
    operations-sorter: method
    tags-sorter: alpha
```

### OpenApiConfig.java

- **Informações da API**: Título, descrição, versão
- **Contato do desenvolvedor**: Christian Vladimir Uhdre Mulato
- **Servidores**: Local e configurado
- **Licença**: MIT License

## Recursos Avançados

### Modelos de Dados

Todos os DTOs possuem anotações `@Schema` com:

- **Descrições detalhadas** de cada campo
- **Exemplos práticos** de valores
- **Validações** e restrições
- **Tipos de dados** explícitos

### Respostas HTTP

Cada endpoint documenta:

- **Códigos de status** possíveis (200, 400, 404, 500)
- **Exemplos de respostas** para cada cenário
- **Content-Type** apropriado
- **Estrutura dos dados** retornados

### Testabilidade

- **Try it out**: Teste direto na interface
- **Validação**: Campos obrigatórios destacados
- **Autocomplete**: Sugestões de valores
- **Formatação**: JSON pretty-print automático

## Benefícios para Desenvolvimento

### **Produtividade**

- Teste imediato de alterações na API
- Validação automática de contratos
- Exemplos sempre atualizados

### **Debugging**

- Visualização clara de requests/responses
- Códigos de erro explicados
- Rastreamento de parâmetros

### **Colaboração**

- Documentação sempre sincronizada com código
- Interface intuitiva para não-desenvolvedores
- Especificação padrão da indústria

### **Integração**

- Export para Postman/Insomnia
- Geração de clients automática
- Compatibilidade com ferramentas OpenAPI

---

**Desenvolvido por:** Christian Vladimir Uhdre Mulato  
**Empresa:** Axur  
**Teste Técnico:** Desenvolvedor Java Sênior
