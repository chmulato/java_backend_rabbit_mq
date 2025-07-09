#!/bin/bash

# Script para build da aplicação Docker

echo "=== Axur Web Crawler - Docker Build ==="

# Verificar se Docker está rodando
if ! docker info > /dev/null 2>&1; then
    echo "❌ Docker não está rodando. Por favor, inicie o Docker primeiro."
    exit 1
fi

echo "✅ Docker detectado e funcionando"

# Limpar builds anteriores
echo "🧹 Limpando builds anteriores..."
docker rmi axreng/backend 2>/dev/null || true

# Fazer build da aplicação Java primeiro
echo "🔨 Compilando aplicação Java..."
mvn clean package -DskipTests -q

if [ $? -eq 0 ]; then
    echo "✅ Compilação Java concluída com sucesso"
else
    echo "❌ Erro na compilação Java"
    exit 1
fi

# Build da imagem Docker
echo "🐳 Construindo imagem Docker..."
docker build -t axreng/backend . --progress=plain

if [ $? -eq 0 ]; then
    echo "✅ Build Docker concluído com sucesso"
    echo "🎉 Imagem 'axreng/backend' criada!"
    
    # Mostrar informações da imagem
    echo ""
    echo "📊 Informações da imagem:"
    docker images axreng/backend
    
    echo ""
    echo "🚀 Para executar:"
    echo "docker run -p 4567:4567 axreng/backend"
    echo ""
    echo "📝 Ou use o docker-compose:"
    echo "docker-compose up -d"
    
else
    echo "❌ Erro no build Docker"
    exit 1
fi
