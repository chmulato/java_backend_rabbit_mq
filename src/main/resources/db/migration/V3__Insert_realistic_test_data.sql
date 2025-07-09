-- Massa de dados mais realista para demonstração
-- V3__Insert_realistic_test_data.sql

-- Limpar dados de teste existentes (apenas em desenvolvimento)
DELETE FROM visited_urls WHERE task_id IN ('sample01', 'sample02');
DELETE FROM crawl_results WHERE task_id IN ('sample01', 'sample02');
DELETE FROM crawl_tasks WHERE id IN ('sample01', 'sample02');

-- ======================================
-- CENÁRIO 1: Tarefa concluída com sucesso
-- ======================================
INSERT INTO crawl_tasks (id, keyword, base_url, status, start_time, end_time, total_pages_visited, total_urls_found, created_at, updated_at) 
VALUES ('abcd1234', 'security', 'http://hiring.axreng.com', 'done', 
        DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 
        DATEADD('MINUTE', -90, CURRENT_TIMESTAMP), 
        45, 8, 
        DATEADD('HOUR', -2, CURRENT_TIMESTAMP), 
        DATEADD('MINUTE', -90, CURRENT_TIMESTAMP));

-- URLs encontradas para 'security'
INSERT INTO crawl_results (task_id, url, found_at) 
VALUES 
('abcd1234', 'http://hiring.axreng.com/security-overview.html', DATEADD('MINUTE', -115, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/docs/security-guidelines.html', DATEADD('MINUTE', -110, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/api/security', DATEADD('MINUTE', -105, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/security/best-practices.html', DATEADD('MINUTE', -100, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/blog/security-tips', DATEADD('MINUTE', -98, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/security-policy.pdf', DATEADD('MINUTE', -95, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/team/security-expert.html', DATEADD('MINUTE', -93, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/security/audit-report.html', DATEADD('MINUTE', -90, CURRENT_TIMESTAMP));

-- ======================================
-- CENÁRIO 2: Tarefa ativa em andamento
-- ======================================
INSERT INTO crawl_tasks (id, keyword, base_url, status, start_time, total_pages_visited, total_urls_found, created_at, updated_at) 
VALUES ('efgh5678', 'privacy', 'http://hiring.axreng.com', 'active', 
        DATEADD('MINUTE', -30, CURRENT_TIMESTAMP), 
        NULL, 
        15, 3, 
        DATEADD('MINUTE', -30, CURRENT_TIMESTAMP), 
        DATEADD('MINUTE', -5, CURRENT_TIMESTAMP));

-- URLs encontradas para 'privacy' (em progresso)
INSERT INTO crawl_results (task_id, url, found_at) 
VALUES 
('efgh5678', 'http://hiring.axreng.com/privacy-policy.html', DATEADD('MINUTE', -25, CURRENT_TIMESTAMP)),
('efgh5678', 'http://hiring.axreng.com/docs/privacy-compliance.html', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
('efgh5678', 'http://hiring.axreng.com/legal/privacy-terms.html', DATEADD('MINUTE', -15, CURRENT_TIMESTAMP));

-- ======================================
-- CENÁRIO 3: Tarefa concluída sem resultados
-- ======================================
INSERT INTO crawl_tasks (id, keyword, base_url, status, start_time, end_time, total_pages_visited, total_urls_found, created_at, updated_at) 
VALUES ('ijkl9012', 'blockchain', 'http://hiring.axreng.com', 'done', 
        DATEADD('HOUR', -6, CURRENT_TIMESTAMP), 
        DATEADD('HOUR', -5, CURRENT_TIMESTAMP), 
        35, 0, 
        DATEADD('HOUR', -6, CURRENT_TIMESTAMP), 
        DATEADD('HOUR', -5, CURRENT_TIMESTAMP));

-- ======================================
-- CENÁRIO 4: Tarefa com muitos resultados
-- ======================================
INSERT INTO crawl_tasks (id, keyword, base_url, status, start_time, end_time, total_pages_visited, total_urls_found, created_at, updated_at) 
VALUES ('mnop3456', 'test', 'http://hiring.axreng.com', 'done', 
        DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 
        DATEADD('MINUTE', -30, CURRENT_TIMESTAMP), 
        28, 12, 
        DATEADD('HOUR', -1, CURRENT_TIMESTAMP), 
        DATEADD('MINUTE', -30, CURRENT_TIMESTAMP));

-- URLs encontradas para 'test' (muitos resultados)
INSERT INTO crawl_results (task_id, url, found_at) 
VALUES 
('mnop3456', 'http://hiring.axreng.com/test-suite.html', DATEADD('MINUTE', -55, CURRENT_TIMESTAMP)),
('mnop3456', 'http://hiring.axreng.com/testing/unit-tests.html', DATEADD('MINUTE', -52, CURRENT_TIMESTAMP)),
('mnop3456', 'http://hiring.axreng.com/qa/test-automation.html', DATEADD('MINUTE', -50, CURRENT_TIMESTAMP)),
('mnop3456', 'http://hiring.axreng.com/docs/testing-guide.html', DATEADD('MINUTE', -48, CURRENT_TIMESTAMP)),
('mnop3456', 'http://hiring.axreng.com/api/test-endpoints', DATEADD('MINUTE', -45, CURRENT_TIMESTAMP)),
('mnop3456', 'http://hiring.axreng.com/test-results.html', DATEADD('MINUTE', -43, CURRENT_TIMESTAMP)),
('mnop3456', 'http://hiring.axreng.com/testing/integration-tests.html', DATEADD('MINUTE', -40, CURRENT_TIMESTAMP)),
('mnop3456', 'http://hiring.axreng.com/test-coverage-report.html', DATEADD('MINUTE', -38, CURRENT_TIMESTAMP)),
('mnop3456', 'http://hiring.axreng.com/performance-test.html', DATEADD('MINUTE', -35, CURRENT_TIMESTAMP)),
('mnop3456', 'http://hiring.axreng.com/test-data.json', DATEADD('MINUTE', -33, CURRENT_TIMESTAMP)),
('mnop3456', 'http://hiring.axreng.com/testing/load-test.html', DATEADD('MINUTE', -32, CURRENT_TIMESTAMP)),
('mnop3456', 'http://hiring.axreng.com/test-environment.html', DATEADD('MINUTE', -30, CURRENT_TIMESTAMP));

-- ======================================
-- URLs visitadas (amostra representativa)
-- ======================================

-- Para tarefa 'security' (abcd1234)
INSERT INTO visited_urls (task_id, url, visited_at) 
VALUES 
('abcd1234', 'http://hiring.axreng.com', DATEADD('MINUTE', -120, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/index.html', DATEADD('MINUTE', -118, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/about.html', DATEADD('MINUTE', -116, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/security-overview.html', DATEADD('MINUTE', -115, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/docs/security-guidelines.html', DATEADD('MINUTE', -110, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/contact.html', DATEADD('MINUTE', -108, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/api/security', DATEADD('MINUTE', -105, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/news.html', DATEADD('MINUTE', -103, CURRENT_TIMESTAMP)),
('abcd1234', 'http://hiring.axreng.com/security/best-practices.html', DATEADD('MINUTE', -100, CURRENT_TIMESTAMP));

-- Para tarefa 'privacy' (efgh5678) - em andamento
INSERT INTO visited_urls (task_id, url, visited_at) 
VALUES 
('efgh5678', 'http://hiring.axreng.com', DATEADD('MINUTE', -30, CURRENT_TIMESTAMP)),
('efgh5678', 'http://hiring.axreng.com/index.html', DATEADD('MINUTE', -28, CURRENT_TIMESTAMP)),
('efgh5678', 'http://hiring.axreng.com/privacy-policy.html', DATEADD('MINUTE', -25, CURRENT_TIMESTAMP)),
('efgh5678', 'http://hiring.axreng.com/about.html', DATEADD('MINUTE', -23, CURRENT_TIMESTAMP)),
('efgh5678', 'http://hiring.axreng.com/docs/privacy-compliance.html', DATEADD('MINUTE', -20, CURRENT_TIMESTAMP)),
('efgh5678', 'http://hiring.axreng.com/legal/privacy-terms.html', DATEADD('MINUTE', -15, CURRENT_TIMESTAMP));

-- Para tarefa 'blockchain' (ijkl9012) - sem resultados
INSERT INTO visited_urls (task_id, url, visited_at) 
VALUES 
('ijkl9012', 'http://hiring.axreng.com', DATEADD('HOUR', -6, CURRENT_TIMESTAMP)),
('ijkl9012', 'http://hiring.axreng.com/index.html', DATEADD('MINUTE', -350, CURRENT_TIMESTAMP)),
('ijkl9012', 'http://hiring.axreng.com/about.html', DATEADD('MINUTE', -345, CURRENT_TIMESTAMP)),
('ijkl9012', 'http://hiring.axreng.com/services.html', DATEADD('MINUTE', -340, CURRENT_TIMESTAMP)),
('ijkl9012', 'http://hiring.axreng.com/contact.html', DATEADD('MINUTE', -335, CURRENT_TIMESTAMP));
