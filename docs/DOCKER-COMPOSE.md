# Docker Compose - Web Crawler API

## Arquivos

- `docker-compose.yml`: Configuração principal
- `.env`: Variáveis de ambiente
- `.env.example`: Exemplo de configuração

## Serviços

### RabbitMQ

- **Imagem**: `rabbitmq:3-management`
- **Portas**:
  - `5672`: AMQP
  - `15672`: Management UI
- **Credenciais**: guest/guest
- **Health Check**: Configurado

### Aplicação Spring Boot

- **Build**: Local via Dockerfile
- **Porta**: `4567`
- **Dependências**: RabbitMQ (health check)
- **Configurações UTF-8**: Totalmente configurado
- **Health Check**: `/actuator/health`

## Comandos Úteis

```bash
# Iniciar todos os serviços
docker-compose up -d

# Ver logs
docker-compose logs -f

# Ver logs de um serviço específico
docker-compose logs -f app
docker-compose logs -f rabbitmq

# Rebuild e restart
docker-compose up --build

# Parar serviços
docker-compose down

# Parar e remover volumes
docker-compose down -v

# Status dos serviços
docker-compose ps
```

## URLs

- **Aplicação**: <http://localhost:4567>
- **RabbitMQ Management**: <http://localhost:15672>
- **Health Check**: <http://localhost:4567/actuator/health>
- **API Docs**: <http://localhost:4567/actuator/info>

## Configurações UTF-8

Todas as configurações UTF-8 estão aplicadas:

- Variáveis de ambiente do sistema
- Java Tool Options
- Configurações Spring Boot
- Logback encoders
