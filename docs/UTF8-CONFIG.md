# Configuração UTF-8

Este projeto está configurado para usar UTF-8 em todos os pontos:

## Maven (pom.xml)

```xml
<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    <maven.compiler.encoding>UTF-8</maven.compiler.encoding>
</properties>
```

## Plugins Maven

- **maven-compiler-plugin**: configurado com `<encoding>UTF-8</encoding>`
- **maven-surefire-plugin**: configurado com encoding UTF-8 e system property `file.encoding=UTF-8`
- **maven-resources-plugin**: configurado com `<encoding>UTF-8</encoding>`

## Spring Boot (application.yml)

```yaml
server:
  servlet:
    encoding:
      charset: UTF-8
      enabled: true
      force: false  # Alterado para evitar charset automático em responses
```

**Nota**: O `force: false` permite que responses JSON não incluam automaticamente `;charset=UTF-8` no Content-Type, conforme requerido pela especificação do desafio Axur.

## Logback

Todos os appenders nos arquivos `logback-spring.xml` e `logback-test.xml` estão configurados com:

```xml
<encoder>
    <charset>UTF-8</charset>
</encoder>
```

**Nota**: O `logback-test.xml` utiliza `SizeAndTimeBasedRollingPolicy` para suportar indexação de arquivos (`%i`) com rotação por tempo e tamanho.


## Docker

```dockerfile
ENV LANG=en_US.UTF-8
ENV LANGUAGE=en_US:en
ENV LC_ALL=en_US.UTF-8
ENV JAVA_TOOL_OPTIONS="-Dfile.encoding=UTF-8"
```

## Verificação

Para verificar se o UTF-8 está funcionando:

```bash
mvn clean compile
mvn test
```

## Mudanças para Conformidade com Desafio Axur

Para atender à especificação exata do desafio técnico da Axur, que requer responses com Content-Type `application/json` (sem charset), foram feitas as seguintes configurações específicas:

### Spring Boot Response Headers

- **force: false**: Evita adição automática de `;charset=UTF-8` no Content-Type
- **produces = MediaType.APPLICATION_JSON_VALUE**: No controlador para garantir Content-Type limpo

### Logback Rolling Policy  

- **SizeAndTimeBasedRollingPolicy**: Para suportar rotação por tempo E tamanho simultaneamente

Estas mudanças mantêm o UTF-8 interno (encoding de arquivos, compilação, logs) mas ajustam a saída HTTP para conformidade com a especificação do desafio.
