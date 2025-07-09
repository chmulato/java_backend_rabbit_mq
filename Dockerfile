# Usar imagem oficial do Maven para build
FROM maven:3.9-openjdk-17 AS build

# Configurar diretório de trabalho
WORKDIR /app

# Copiar código fonte
COPY . .

# Compilar aplicação
RUN mvn clean package -DskipTests

# Imagem final com JRE
FROM openjdk:17-jre-slim

# Instalar curl para health checks
RUN apt-get update && \
    apt-get install -y curl && \
    apt-get clean && \
    rm -rf /var/lib/apt/lists/*

# Configurar UTF-8
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en
ENV LC_ALL=en_US.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"

# Diretório de trabalho
WORKDIR /app

# Copiar JAR compilado
COPY --from=build /app/target/*.jar app.jar

# Criar diretório de logs
RUN mkdir -p logs

# Expor porta
EXPOSE 4567

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:4567/actuator/health || exit 1

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]