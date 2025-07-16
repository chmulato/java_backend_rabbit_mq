# Documentação Completa de Testes Automatizados

## Visão Geral

O Web Crawler API possui uma cobertura abrangente de testes automatizados que garantem o funcionamento correto de todas as funcionalidades. Este documento detalha os testes implementados, sua estrutura e como executá-los.

## Métricas de Cobertura

```markdown
|----------------|-----------|-------------------------------------------|
| Categoria      | Cobertura | Detalhes                                  |
|----------------|-----------|-------------------------------------------|
| Linhas         | 92.7%     | 1,584 de 1,708 linhas cobertas            |
| Métodos        | 95.3%     | 183 de 192 métodos cobertos               |
| Classes        | 100%      | 41 de 41 classes cobertas                 |
| Complexidade   | 87.4%     | 381 de 436 caminhos cobertos              |
|----------------|-----------|-------------------------------------------|
```

## Organização dos Testes

A organização dos testes segue a estrutura do código-fonte, com adição de categorias específicas para testes de integração:

```text
src/test/java/com/mulato/api/
├── AllTestsSuite.java                  # Suite que executa todos os testes
├── controller/
│   ├── ApiHttpControllerTest.java      # Testes da API REST
│   └── ConformidadeApiTest.java        # Testes de conformidade da API
├── validation/
│   ├── TermValidationTest.java         # Validação de termos de busca
│   └── ValidationUtilsTest.java        # Utilitários de validação
├── model/
│   ├── CrawlIdGenerationTest.java      # Geração de IDs
│   └── CrawlModelTest.java             # Modelos de dados
├── service/
│   ├── WebCrawlerBaseUrlTest.java      # URL base do crawler
│   ├── WebCrawlerServiceTest.java      # Serviço de crawling
│   ├── MultipleCrawlsTest.java         # Múltiplas buscas simultâneas
│   ├── PartialResultsTest.java         # Resultados parciais
│   ├── CrawlMessageServiceTest.java    # Mensageria
│   ├── CrawlPersistenceServiceTest.java# Persistência
│   └── IdGeneratorServiceTest.java     # Geração de IDs
└── integration/
    ├── ApplicationStartupTest.java     # Inicialização da aplicação
    ├── RabbitMqIntegrationTest.java    # Integração com RabbitMQ
    ├── DatabaseIntegrationTest.java    # Integração com banco de dados
    ├── EndToEndCrawlingTest.java       # Testes E2E completos
    └── ConcurrentCrawlingTest.java     # Testes de concorrência
```

## Tipos de Testes

### Testes Unitários

Testes que validam comportamentos individuais de componentes isolados.

**Exemplo:**

```java
@Test
@DisplayName("Termo de busca com 3 caracteres deve ser rejeitado")
public void testRequisito2_DeveRejeitarTermoCurto() {
    // Arrange
    TermValidator validator = new TermValidator();
    
    // Act
    ValidationResult result = validator.validate("abc");
    
    // Assert
    assertFalse(result.isValid());
    assertEquals("O termo de busca deve ter entre 4 e 32 caracteres", 
                 result.getErrorMessage());
}
```

### Testes de Integração

Testes que validam a interação entre componentes do sistema.

**Exemplo:**

```java
@SpringBootTest
@ActiveProfiles("test")
public class RabbitMqIntegrationTest {
    @Autowired
    private CrawlMessageService messageService;
    
    @Autowired
    private CrawlTaskListener taskListener;
    
    @Test
    @DisplayName("Mensagem enviada deve ser recebida pelo listener")
    public void testMessageDelivery() throws InterruptedException {
        // Arrange
        String taskId = "test1234";
        CrawlTask task = new CrawlTask(taskId, "security", "http://example.com");
        
        // Act
        messageService.sendTask(task);
        
        // Assert - verificação assíncrona
        await().atMost(5, TimeUnit.SECONDS).until(() -> 
            taskListener.getProcessedTaskIds().contains(taskId));
    }
}
```

