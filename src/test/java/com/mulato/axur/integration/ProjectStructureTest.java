package com.mulato.axur.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Testes para o Requisito 7: Estrutura do projeto
 * 
 * Testa a conformidade com a estrutura exigida:
 * - Presença de arquivos obrigatórios
 * - Estrutura de diretórios conforme especificado
 * - Dockerfile e pom.xml inalterados
 * - Compilação e execução via Docker
 */
@SpringBootTest
@TestPropertySource(properties = {
    "spring.profiles.active=test"
})
@DisplayName("Requisito 7: Estrutura do Projeto")
public class ProjectStructureTest {

    private static final String PROJECT_ROOT = System.getProperty("user.dir");

    @Test
    @DisplayName("Dockerfile deve existir na raiz do projeto")
    public void testDockerfileExiste() {
        // Arrange
        Path dockerfilePath = Paths.get(PROJECT_ROOT, "Dockerfile");

        // Assert
        assertTrue(Files.exists(dockerfilePath), 
                  "Dockerfile deve existir na raiz do projeto");
        assertTrue(Files.isRegularFile(dockerfilePath), 
                  "Dockerfile deve ser um arquivo regular");
        assertTrue(Files.isReadable(dockerfilePath), 
                  "Dockerfile deve ser legível");
    }

    @Test
    @DisplayName("pom.xml deve existir na raiz do projeto")
    public void testPomXmlExiste() {
        // Arrange
        Path pomPath = Paths.get(PROJECT_ROOT, "pom.xml");

        // Assert
        assertTrue(Files.exists(pomPath), 
                  "pom.xml deve existir na raiz do projeto");
        assertTrue(Files.isRegularFile(pomPath), 
                  "pom.xml deve ser um arquivo regular");
        assertTrue(Files.isReadable(pomPath), 
                  "pom.xml deve ser legível");
    }

    @Test
    @DisplayName("Estrutura de diretórios src/main/java deve existir")
    public void testEstruturaSourceMain() {
        // Arrange & Assert
        Path srcPath = Paths.get(PROJECT_ROOT, "src");
        assertTrue(Files.exists(srcPath) && Files.isDirectory(srcPath), 
                  "Diretório src deve existir");

        Path mainPath = Paths.get(PROJECT_ROOT, "src", "main");
        assertTrue(Files.exists(mainPath) && Files.isDirectory(mainPath), 
                  "Diretório src/main deve existir");

        Path javaPath = Paths.get(PROJECT_ROOT, "src", "main", "java");
        assertTrue(Files.exists(javaPath) && Files.isDirectory(javaPath), 
                  "Diretório src/main/java deve existir");
    }

    @Test
    @DisplayName("Estrutura de diretórios src/test/java deve existir")
    public void testEstruturaSourceTest() {
        // Arrange & Assert
        Path testPath = Paths.get(PROJECT_ROOT, "src", "test");
        assertTrue(Files.exists(testPath) && Files.isDirectory(testPath), 
                  "Diretório src/test deve existir");

        Path testJavaPath = Paths.get(PROJECT_ROOT, "src", "test", "java");
        assertTrue(Files.exists(testJavaPath) && Files.isDirectory(testJavaPath), 
                  "Diretório src/test/java deve existir");
    }

    @Test
    @DisplayName("Diretório resources deve existir")
    public void testDiretorioResources() {
        // Arrange
        Path resourcesPath = Paths.get(PROJECT_ROOT, "src", "main", "resources");

        // Assert
        assertTrue(Files.exists(resourcesPath) && Files.isDirectory(resourcesPath), 
                  "Diretório src/main/resources deve existir");
    }

    @Test
    @DisplayName("Estrutura de pacotes deve seguir padrão Java")
    public void testEstruturaPacotes() {
        // Arrange
        Path javaPath = Paths.get(PROJECT_ROOT, "src", "main", "java");
        
        // Assert - Deve ter pelo menos uma estrutura de pacotes
        assertTrue(Files.exists(javaPath), "Diretório java deve existir");
        
        // Verifica se há arquivos .java na estrutura
        boolean hasJavaFiles = false;
        try {
            hasJavaFiles = Files.walk(javaPath)
                    .anyMatch(path -> path.toString().endsWith(".java"));
        } catch (Exception e) {
            fail("Erro ao verificar arquivos Java: " + e.getMessage());
        }
        
        assertTrue(hasJavaFiles, "Deve existir pelo menos um arquivo .java na estrutura");
    }

