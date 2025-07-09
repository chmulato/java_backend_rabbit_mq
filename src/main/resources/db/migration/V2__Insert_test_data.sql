-- Dados de teste para desenvolvimento
-- Inserir algumas tarefas de exemplo (apenas para ambiente de desenvolvimento)

-- Tarefa concluída
INSERT INTO crawl_tasks (id, keyword, base_url, status, start_time, end_time, total_pages_visited, total_urls_found, created_at, updated_at) 
VALUES ('sample01', 'security', 'http://example.com', 'done', '2025-01-01 10:00:00', '2025-01-01 10:05:00', 25, 3, '2025-01-01 10:00:00', '2025-01-01 10:05:00');

-- URLs encontradas para a tarefa sample01
INSERT INTO crawl_results (task_id, url, found_at) 
VALUES 
('sample01', 'http://example.com/security', '2025-01-01 10:01:00'),
('sample01', 'http://example.com/security-policy', '2025-01-01 10:02:00'),
('sample01', 'http://example.com/docs/security', '2025-01-01 10:03:00');

-- URLs visitadas para a tarefa sample01
INSERT INTO visited_urls (task_id, url, visited_at) 
VALUES 
('sample01', 'http://example.com', '2025-01-01 10:00:30'),
('sample01', 'http://example.com/about', '2025-01-01 10:01:30'),
('sample01', 'http://example.com/security', '2025-01-01 10:01:00'),
('sample01', 'http://example.com/security-policy', '2025-01-01 10:02:00'),
('sample01', 'http://example.com/docs/security', '2025-01-01 10:03:00');

-- Tarefa ativa de exemplo
INSERT INTO crawl_tasks (id, keyword, base_url, status, start_time, total_pages_visited, total_urls_found, created_at, updated_at) 
VALUES ('sample02', 'privacy', 'http://example.org', 'active', '2025-01-01 11:00:00', 10, 1, '2025-01-01 11:00:00', '2025-01-01 11:00:00');

-- URL encontrada para a tarefa sample02
INSERT INTO crawl_results (task_id, url, found_at) 
VALUES 
('sample02', 'http://example.org/privacy-policy', '2025-01-01 11:01:00');

-- URLs visitadas para a tarefa sample02
INSERT INTO visited_urls (task_id, url, visited_at) 
VALUES 
('sample02', 'http://example.org', '2025-01-01 11:00:30'),
('sample02', 'http://example.org/privacy-policy', '2025-01-01 11:01:00');