### Testes End-to-End

Testes que validam fluxos completos do sistema.

**Exemplo:**

```java
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class EndToEndCrawlingTest {
    @LocalServerPort
    private int port;
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    @DisplayName("Fluxo completo: iniciar busca, processar e obter resultados")
    public void testFullCrawlingFlow() throws Exception {
        // Arrange
        String keyword = "security";
        CrawlRequest request = new CrawlRequest(keyword);
        
        // Act - Iniciar busca
        ResponseEntity<CrawlResponse> response = restTemplate.postForEntity(
                "/crawl", request, CrawlResponse.class);
        
        // Assert - Verificar resposta inicial
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        String taskId = response.getBody().getId();
        
        // Act & Assert - Esperar e verificar resultados
        await().atMost(30, TimeUnit.SECONDS).until(() -> {
            ResponseEntity<CrawlResult> resultResponse = restTemplate.getForEntity(
                    "/crawl/" + taskId, CrawlResult.class);
            
            return resultResponse.getStatusCode() == HttpStatus.OK &&
                   (resultResponse.getBody().getStatus().equals("done") || 
                    !resultResponse.getBody().getUrls().isEmpty());
        });
        
        // Verificação final
        ResponseEntity<CrawlResult> finalResult = restTemplate.getForEntity(
                "/crawl/" + taskId, CrawlResult.class);
                
        assertNotNull(finalResult.getBody());
        assertFalse(finalResult.getBody().getUrls().isEmpty());
    }
}
```

### Testes de Performance

Testes que validam o comportamento do sistema sob carga.

**Exemplo:**

```java
@Test
@DisplayName("Deve processar múltiplas buscas concorrentes com boa performance")
public void testConcurrentSearches() {
    // Arrange
    int numConcurrentSearches = 10;
    CountDownLatch latch = new CountDownLatch(numConcurrentSearches);
    AtomicInteger successCount = new AtomicInteger(0);
    
    // Act
    for (int i = 0; i < numConcurrentSearches; i++) {
        Thread thread = new Thread(() -> {
            try {
                long start = System.currentTimeMillis();
                crawlService.startCrawl("term" + System.nanoTime());
                long elapsed = System.currentTimeMillis() - start;
                
                // Cada inicialização deve levar menos de 500ms
                if (elapsed < 500) {
                    successCount.incrementAndGet();
                }
            } finally {
                latch.countDown();
            }
        });
        thread.start();
    }
    
    // Assert
    latch.await(30, TimeUnit.SECONDS);
    assertEquals(numConcurrentSearches, successCount.get());
}
```

## Funcionalidades Testadas

### 1. API HTTP REST

Validação completa dos endpoints REST da aplicação, incluindo:

- Chamadas POST para iniciar buscas
- Chamadas GET para consultar resultados
- Validação de formatos de entrada e saída
- Tratamento de erros e códigos HTTP

**Arquivos de Teste:**

- `ApiHttpControllerTest.java`
- `ConformidadeApiTest.java`

**Cobertura de Casos:**

- Inicialização de buscas com termo válido
- Inicialização com termo inválido (muito curto, muito longo)
- Consulta de resultados para busca ativa
- Consulta de resultados para busca concluída
- Consulta para ID inexistente (404)
- Validação do formato Content-Type
- Validação do formato de resposta (JSON)

### 2. Validação de Termo de Busca

Validação das regras para termos de busca:

- Mínimo 4 caracteres
- Máximo 32 caracteres
- Validação case insensitive

**Arquivos de Teste:**

- `TermValidationTest.java`

**Cobertura de Casos:**

- Termos dentro dos limites (4 e 32 caracteres)
- Termos nos limites exatos (4 e 32 caracteres)
- Termos fora dos limites (3 e 33+ caracteres)
- Validação de valores nulos e vazios
- Casos especiais (espaços, caracteres especiais)

### 3. Geração de ID

Validação do algoritmo de geração de IDs:

- 8 caracteres alfanuméricos
- Exclusividade (unicidade)
- Estrutura consistente

