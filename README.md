# Compilador MiniPascal → JVM

Compilador completo para um subconjunto da linguagem Pascal, desenvolvido como trabalho prático da disciplina de **Compiladores**. Transforma código-fonte `.pas` em bytecode executável pela JVM (`.class`).

---

## Pipeline de Compilação

```
arquivo.pas
    │
    ▼
[tp2] Scanner          ← Análise Léxica manual (char → tokens)
    │
    ▼
[tp4] ANTLR4 + MyVisitor  ← Análise Sintática (tokens → AST)
    │
    ▼
[tp5] SemanticAnalyzer ← Análise de Escopo (variáveis declaradas?)
    │
    ▼
[tp6] TypeChecker      ← Análise de Tipos (integer + boolean = erro)
    │
    ▼
[tp7] CodeGenerator    ← Geração de Bytecode JVM via ASM
    │
    ▼
arquivo.class          ← executável pela JVM
```

---

## Funcionalidades

### Linguagem suportada

- Declaração de variáveis (`var x, y: integer`)
- Tipos: `integer`, `boolean`, `string`
- Atribuição (`:=`)
- Operadores aritméticos: `+`, `-`, `*`, `/`, `div`, `mod`
- Operadores relacionais: `=`, `<>`, `<`, `>`, `<=`, `>=`
- Operadores lógicos: `and`, `or`, `not`
- Estruturas de controle: `if/then/else`, `while/do`, `repeat/until`
- Entrada/saída: `writeln`, `readln`
- Funções com parâmetros e retorno
- Comentários: `{ }` e `(* *)`

### Estágios implementados

| Estágio | Descrição |
|---|---|
| tp0 | Aquecimento: manipulação de strings, contagem de caracteres, palavras e frequências |
| tp1 | Fundações: nós da AST, Token, TokenType, Tabela de Símbolos (pilha de escopos) |
| tp2 | Scanner manual incremental com `nextToken()` |
| tp3 | Parser manual de descida recursiva |
| tp4 | Gramática ANTLR4 + Visitor para construção da AST |
| tp5 | Análise semântica com tabela de símbolos (pilha de escopos) |
| tp6 | Verificação de tipos Bottom-Up com inferência de `evalType` |
| tp7 | Geração de bytecode JVM via biblioteca ASM 9.6 |

---

## Como usar

### Pré-requisitos

- Java 17+
- Maven 3.8+

### Compilar o projeto

```bash
mvn package -DskipTests
```

### Executar o compilador

```bash
# Compilar um arquivo .pas (gera o .class)
java -jar target/comcet-1.0-SNAPSHOT.jar fatorial.pas

# Executar o .class gerado
java fatorial
# Saída: 120
```

### Modo por estágio (para testes individuais)

```bash
# Estágio 2 — tokens do Scanner
java -jar target/comcet-1.0-SNAPSHOT.jar --stage 2 fatorial.pas

# Estágio 4 — AST via ANTLR
java -jar target/comcet-1.0-SNAPSHOT.jar --stage 4 fatorial.pas

# Estágio 5 — análise semântica
java -jar target/comcet-1.0-SNAPSHOT.jar --stage 5 fatorial.pas

# Estágio 7 — compilação completa
java -jar target/comcet-1.0-SNAPSHOT.jar --stage 7 fatorial.pas
```

### Rodar os testes

```bash
mvn clean test
```

---

## Exemplo

**fatorial.pas**
```pascal
program fatorial;

var
  n, fat: integer;

begin
  n := 5;
  fat := 1;

  while n > 0 do
  begin
    fat := fat * n;
    n := n - 1;
  end;

  writeln(fat);
end.
```

**Compilar e executar:**
```bash
java -jar target/comcet-1.0-SNAPSHOT.jar fatorial.pas
# Compilado com sucesso: fatorial.class

java fatorial
# 120
```

**Tokens gerados pelo Scanner (--stage 2):**
```
[KEYWORD, "program"]
[IDENTIFIER, "fatorial"]
[DELIMITER, ";"]
[KEYWORD, "var"]
[IDENTIFIER, "n"]
[DELIMITER, ","]
[IDENTIFIER, "fat"]
[DELIMITER, ":"]
[KEYWORD, "integer"]
[DELIMITER, ";"]
...
[EOF, ""]
```

---

## Estrutura do Projeto

```
src/
├── main/
│   ├── antlr4/br/com/comcet/tp4/parser/
│   │   └── MiniPascal.g4          # Gramática da linguagem
│   └── java/br/com/comcet/
│       ├── Main.java               # Ponto de entrada unificado
│       ├── tp1/                    # AST, Token, TokenType, SymbolTable
│       ├── tp2/                    # Scanner manual + LexicalException
│       ├── tp3/                    # Parser manual de descida recursiva
│       ├── tp4/                    # MyVisitor (ANTLR → AST)
│       ├── tp5/                    # SemanticAnalyzer
│       ├── tp6/                    # TypeChecker
│       └── tp7/                    # CodeGenerator + MainCompiler
└── test/
    └── java/br/com/comcet/
        ├── tp2/ScannerTest.java
        ├── tp3/ParserTest.java
        ├── tp5/SemanticScopeTest.java
        └── tp6/TypeCheckerTest.java
```

---

## Tecnologias

- **Java 17**
- **Maven** — build e dependências
- **ANTLR4 4.13** — geração do parser a partir da gramática
- **ASM 9.6** — geração de bytecode JVM
- **JUnit 5** — testes automatizados

---

## Autora

**Marcela Freitas Mariano**  
Disciplina de Compiladores
