-- Script para inserir dados de desenvolvimento adicionais
-- Use este script para popular dados durante desenvolvimento local
-- Não será executado automaticamente pelo Flyway

-- ======================================
-- DADOS PARA DEMONSTRAÇÃO E TESTES
-- ======================================

-- Cenário: Busca por 'java' com resultados variados
INSERT INTO crawl_tasks (id, keyword, base_url, status, start_time, end_time, total_pages_visited, total_urls_found, created_at, updated_at) 
VALUES ('java1234', 'java', 'http://example.com', 'done', 
        DATEADD('DAY', -1, CURRENT_TIMESTAMP), 
        DATEADD('HOUR', -22, CURRENT_TIMESTAMP), 
        67, 15, 
        DATEADD('DAY', -1, CURRENT_TIMESTAMP), 
        DATEADD('HOUR', -22, CURRENT_TIMESTAMP));

INSERT INTO crawl_results (task_id, url, found_at) 
VALUES 
('java1234', 'http://hiring.axreng.com/careers/java-developer.html', DATEADD('HOUR', -23, CURRENT_TIMESTAMP)),
('java1234', 'http://hiring.axreng.com/tech/java-framework.html', DATEADD('HOUR', -23, CURRENT_TIMESTAMP)),
('java1234', 'http://hiring.axreng.com/docs/java-guidelines.html', DATEADD('HOUR', -23, CURRENT_TIMESTAMP)),
('java1234', 'http://hiring.axreng.com/training/java-course.html', DATEADD('HOUR', -23, CURRENT_TIMESTAMP)),
('java1234', 'http://hiring.axreng.com/blog/java-best-practices', DATEADD('HOUR', -22, CURRENT_TIMESTAMP));

-- Cenário: Busca por 'api' em progresso
INSERT INTO crawl_tasks (id, keyword, base_url, status, start_time, total_pages_visited, total_urls_found, created_at, updated_at) 
VALUES ('api56789', 'api', 'http://hiring.axreng.com', 'active', 
        DATEADD('MINUTE', -45, CURRENT_TIMESTAMP), 
        NULL, 
        20, 7, 
        DATEADD('MINUTE', -45, CURRENT_TIMESTAMP), 
        DATEADD('MINUTE', -2, CURRENT_TIMESTAMP));

INSERT INTO crawl_results (task_id, url, found_at) 
VALUES 
('api56789', 'http://hiring.axreng.com/api/documentation', DATEADD('MINUTE', -40, CURRENT_TIMESTAMP)),
('api56789', 'http://hiring.axreng.com/api/v1/users', DATEADD('MINUTE', -35, CURRENT_TIMESTAMP)),
('api56789', 'http://hiring.axreng.com/docs/api-guide.html', DATEADD('MINUTE', -30, CURRENT_TIMESTAMP)),
('api56789', 'http://hiring.axreng.com/api/v2/products', DATEADD('MINUTE', -25, CURRENT_TIMESTAMP)),
('api56789', 'http://hiring.axreng.com/api-examples.html', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
('api56789', 'http://hiring.axreng.com/restful-api.html', DATEADD('MINUTE', -15, CURRENT_TIMESTAMP)),
('api56789', 'http://hiring.axreng.com/api/authentication', DATEADD('MINUTE', -10, CURRENT_TIMESTAMP));

-- ======================================
-- DADOS HISTÓRICOS (simulando uso ao longo do tempo)
-- ======================================

-- Busca antiga por 'spring'
INSERT INTO crawl_tasks (id, keyword, base_url, status, start_time, end_time, total_pages_visited, total_urls_found, created_at, updated_at) 
VALUES ('spr12345', 'spring', 'http://hiring.axreng.com', 'done', 
        DATEADD('DAY', -7, CURRENT_TIMESTAMP), 
        DATEADD('DAY', -7, DATEADD('MINUTE', 45, CURRENT_TIMESTAMP)), 
        55, 9, 
        DATEADD('DAY', -7, CURRENT_TIMESTAMP), 
        DATEADD('DAY', -7, DATEADD('MINUTE', 45, CURRENT_TIMESTAMP)));

INSERT INTO crawl_results (task_id, url, found_at) 
VALUES 
('spr12345', 'http://hiring.axreng.com/tech/spring-boot.html', DATEADD('DAY', -7, DATEADD('MINUTE', 10, CURRENT_TIMESTAMP))),
('spr12345', 'http://hiring.axreng.com/docs/spring-security.html', DATEADD('DAY', -7, DATEADD('MINUTE', 15, CURRENT_TIMESTAMP))),
('spr12345', 'http://hiring.axreng.com/spring-framework-guide.html', DATEADD('DAY', -7, DATEADD('MINUTE', 20, CURRENT_TIMESTAMP))),
('spr12345', 'http://hiring.axreng.com/tutorials/spring-mvc.html', DATEADD('DAY', -7, DATEADD('MINUTE', 25, CURRENT_TIMESTAMP)));

-- ======================================
-- DADOS PARA TESTE DE PERFORMANCE
-- ======================================

-- Tarefa com muitas páginas visitadas
INSERT INTO crawl_tasks (id, keyword, base_url, status, start_time, end_time, total_pages_visited, total_urls_found, created_at, updated_at) 
VALUES ('perf1234', 'performance', 'http://hiring.axreng.com', 'done', 
        DATEADD('HOUR', -3, CURRENT_TIMESTAMP), 
        DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 
        150, 25, 
        DATEADD('HOUR', -3, CURRENT_TIMESTAMP), 
        DATEADD('HOUR', -2, CURRENT_TIMESTAMP));

-- Apenas algumas URLs de exemplo para performance (não todas as 150)
INSERT INTO crawl_results (task_id, url, found_at) 
VALUES 
('perf1234', 'http://hiring.axreng.com/performance-testing.html', DATEADD('HOUR', -3, DATEADD('MINUTE', 10, CURRENT_TIMESTAMP))),
('perf1234', 'http://hiring.axreng.com/optimization/performance.html', DATEADD('HOUR', -3, DATEADD('MINUTE', 15, CURRENT_TIMESTAMP))),
('perf1234', 'http://hiring.axreng.com/monitoring/performance-metrics.html', DATEADD('HOUR', -3, DATEADD('MINUTE', 20, CURRENT_TIMESTAMP)));

-- ======================================
-- QUERIES ÚTEIS PARA DESENVOLVIMENTO
-- ======================================

/*
-- Ver todas as tarefas ordenadas por data
SELECT id, keyword, status, start_time, end_time, total_pages_visited, total_urls_found 
FROM crawl_tasks 
ORDER BY start_time DESC;

-- Ver URLs encontradas para uma tarefa específica
SELECT cr.url, cr.found_at 
FROM crawl_results cr 
WHERE cr.task_id = 'abcd1234' 
ORDER BY cr.found_at;

-- Estatísticas por keyword
SELECT keyword, COUNT(*) as total_searches, 
       AVG(total_pages_visited) as avg_pages, 
       AVG(total_urls_found) as avg_results
FROM crawl_tasks 
GROUP BY keyword 
ORDER BY total_searches DESC;

-- Tarefas ativas
SELECT id, keyword, start_time, total_pages_visited, total_urls_found 
FROM crawl_tasks 
WHERE status = 'active' 
ORDER BY start_time;

-- Top URLs mais encontradas
SELECT url, COUNT(*) as frequency 
FROM crawl_results 
GROUP BY url 
HAVING COUNT(*) > 1 
ORDER BY frequency DESC;
*/