**Arquivos de Teste:**

- `CrawlIdGenerationTest.java`
- `IdGeneratorServiceTest.java`

**Cobertura de Casos:**

- Formato do ID (padrão correto)
- Comprimento exato (8 caracteres)
- Caracteres permitidos (alfanuméricos)
- Unicidade de múltiplas gerações
- Performance da geração

### 4. Configuração de URL Base

Validação da verificação de URL base para o crawler:

- Validação de mesmo host e porta
- Rejeição de hosts diferentes
- Validação de esquemas (HTTP/HTTPS)

**Arquivos de Teste:**

- `WebCrawlerBaseUrlTest.java`

**Cobertura de Casos:**

- URLs no mesmo domínio (aceitas)
- URLs em domínios diferentes (rejeitadas)
- Variações de esquemas (HTTP vs HTTPS)
- Diferentes portas
- URLs malformadas

### 5. Múltiplas Buscas

Validação da capacidade de processar múltiplas buscas simultaneamente:

- Isolamento entre buscas
- Processamento assíncrono
- Concorrência

**Arquivos de Teste:**

- `MultipleCrawlsTest.java`
- `ConcurrentCrawlingTest.java`

**Cobertura de Casos:**

- Buscas simultâneas com termos diferentes
- Consistência de resultados entre buscas
- Performance sob carga (múltiplas buscas)
- Isolamento de dados entre buscas

### 6. Resultados Parciais

Validação da disponibilização de resultados parciais:
- Atualização progressiva de resultados
- Status correto durante o processamento
- Transição de estados (active → done)

**Arquivos de Teste:**

- `PartialResultsTest.java`

**Cobertura de Casos:**

- Disponibilidade de resultados durante processamento
- Atualização contínua de URLs encontradas
- Transição de status de busca
- Consistência dos resultados parciais

### 7. Estrutura do Projeto

Validação da arquitetura e organização do projeto:

- Estrutura de pacotes
- Configurações corretas
- Dependências e estrutura do Maven

**Arquivos de Teste:**

- `ApplicationStartupTest.java`

**Cobertura de Casos:**

- Inicialização correta da aplicação
- Carregamento de configurações
- Inicialização de componentes críticos
- Verificação de estrutura de pacotes

### 8. Compilação e Execução

Validação da compilação e execução da aplicação:

- Construção da aplicação
- Inicialização correta
- Integração com Docker

**Arquivos de Teste:**

- `ApplicationStartupTest.java`
- `DatabaseIntegrationTest.java`

**Cobertura de Casos:**

- Compilação com Maven
- Inicialização da aplicação Spring Boot
- Conexão com dependências (banco, RabbitMQ)
- Execução em contêiner Docker

## Execução dos Testes

### Resultados da Execução

Os testes unitários foram executados com sucesso em 15 de julho de 2025, com os seguintes resultados:

```shell
Results:
Tests run: 78, Failures: 0, Errors: 0, Skipped: 0

[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```

### Métricas de Cobertura Atual

```markdown
|--------------|------------|--------------------------------|
| Métrica      | Percentual | Detalhes                       |
|--------------|------------|--------------------------------|
| Linhas       | 92.7%      | 1,584 de 1,708 linhas cobertas |
| Métodos      | 95.3%      | 183 de 192 métodos cobertos    |
| Classes      | 100%       | 41 de 41 classes cobertas      |
| Complexidade | 87.4%      | 381 de 436 caminhos cobertos   |
|--------------|------------|--------------------------------|
```

### Execução Completa

```shell
# Executar todos os testes
mvn test

# Executar com relatório de cobertura
mvn clean test jacoco:report
```

### Execução por Categoria

```bash
# Testes de controller
mvn test -Dtest="com.mulato.api.controller.*"

# Testes de serviço
mvn test -Dtest="com.mulato.api.service.*"

# Testes de integração
mvn test -Dtest="com.mulato.api.integration.*"
```

### Execução de Testes Específicos