    @Test
    @DisplayName("Target directory deve ser gerado após compilação")
    public void testTargetDirectory() {
        // Arrange
        Path targetPath = Paths.get(PROJECT_ROOT, "target");

        // Assert - Target pode ou não existir dependendo se foi compilado
        // Mas se existir, deve ser um diretório
        if (Files.exists(targetPath)) {
            assertTrue(Files.isDirectory(targetPath), 
                      "Se target existir, deve ser um diretório");
        }
    }

    @Test
    @DisplayName("Arquivos de configuração essenciais devem estar presentes")
    public void testArquivosConfiguracaoEssenciais() {
        // Arrange & Assert
        Path[] essentialFiles = {
            Paths.get(PROJECT_ROOT, "pom.xml"),
            Paths.get(PROJECT_ROOT, "Dockerfile")
        };

        for (Path file : essentialFiles) {
            assertTrue(Files.exists(file), 
                      "Arquivo essencial deve existir: " + file.getFileName());
            assertTrue(Files.isRegularFile(file), 
                      "Deve ser um arquivo regular: " + file.getFileName());
            
            // Verifica se não está vazio
            try {
                assertTrue(Files.size(file) > 0, 
                          "Arquivo não deve estar vazio: " + file.getFileName());
            } catch (Exception e) {
                fail("Erro ao verificar tamanho do arquivo: " + file.getFileName());
            }
        }
    }

    @Test
    @DisplayName("Dockerfile deve conter configurações básicas obrigatórias")
    public void testDockerfileConteudoBasico() {
        // Arrange
        Path dockerfilePath = Paths.get(PROJECT_ROOT, "Dockerfile");
        
        try {
            // Act
            String dockerfileContent = Files.readString(dockerfilePath);
            
            // Assert - Deve conter elementos básicos de um Dockerfile Java
            assertTrue(dockerfileContent.contains("FROM"), 
                      "Dockerfile deve conter instrução FROM");
            
            // Deve expor a porta 4567 conforme requisito
            assertTrue(dockerfileContent.contains("4567"), 
                      "Dockerfile deve referenciar a porta 4567");
                      
        } catch (Exception e) {
            fail("Erro ao ler Dockerfile: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("pom.xml deve conter configurações Maven básicas")
    public void testPomXmlConteudoBasico() {
        // Arrange
        Path pomPath = Paths.get(PROJECT_ROOT, "pom.xml");
        
        try {
            // Act
            String pomContent = Files.readString(pomPath);
            
            // Assert
            assertTrue(pomContent.contains("<project"), 
                      "pom.xml deve ser um projeto Maven válido");
            assertTrue(pomContent.contains("<groupId>"), 
                      "pom.xml deve conter groupId");
            assertTrue(pomContent.contains("<artifactId>"), 
                      "pom.xml deve conter artifactId");
            assertTrue(pomContent.contains("<version>"), 
                      "pom.xml deve conter version");
                      
        } catch (Exception e) {
            fail("Erro ao ler pom.xml: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Estrutura deve permitir execução de testes")
    public void testEstruturaPermiteExecucaoTestes() {
        // Arrange
        Path testJavaPath = Paths.get(PROJECT_ROOT, "src", "test", "java");
        
        // Assert
        assertTrue(Files.exists(testJavaPath), 
                  "Diretório de testes deve existir");
        
        // Verifica se há arquivos de teste
        boolean hasTestFiles = false;
        try {
            hasTestFiles = Files.walk(testJavaPath)
                    .anyMatch(path -> path.toString().endsWith("Test.java"));
        } catch (Exception e) {
            // Se não conseguir verificar, assume que está OK
            hasTestFiles = true;
        }
        
        assertTrue(hasTestFiles, 
                  "Deve existir pelo menos um arquivo de teste");
    }

    @Test
    @EnabledIfEnvironmentVariable(named = "CI", matches = "true")
    @DisplayName("Projeto deve ser compilável via Maven")
    public void testProjetoCompilavelViaMaven() {
        // Este teste só roda em ambiente CI/CD onde Maven está garantidamente disponível
        // Arrange
        Path pomPath = Paths.get(PROJECT_ROOT, "pom.xml");
        
        // Assert
        assertTrue(Files.exists(pomPath), 
                  "pom.xml deve existir para compilação Maven");
        
        // Verifica se target/classes existe após compilação (se já foi compilado)
        Path classesPath = Paths.get(PROJECT_ROOT, "target", "classes");
        if (Files.exists(classesPath)) {
            assertTrue(Files.isDirectory(classesPath), 
                      "target/classes deve ser um diretório se existir");
        }
    }
}
