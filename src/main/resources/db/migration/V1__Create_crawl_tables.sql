-- Tabela para armazenar as tarefas de crawling
CREATE TABLE crawl_tasks (
    id VARCHAR(8) PRIMARY KEY,
    keyword VARCHAR(32) NOT NULL,
    base_url VARCHAR(500) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'active',
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_time TIMESTAMP NULL,
    total_pages_visited INT DEFAULT 0,
    total_urls_found INT DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Tabela para armazenar as URLs encontradas
CREATE TABLE crawl_results (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(8) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    found_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES crawl_tasks(id) ON DELETE CASCADE
);

-- Tabela para armazenar as URLs visitadas (para evitar duplicatas)
CREATE TABLE visited_urls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    task_id VARCHAR(8) NOT NULL,
    url VARCHAR(1000) NOT NULL,
    visited_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (task_id) REFERENCES crawl_tasks(id) ON DELETE CASCADE
);

-- Criar índice único separadamente
CREATE UNIQUE INDEX unique_task_url ON visited_urls(task_id, url);

-- Índices para performance
CREATE INDEX idx_crawl_tasks_status ON crawl_tasks(status);
CREATE INDEX idx_crawl_tasks_start_time ON crawl_tasks(start_time);
CREATE INDEX idx_crawl_results_task_id ON crawl_results(task_id);
CREATE INDEX idx_crawl_results_found_at ON crawl_results(found_at);
CREATE INDEX idx_visited_urls_task_id ON visited_urls(task_id);
