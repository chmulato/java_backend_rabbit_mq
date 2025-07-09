#!/bin/bash

# Script para inicializar o ambiente de desenvolvimento

echo "=== Axur Web Crawler - Setup Development Environment ==="

# Verificar se Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Por favor, inicie o Docker primeiro."
    exit 1
fi

# Verificar se docker-compose está disponível
if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose não encontrado. Por favor, instale o Docker Compose."
    exit 1
fi

echo "✅ Docker e Docker Compose estão disponíveis."

# Criar arquivo .env se não existir
if [ ! -f .env ]; then
    echo "📝 Criando arquivo .env..."
    cp .env.example .env
    echo "✅ Arquivo .env criado. Ajuste as configurações se necessário."
fi

# Criar diretório de logs se não existir
if [ ! -d logs ]; then
    echo "📁 Criando diretório logs..."
    mkdir -p logs
    echo "✅ Diretório logs criado."
fi

echo ""
echo "=== Opções de execução ==="
echo "1. Desenvolvimento (apenas RabbitMQ): docker-compose -f docker-compose.dev.yml up -d"
echo "2. Produção completa: docker-compose up -d"
echo "3. Build e execução: docker-compose up --build"
echo ""

read -p "Escolha uma opção (1-3) ou pressione Enter para pular: " choice

case $choice in
    1)
        echo "🚀 Iniciando ambiente de desenvolvimento..."
        docker-compose -f docker-compose.dev.yml up -d
        echo "✅ RabbitMQ disponível em http://localhost:15672 (guest/guest)"
        echo "💡 Execute 'mvn spring-boot:run' para iniciar a aplicação"
        ;;
    2)
        echo "🚀 Iniciando ambiente completo..."
        docker-compose up -d
        echo "✅ Aplicação disponível em http://localhost:4567"
        echo "✅ RabbitMQ disponível em http://localhost:15672 (guest/guest)"
        ;;
    3)
        echo "🚀 Building e iniciando ambiente completo..."
        docker-compose up --build
        ;;
    *)
        echo "⏭️ Configuração concluída. Use os comandos Docker Compose conforme necessário."
        ;;
esac

echo ""
echo "=== URLs úteis ==="
echo "🌐 Aplicação: http://localhost:4567"
echo "🐰 RabbitMQ Management: http://localhost:15672 (guest/guest)"
echo "📊 Health Check: http://localhost:4567/actuator/health"
echo "📈 Metrics: http://localhost:4567/actuator/metrics"
echo ""
