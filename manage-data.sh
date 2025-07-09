#!/bin/bash

# Script para gerenciar massa de dados do projeto Axur Web Crawler
# Uso: ./manage-data.sh [command]

DB_URL="jdbc:h2:mem:testdb"
DB_USER="sa"
DB_PASS=""

show_help() {
    echo "Gerenciador de Massa de Dados - Axur Web Crawler"
    echo ""
    echo "Comandos disponíveis:"
    echo "  show-tasks      - Listar todas as tarefas"
    echo "  show-results    - Listar resultados por tarefa"
    echo "  show-stats      - Mostrar estatísticas gerais"
    echo "  load-sample     - Carregar dados de exemplo adicionais"
    echo "  clear-data      - Limpar todos os dados (cuidado!)"
    echo "  help            - Mostrar esta ajuda"
    echo ""
    echo "Exemplos:"
    echo "  ./manage-data.sh show-tasks"
    echo "  ./manage-data.sh show-results abcd1234"
    echo "  ./manage-data.sh load-sample"
}

show_tasks() {
    echo "=== TAREFAS DE CRAWLING ==="
    echo "ID       | Keyword   | Status | Páginas | URLs | Data/Hora Início"
    echo "---------|-----------|--------|---------|------|------------------"
    
    # Aqui você conectaria ao banco H2 e executaria:
    # SELECT id, keyword, status, total_pages_visited, total_urls_found, start_time FROM crawl_tasks ORDER BY start_time DESC;
    
    echo "Nota: Para executar consultas reais, use o console H2 em http://localhost:4567/h2-console"
    echo "ou implemente conexão JDBC neste script."
}

show_results() {
    local task_id=$1
    if [ -z "$task_id" ]; then
        echo "Uso: $0 show-results <task_id>"
        echo "Exemplo: $0 show-results abcd1234"
        return 1
    fi
    
    echo "=== RESULTADOS PARA TAREFA: $task_id ==="
    echo "URL                                          | Data/Hora Encontrada"
    echo "---------------------------------------------|---------------------"
    
    # Aqui você conectaria ao banco e executaria:
    # SELECT url, found_at FROM crawl_results WHERE task_id = '$task_id' ORDER BY found_at;
    
    echo "Execute no H2 Console:"
    echo "SELECT url, found_at FROM crawl_results WHERE task_id = '$task_id' ORDER BY found_at;"
}

show_stats() {
    echo "=== ESTATÍSTICAS GERAIS ==="
    echo ""
    echo "Consultas úteis para executar no H2 Console:"
    echo ""
    echo "1. Total de tarefas por status:"
    echo "   SELECT status, COUNT(*) FROM crawl_tasks GROUP BY status;"
    echo ""
    echo "2. Keywords mais buscadas:"
    echo "   SELECT keyword, COUNT(*) as total FROM crawl_tasks GROUP BY keyword ORDER BY total DESC;"
    echo ""
    echo "3. Média de páginas visitadas:"
    echo "   SELECT AVG(total_pages_visited) as avg_pages FROM crawl_tasks WHERE status = 'done';"
    echo ""
    echo "4. Tarefas com mais resultados:"
    echo "   SELECT id, keyword, total_urls_found FROM crawl_tasks ORDER BY total_urls_found DESC LIMIT 5;"
    echo ""
    echo "5. URLs mais encontradas:"
    echo "   SELECT url, COUNT(*) as freq FROM crawl_results GROUP BY url HAVING COUNT(*) > 1 ORDER BY freq DESC;"
}

load_sample() {
    echo "=== CARREGANDO DADOS DE EXEMPLO ==="
    echo ""
    echo "Para carregar dados adicionais:"
    echo "1. Acesse http://localhost:4567/h2-console"
    echo "2. Use as credenciais: URL=jdbc:h2:mem:testdb, User=sa, Password=(vazio)"
    echo "3. Execute o conteúdo do arquivo: src/main/resources/db/sample-data.sql"
    echo ""
    echo "Ou execute via aplicação Spring Boot com perfil de desenvolvimento."
}

clear_data() {
    echo "=== LIMPEZA DE DADOS ==="
    echo ""
    echo "⚠️  CUIDADO: Esta operação remove TODOS os dados!"
    echo ""
    read -p "Tem certeza que deseja continuar? (digite 'SIM' para confirmar): " confirm
    
    if [ "$confirm" = "SIM" ]; then
        echo "SQL para executar no H2 Console:"
        echo "DELETE FROM visited_urls;"
        echo "DELETE FROM crawl_results;"
        echo "DELETE FROM crawl_tasks;"
        echo ""
        echo "Dados limpos! Execute as migrações Flyway para recriar dados básicos."
    else
        echo "Operação cancelada."
    fi
}

# Menu principal
case "${1:-help}" in
    "show-tasks")
        show_tasks
        ;;
    "show-results")
        show_results $2
        ;;
    "show-stats")
        show_stats
        ;;
    "load-sample")
        load_sample
        ;;
    "clear-data")
        clear_data
        ;;
    "help"|*)
        show_help
        ;;
esac