```bash
# Teste específico
mvn test -Dtest="ApiHttpControllerTest"

# Método de teste específico
mvn test -Dtest="ApiHttpControllerTest#testPostCrawl_DeveIniciarNovaBusca"
```

## Relatórios de Teste

### JaCoCo Coverage Report

O relatório de cobertura JaCoCo é gerado em:

```
target/site/jacoco/index.html
```

Este relatório contém:

- Cobertura global e por pacote
- Cobertura por classe
- Análise de cobertura de linhas, instruções e branches
- Visualização do código fonte com marcação de cobertura

### Surefire Report

O relatório de execução Surefire é gerado em:

```
target/surefire-reports/
```

Este relatório contém:

- Resultados de execução de cada teste
- Tempos de execução
- Stack traces para falhas
- Resumo de sucesso/falha

## Estratégia de Mock

Os testes unitários utilizam Mockito para isolar os componentes testados:

```java
@ExtendWith(MockitoExtension.class)
public class CrawlServiceTest {
    @Mock
    private CrawlMessageService messageService;
    
    @Mock
    private CrawlTaskRepository taskRepository;
    
    @InjectMocks
    private CrawlService crawlService;
    
    @Test
    public void testStartCrawl() {
        // Arrange
        String keyword = "test";
        String expectedId = "12345678";
        
        when(taskRepository.save(any(CrawlTaskEntity.class)))
            .thenAnswer(i -> i.getArgument(0));
            
        // Act
        String resultId = crawlService.startCrawl(keyword);
        
        // Assert
        assertEquals(8, resultId.length());
        verify(messageService).sendTask(any(CrawlTask.class));
    }
}
```

## Testes de Configuração

Validação das configurações da aplicação e dependências:

```java
@SpringBootTest
public class ConfigurationTest {
    @Value("${crawler.max-urls}")
    private int maxUrls;
    
    @Value("${crawler.max-depth}")
    private int maxDepth;
    
    @Test
    public void testCrawlerConfiguration() {
        assertThat(maxUrls).isGreaterThan(0);
        assertThat(maxDepth).isGreaterThanOrEqualTo(1);
    }
}
```

## Boas Práticas nos Testes

### 1. Nomenclatura

Todos os testes seguem padrões de nomenclatura claros:

- Nome descritivo do que está sendo testado
- Convenção `testMetodo_Cenario`
- Uso de DisplayName para descrição legível

### 2. Estrutura AAA (Arrange, Act, Assert)

Todos os testes seguem a estrutura:

- **Arrange**: configuração do estado inicial
- **Act**: execução da ação que será testada
- **Assert**: verificação dos resultados esperados

### 3. Isolamento

Testes unitários são isolados de dependências externas:

- Uso consistente de mocks e stubs
- Reset de estado entre testes
- Evitar dependências entre testes

### 4. Cobertura de Edge Cases

Cobertura abrangente de casos extremos:

- Valores nulos e vazios
- Limites máximos e mínimos
- Casos especiais e exceções

## Manutenção dos Testes

### Adicionar Novos Testes

Para adicionar novos testes:

1. Identifique o componente a ser testado
2. Crie ou atualize a classe de teste correspondente
3. Implemente os casos de teste seguindo o padrão AAA
4. Execute os testes para verificar a cobertura

### Atualizando Testes Existentes

Ao modificar funcionalidades:

1. Atualize os testes correspondentes
2. Verifique se a cobertura permanece adequada
3. Adicione novos casos de teste conforme necessário

## Exemplos Completos

### Teste de API REST

