# Script PowerShell para build da aplicação Docker

Write-Host "=== Axur Web Crawler - Docker Build ===" -ForegroundColor Green

# Verificar se Docker está rodando
try {
    docker info | Out-Null
    Write-Host "✅ Docker detectado e funcionando" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker não está rodando. Por favor, inicie o Docker primeiro." -ForegroundColor Red
    exit 1
}

# Limpar builds anteriores
Write-Host "🧹 Limpando builds anteriores..." -ForegroundColor Yellow
docker rmi axreng/backend 2>$null

# Fazer build da aplicação Java primeiro
Write-Host "🔨 Compilando aplicação Java..." -ForegroundColor Yellow
mvn clean package -DskipTests -q

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Compilação Java concluída com sucesso" -ForegroundColor Green
} else {
    Write-Host "❌ Erro na compilação Java" -ForegroundColor Red
    exit 1
}

# Build da imagem Docker
Write-Host "🐳 Construindo imagem Docker..." -ForegroundColor Yellow
docker build -t axreng/backend . --progress=plain

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ Build Docker concluído com sucesso" -ForegroundColor Green
    Write-Host "🎉 Imagem 'axreng/backend' criada!" -ForegroundColor Green
    
    # Mostrar informações da imagem
    Write-Host ""
    Write-Host "📊 Informações da imagem:" -ForegroundColor Cyan
    docker images axreng/backend
    
    Write-Host ""
    Write-Host "🚀 Para executar:" -ForegroundColor Cyan
    Write-Host "docker run -p 4567:4567 axreng/backend" -ForegroundColor White
    Write-Host ""
    Write-Host "📝 Ou use o docker-compose:" -ForegroundColor Cyan
    Write-Host "docker-compose up -d" -ForegroundColor White
    
} else {
    Write-Host "❌ Erro no build Docker" -ForegroundColor Red
    exit 1
}
