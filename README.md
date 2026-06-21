# Analisador de HTML em Java

Projeto acadêmico completo para análise estrutural de documentos HTML utilizando **Pilha**, **MergeSort manual**, **árvore hierárquica** e **interface gráfica Swing**.

## Funcionalidades

- Leitura de arquivos `.html`, `.htm` e `.txt` linha a linha (linhas em branco não geram tags e mantêm a numeração original)
- Reconhecimento de tags de abertura, fechamento e autofechamento (com atributos)
- Comparação de tags **case-insensitive**
- Tags singleton não empilhadas: `meta`, `base`, `br`, `col`, `command`, `embed`, `hr`, `img`, `input`, `link`, `param`, `source`, `!doctype`
- Validação estrutural com **Pilha** implementada manualmente
- Detecção de erros: tag final inesperada, tag final sem abertura, tags não finalizadas e tag malformada
- Estatísticas (tag, frequência, tipo, 1ª ocorrência) ordenadas com **MergeSort manual**
- Hierarquia do documento exibida **somente quando válido**
- Interface gráfica Swing com abas: Relatório, Estatísticas e Hierarquia

## Arquitetura do Projeto

```
br.edu.htmlanalyzer
├── Main.java                    # Ponto de entrada
├── model/                       # Entidades de domínio
│   ├── AnalysisError.java
│   ├── AnalysisResult.java
│   ├── ErrorType.java
│   ├── HtmlNode.java            # Árvore hierárquica
│   ├── ParsedTag.java
│   ├── TagStatistics.java
│   └── TagType.java
├── datastructure/               # Estruturas de dados manuais
│   ├── MergeSort.java
│   └── Stack.java               # Pilha LIFO
├── parser/                      # Extração e validação
│   ├── HtmlTagParser.java
│   └── HtmlValidator.java
├── service/                     # Regras de negócio
│   ├── HierarchyBuilder.java
│   ├── HtmlAnalyzerService.java
│   └── StatisticsService.java
├── ui/                          # Interface Swing
│   └── MainFrame.java
└── util/                        # Utilitários
    ├── FileReaderUtil.java
    └── TagUtils.java
```

## Estrutura de Pastas

```
tarbalhoFinalDados/
├── src/br/edu/htmlanalyzer/     # Código-fonte principal
├── test/br/edu/htmlanalyzer/    # Casos de teste (Java puro)
├── resources/samples/           # Arquivos HTML de exemplo
├── docs/                        # UML e fluxograma
└── README.md
```

## Estruturas de Dados Utilizadas

| Estrutura | Uso | Implementação |
|-----------|-----|---------------|
| **Pilha (Stack)** | Validação de balanceamento de tags | Array dinâmico com `push`, `pop`, `peek` |
| **MergeSort** | Ordenação alfabética das estatísticas | Divisão e conquista manual |
| **Árvore (HtmlNode)** | Hierarquia do documento | Lista de filhos com indentação |
| **HashMap** | Contagem de frequência de tags | API padrão Java |

## Compilação e Execução via Terminal

### Pré-requisitos

- JDK 8 ou superior (testado com Java 8)

### Compilar

```powershell
# Windows (PowerShell) - na raiz do projeto
Get-ChildItem -Path src,test -Recurse -Filter *.java | ForEach-Object { $_.FullName } | Set-Content sources.txt
javac -encoding UTF-8 -source 8 -target 8 -d out (Get-Content sources.txt)
```

> **Nota:** Se o `javac` for mais recente que o `java` instalado, use `-source 8 -target 8` para garantir compatibilidade.

### Executar a aplicação

```bash
java -cp out br.edu.htmlanalyzer.Main
```

### Executar testes

```powershell
java -cp out br.edu.htmlanalyzer.test.AllTests
```

## IntelliJ IDEA

1. **File → Open** e selecione a pasta `tarbalhoFinalDados`
2. **File → Project Structure → Modules**
   - Marque `src` como **Sources**
   - Marque `test` como **Test Sources** (ou Sources)
   - Marque `resources` como **Resources**
3. Defina o SDK do projeto (JDK 11+)
4. Crie uma **Run Configuration**:
   - Main class: `br.edu.htmlanalyzer.Main`
   - Classpath: módulo do projeto
5. Clique em **Run**

## Eclipse

1. **File → Import → General → Existing Projects into Workspace**
2. Se necessário, crie um projeto Java:
   - **File → New → Java Project**
   - Desmarque "Use default location" e aponte para a pasta do projeto
3. Clique com botão direito em `src` → **Build Path → Use as Source Folder**
4. Repita para `test`
5. **Run → Run Configurations → Java Application**
   - Main class: `br.edu.htmlanalyzer.Main`

## Apache NetBeans

1. **File → Open Project** e selecione a pasta
2. Se abrir como projeto simples, configure:
   - **Properties → Sources**: Source Package Folders = `src`
   - **Properties → Run**: Main Class = `br.edu.htmlanalyzer.Main`
3. Pressione **F6** para executar

## Casos de Teste

| Classe | Descrição |
|--------|-----------|
| `StackTest` | Operações push, pop, peek, redimensionamento |
| `MergeSortTest` | Ordenação de strings e TagStatistics |
| `HtmlValidatorTest` | Todos os tipos de erro e casos válidos |
| `IntegrationTest` | Arquivos de exemplo em `resources/samples/` |
| `AllTests` | Executa todos os testes |

### Arquivos de exemplo

| Arquivo | Resultado esperado |
|---------|-------------------|
| `valido.html` | Válido, com estatísticas e hierarquia |
| `tag_final_inesperada.html` | Tag final inesperada |
| `tag_sem_abertura.html` | Tag final sem abertura |
| `tags_nao_finalizadas.html` | Tags não finalizadas |
| `tag_malformada.html` | Tag malformada |

## Documentação Adicional

- [Diagrama UML](docs/UML.md)
- [Fluxograma Mermaid](docs/fluxograma.md)

## Autor

Projeto desenvolvido para disciplina de Estruturas de Dados e Engenharia de Software.
