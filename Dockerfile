FROM openjdk:17-jdk-slim

# Configurações UTF-8
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en
ENV LC_ALL=en_US.UTF-8

# Instalar Maven
RUN apt-get update && \
    apt-get install -y maven && \
    rm -rf /var/lib/apt/lists/*

# Diretório de trabalho
WORKDIR /app

# Copiar arquivos do projeto
COPY . .

# Compilar aplicação
RUN mvn clean package -DskipTests

# Expor porta 4567
EXPOSE 4567

# Comando para executar a aplicação
CMD ["java", "-jar", "target/backend-axur-1.0-SNAPSHOT.jar"]
