# Massa de Dados - Web Crawler API

## Estrutura do Banco de Dados

### Tabelas Principais

#### `crawl_tasks`

Armazena informações das tarefas de crawling.

```markdown
|---------------------------|--------------|---------------------------------------------|
| Campo                     | Tipo         | Descrição                                   |
|---------------------------|--------------|---------------------------------------------|
| `id`                      | VARCHAR(8)   | ID único da tarefa (gerado automaticamente) |
| `keyword`                 | VARCHAR(32)  | Palavra-chave sendo buscada                 |
| `base_url`                | VARCHAR(500) | URL base para o crawling                    |
| `status`                  | VARCHAR(20)  | Status: 'active' ou 'done'                  |
| `start_time`              | TIMESTAMP    | Data/hora de início                         |
| `end_time`                | TIMESTAMP    | Data/hora de conclusão (NULL se ativa)      |
| `total_pages_visited`     | INT          | Total de páginas visitadas                  |
| `total_urls_found`        | INT          | Total de URLs com a palavra-chave           |
| `created_at`              | TIMESTAMP    | Data de criação do registro                 |
| `updated_at`              | TIMESTAMP    | Data da última atualização                  |
|---------------------------|--------------|---------------------------------------------|
```

#### `crawl_results`

Armazena URLs onde a palavra-chave foi encontrada.

```markdown
|------------|---------------|-----------------------------------|
| Campo      | Tipo          | Descrição                         |
|------------|---------------|-----------------------------------|
| `id`       | BIGINT        | ID auto-incremento                |
| `task_id`  | VARCHAR(8)    | Referência para `crawl_tasks.id`  |
| `url`      | VARCHAR(1000) | URL onde a palavra foi encontrada |
| `found_at` | TIMESTAMP     | Data/hora da descoberta           |
|------------|---------------|-----------------------------------|
```

#### `visited_urls`

Controla URLs já visitadas para evitar loops.

```markdown
|--------------|---------------|-----------------------------------|
| Campo        | Tipo          | Descrição                         |
|--------------|---------------|-----------------------------------|
| `id`         | BIGINT        | ID auto-incremento                |
| `task_id`    | VARCHAR(8)    | Referência para `crawl_tasks.id`  |
| `url`        | VARCHAR(1000) | URL visitada                      |
| `visited_at` | TIMESTAMP     | Data/hora da visita               |
|--------------|---------------|-----------------------------------|
```

## Dados de Exemplo

### Cenários Incluídos

#### 1. Tarefa Concluída com Sucesso

- **ID**: `abcd1234`
- **Keyword**: `security`
- **Status**: `done`
- **Resultados**: 8 URLs encontradas
- **Páginas visitadas**: 45

#### 2. Tarefa Ativa (Em Progresso)

- **ID**: `efgh5678`
- **Keyword**: `privacy`
- **Status**: `active`
- **Resultados**: 3 URLs encontradas (até agora)
- **Páginas visitadas**: 15

#### 3. Tarefa sem Resultados

- **ID**: `ijkl9012`
- **Keyword**: `blockchain`
- **Status**: `done`
- **Resultados**: 0 URLs encontradas
- **Páginas visitadas**: 35

#### 4. Tarefa com Muitos Resultados

- **ID**: `mnop3456`
- **Keyword**: `test`
- **Status**: `done`
- **Resultados**: 12 URLs encontradas
- **Páginas visitadas**: 28

## Migrações Flyway

### V1__Create_crawl_tables.sql

- Cria as tabelas principais
- Define índices para performance
- Estabelece foreign keys

### V2__Insert_test_data.sql

- Dados básicos para desenvolvimento
- 2 tarefas de exemplo

### V3__Insert_realistic_test_data.sql

- Massa de dados mais realista
- 4 cenários diferentes
- Timestamps relativos (baseados no momento atual)

## Dados Adicionais

### sample-data.sql

Dados extras para desenvolvimento local:

- Mais cenários de teste
- Dados históricos
- Exemplos de performance

**Como usar:**

1. Acesse H2 Console: [http://localhost:4567/h2-console](http://localhost:4567/h2-console)
2. Execute o conteúdo do arquivo manualmente

## Consultas Úteis

### Listar todas as tarefas

```sql
SELECT id, keyword, status, start_time, end_time, 
       total_pages_visited, total_urls_found 
FROM crawl_tasks 
ORDER BY start_time DESC;
```

### Resultados de uma tarefa específica

```sql
SELECT url, found_at 
FROM crawl_results 
WHERE task_id = 'abcd1234' 
ORDER BY found_at;
```

### Estatísticas por keyword

```sql
SELECT keyword, 
       COUNT(*) as total_searches,
       AVG(total_pages_visited) as avg_pages,
       AVG(total_urls_found) as avg_results
FROM crawl_tasks 
GROUP BY keyword 
ORDER BY total_searches DESC;
```

### Tarefas ativas

```sql
SELECT id, keyword, start_time, total_pages_visited, total_urls_found 
FROM crawl_tasks 
WHERE status = 'active' 
ORDER BY start_time;
```

### URLs mais encontradas

```sql
SELECT url, COUNT(*) as frequency 
FROM crawl_results 
GROUP BY url 
HAVING COUNT(*) > 1 
ORDER BY frequency DESC;
```

## Acesso ao H2 Console

Durante desenvolvimento:

1. **URL**: http://localhost:4567/h2-console
2. **JDBC URL**: `jdbc:h2:mem:testdb`
3. **User Name**: `sa`
4. **Password**: (deixe vazio)

## Ferramentas

### Script de Gerenciamento

```bash
# Tornar executável
chmod +x manage-data.sh

# Mostrar ajuda
./manage-data.sh help

# Listar tarefas
./manage-data.sh show-tasks

# Ver estatísticas
./manage-data.sh show-stats
```

## Desenvolvimento Local

### Para adicionar novos dados:

1. Crie uma nova migração: `V4__Add_more_data.sql`
2. Ou use `sample-data.sql` para dados temporários
3. Reinicie a aplicação para aplicar migrações

### Para resetar dados:

```sql
DELETE FROM visited_urls;
DELETE FROM crawl_results;
DELETE FROM crawl_tasks;
```

Depois reinicie a aplicação para recriar via Flyway.

## Considerações de Performance

- Índices criados em campos frequentemente consultados
- Foreign keys com CASCADE DELETE
- URLs limitadas a 1000 caracteres
- Unique constraint em `visited_urls` para evitar duplicatas

## UTF-8

Todas as migrações e dados estão configurados para UTF-8:

- Suporte a caracteres especiais em URLs
- Keywords em diferentes idiomas
- Timestamps com timezone apropriado