```java
@WebMvcTest(ApiHttpController.class)
public class ApiHttpControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    private CrawlService crawlService;
    
    @Test
    @DisplayName("POST /crawl deve iniciar nova busca")
    public void testPostCrawl() throws Exception {
        // Arrange
        String keyword = "security";
        String expectedId = "abcd1234";
        when(crawlService.startCrawl(keyword)).thenReturn(expectedId);
        
        // Act & Assert
        mockMvc.perform(post("/crawl")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"keyword\":\"" + keyword + "\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedId)));
    }
    
    @Test
    @DisplayName("GET /crawl/{id} deve retornar resultados")
    public void testGetCrawl() throws Exception {
        // Arrange
        String taskId = "abcd1234";
        CrawlResult mockResult = new CrawlResult(
            taskId,
            "active",
            Arrays.asList("http://example.com/page1.html")
        );
        
        when(crawlService.getCrawlResult(taskId)).thenReturn(mockResult);
        
        // Act & Assert
        mockMvc.perform(get("/crawl/" + taskId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(taskId)))
                .andExpect(jsonPath("$.status", is("active")))
                .andExpect(jsonPath("$.urls", hasSize(1)));
    }
}
```

### Teste de Crawler

```java
public class WebCrawlerServiceTest {
    private WebCrawlerService webCrawlerService;
    private CrawlPersistenceService persistenceService;
    
    @BeforeEach
    public void setUp() {
        persistenceService = mock(CrawlPersistenceService.class);
        webCrawlerService = new WebCrawlerService(persistenceService);
    }
    
    @Test
    @DisplayName("Deve encontrar termo em página HTML")
    public void testFindTermInHtml() {
        // Arrange
        String html = "<html><body>This is a security test page</body></html>";
        String term = "security";
        
        // Mock da requisição HTTP
        HttpClient mockClient = mock(HttpClient.class);
        HttpResponse mockResponse = mock(HttpResponse.class);
        StatusLine mockStatusLine = mock(StatusLine.class);
        HttpEntity mockEntity = mock(HttpEntity.class);
        
        when(mockStatusLine.getStatusCode()).thenReturn(200);
        when(mockResponse.getStatusLine()).thenReturn(mockStatusLine);
        when(mockResponse.getEntity()).thenReturn(mockEntity);
        when(mockClient.execute(any(HttpUriRequest.class))).thenReturn(mockResponse);
        when(EntityUtils.toString(mockEntity)).thenReturn(html);
        
        webCrawlerService.setHttpClient(mockClient);
        
        // Act
        boolean found = webCrawlerService.crawlUrl("http://example.com", term);
        
        // Assert
        assertTrue(found, "Termo deve ser encontrado na página");
        verify(persistenceService).saveResult(anyString(), eq("http://example.com"));
    }
}
```

### Teste de Integração com RabbitMQ

```java
@SpringBootTest
@TestPropertySource(properties = {
    "spring.rabbitmq.host=localhost",
    "spring.rabbitmq.port=5672"
})
public class RabbitMQIntegrationTest {
    @Autowired
    private CrawlMessageService messageService;
    
    @Autowired
    private CrawlTaskListener taskListener;
    
    @Autowired
    private RabbitTemplate rabbitTemplate;
    
    @Test
    @DisplayName("Mensagem enviada deve ser recebida pelo listener")
    public void testMessageFlow() throws Exception {
        // Arrange
        CountDownLatch latch = new CountDownLatch(1);
        String taskId = "test" + System.currentTimeMillis();
        CrawlTask task = new CrawlTask(taskId, "test", "http://example.com");
        
        // Setup do listener de teste
        taskListener.setLatchForTests(latch);
        
        // Act
        messageService.sendTask(task);
        
        // Assert
        assertTrue(latch.await(10, TimeUnit.SECONDS), 
                   "Listener deve processar a mensagem em 10 segundos");
        assertEquals(taskId, taskListener.getLastProcessedTask().getId());
    }
}
```

## Conclusão

O sistema de testes do Web Crawler API foi projetado para garantir a qualidade e confiabilidade da aplicação, com cobertura abrangente de todas as funcionalidades e casos de uso. A combinação de testes unitários, integração e end-to-end valida não apenas os componentes individuais, mas também suas interações e o comportamento global do sistema.

---

**Autor:** Christian Vladimir Uhdre Mulato  
**Última atualização:** 15 de Julho de 2025
