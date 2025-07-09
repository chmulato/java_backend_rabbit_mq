# Script PowerShell para inicializar o ambiente de desenvolvimento no Windows

Write-Host "=== Axur Web Crawler - Setup Development Environment ===" -ForegroundColor Green

# Verificar se Docker está rodando
try {
    docker info | Out-Null
    Write-Host "✅ Docker está rodando." -ForegroundColor Green
} catch {
    Write-Host "❌ Docker não está rodando. Por favor, inicie o Docker primeiro." -ForegroundColor Red
    exit 1
}

# Verificar se docker-compose está disponível
try {
    docker-compose --version | Out-Null
    Write-Host "✅ Docker Compose está disponível." -ForegroundColor Green
} catch {
    Write-Host "❌ Docker Compose não encontrado. Por favor, instale o Docker Compose." -ForegroundColor Red
    exit 1
}

# Criar arquivo .env se não existir
if (-not (Test-Path ".env")) {
    Write-Host "📝 Criando arquivo .env..." -ForegroundColor Yellow
    Copy-Item ".env.example" ".env"
    Write-Host "✅ Arquivo .env criado. Ajuste as configurações se necessário." -ForegroundColor Green
}

# Criar diretório de logs se não existir
if (-not (Test-Path "logs")) {
    Write-Host "📁 Criando diretório logs..." -ForegroundColor Yellow
    New-Item -ItemType Directory -Path "logs" | Out-Null
    Write-Host "✅ Diretório logs criado." -ForegroundColor Green
}

Write-Host ""
Write-Host "=== Opções de execução ===" -ForegroundColor Cyan
Write-Host "1. Desenvolvimento (apenas RabbitMQ): docker-compose -f docker-compose.dev.yml up -d"
Write-Host "2. Produção completa: docker-compose up -d"
Write-Host "3. Build e execução: docker-compose up --build"
Write-Host ""

$choice = Read-Host "Escolha uma opção (1-3) ou pressione Enter para pular"

switch ($choice) {
    "1" {
        Write-Host "🚀 Iniciando ambiente de desenvolvimento..." -ForegroundColor Yellow
        docker-compose -f docker-compose.dev.yml up -d
        Write-Host "✅ RabbitMQ disponível em http://localhost:15672 (guest/guest)" -ForegroundColor Green
        Write-Host "💡 Execute 'mvn spring-boot:run' para iniciar a aplicação" -ForegroundColor Cyan
    }
    "2" {
        Write-Host "🚀 Iniciando ambiente completo..." -ForegroundColor Yellow
        docker-compose up -d
        Write-Host "✅ Aplicação disponível em http://localhost:4567" -ForegroundColor Green
        Write-Host "✅ RabbitMQ disponível em http://localhost:15672 (guest/guest)" -ForegroundColor Green
    }
    "3" {
        Write-Host "🚀 Building e iniciando ambiente completo..." -ForegroundColor Yellow
        docker-compose up --build
    }
    default {
        Write-Host "⏭️ Configuração concluída. Use os comandos Docker Compose conforme necessário." -ForegroundColor Gray
    }
}

Write-Host ""
Write-Host "=== URLs úteis ===" -ForegroundColor Cyan
Write-Host "🌐 Aplicação: http://localhost:4567"
Write-Host "🐰 RabbitMQ Management: http://localhost:15672 (guest/guest)"
Write-Host "📊 Health Check: http://localhost:4567/actuator/health"
Write-Host "📈 Metrics: http://localhost:4567/actuator/metrics"
Write-Host ""
