# Contribuindo para Web Crawler API

Obrigado por considerar contribuir para o Web Crawler API! 🎉

## 🚀 Como Contribuir

### 1. **Reporting Bugs**

Se você encontrou um bug, por favor:
- Verifique se já não existe uma issue similar
- Crie uma nova issue com detalhes claros
- Inclua informações sobre o ambiente (OS, Java version, etc.)
- Forneça passos para reproduzir o problema

### 2. **Sugestões de Funcionalidades**

Para sugerir novas funcionalidades:
- Abra uma issue com label "enhancement"
- Descreva o problema que a funcionalidade resolveria
- Explique a solução proposta
- Considere implementações alternativas

### 3. **Pull Requests**

#### Processo de Development

1. **Fork** o repositório
2. **Clone** seu fork localmente
3. **Crie** uma branch para sua feature/fix:
   ```bash
   git checkout -b feature/nova-funcionalidade
   ```
4. **Desenvolva** sua solução
5. **Teste** completamente suas mudanças
6. **Commit** com mensagens claras
7. **Push** para seu fork
8. **Abra** um Pull Request

#### Padrões de Código

- **Java Code Style**: Siga as convenções padrão do Java
- **Spring Boot**: Use as melhores práticas do Spring
- **Testes**: Mantenha cobertura de testes >90%
- **Documentação**: Atualize docs quando necessário

#### Estrutura de Commits

```
tipo(escopo): descrição breve

Descrição mais detalhada se necessário

- Mudança específica 1
- Mudança específica 2

Closes #numero-da-issue
```

**Tipos de commit:**
- `feat`: Nova funcionalidade
- `fix`: Correção de bug
- `docs`: Mudanças na documentação
- `style`: Formatação, pontos e vírgulas, etc.
- `refactor`: Refatoração de código
- `test`: Adição ou correção de testes
- `chore`: Mudanças em ferramentas, configurações, etc.

### 4. **Configuração do Ambiente de Desenvolvimento**

```bash
# Clone o repositório
git clone https://github.com/chmulato/web-crawler-api.git
cd web-crawler-api

# Configurar ambiente
cp .env.example .env

# Subir dependências
docker-compose up -d rabbitmq

# Executar testes
mvn test

# Executar aplicação
mvn spring-boot:run
```

### 5. **Executando Testes**

```bash
# Todos os testes
mvn test

# Testes específicos
mvn test -Dtest=ApiHttpControllerTest

# Com cobertura
mvn clean test jacoco:report
```

### 6. **Padrões de Qualidade**

#### Requisitos Mínimos
- ✅ Todos os testes passando
- ✅ Cobertura de código >90%
- ✅ Sem warnings de compilação
- ✅ Documentação atualizada
- ✅ Code review aprovado

#### Ferramentas de Qualidade
- **Checkstyle**: Verificação de style
- **SpotBugs**: Análise estática
- **JaCoCo**: Cobertura de testes
- **SonarQube**: Análise de qualidade

## 🎯 Áreas que Precisam de Ajuda

### **High Priority**
- [ ] Suporte a JavaScript rendering
- [ ] Implementação de rate limiting
- [ ] Melhorias de performance
- [ ] Testes de carga

### **Medium Priority**
- [ ] Plugin system para extractors
- [ ] GraphQL endpoint
- [ ] Kubernetes deployment
- [ ] Multi-tenant support

### **Documentation**
- [ ] Tutorial de uso avançado
- [ ] Exemplos de integração
- [ ] API reference completa
- [ ] Guias de deployment

## 📝 Processo de Review

### O que Esperamos
1. **Código limpo** e bem comentado
2. **Testes abrangentes** para novas funcionalidades
3. **Documentação** atualizada
4. **Compatibilidade** com versões suportadas
5. **Performance** adequada

### Timeline
- **Review inicial**: 2-3 dias úteis
- **Feedback**: 1-2 dias para resposta
- **Merge**: Após aprovação e CI passing

## 🤝 Código de Conduta

### Nossos Compromissos

- **Respeito**: Tratamos todos com respeito e dignidade
- **Inclusão**: Bem-vindos desenvolvedores de todos os níveis
- **Colaboração**: Trabalhamos juntos para melhorar o projeto
- **Aprendizado**: Encorajamos troca de conhecimento

### Comportamento Esperado

- Use linguagem acolhedora e inclusiva
- Respeite diferentes pontos de vista
- Aceite críticas construtivas
- Foque no melhor para a comunidade
- Demonstre empatia com outros membros

## 📞 Contato

### Mantenedores
- **Christian Mulato**: [@chmulato](https://github.com/chmulato)

### Canais de Comunicação
- **Issues**: Para bugs e features
- **Discussions**: Para perguntas gerais
- **Email**: Para questões sensíveis

## 🏆 Reconhecimento

Contribuidores são reconhecidos:
- **README.md**: Lista de contribuidores
- **CHANGELOG.md**: Créditos por release
- **GitHub**: Contributor graph
- **LinkedIn**: Posts de agradecimento

---

**Obrigado por fazer parte da comunidade Web Crawler API!** 🚀

Sua contribuição, independente do tamanho, é valiosa e apreciada.
