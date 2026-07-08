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

### Modo por estágio

O compilador aceita o flag `--stage N` para executar e inspecionar cada fase individualmente.

---

#### `--stage 2` — Analisador Léxico (Scanner)

Executa o Scanner manual e imprime todos os tokens reconhecidos no formato `[TIPO, "lexema"]`.

```bash
java -jar target/comcet-1.0-SNAPSHOT.jar --stage 2 fatorial.pas
```

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
[KEYWORD, "begin"]
[IDENTIFIER, "n"]
[OPERATOR, ":="]
[NUMBER, "5"]
[DELIMITER, ";"]
[IDENTIFIER, "fat"]
[OPERATOR, ":="]
[NUMBER, "1"]
[DELIMITER, ";"]
[KEYWORD, "while"]
[IDENTIFIER, "n"]
[OPERATOR, ">"]
[NUMBER, "0"]
[KEYWORD, "do"]
[KEYWORD, "begin"]
[IDENTIFIER, "fat"]
[OPERATOR, ":="]
[IDENTIFIER, "fat"]
[OPERATOR, "*"]
[IDENTIFIER, "n"]
[DELIMITER, ";"]
[IDENTIFIER, "n"]
[OPERATOR, ":="]
[IDENTIFIER, "n"]
[OPERATOR, "-"]
[NUMBER, "1"]
[DELIMITER, ";"]
[KEYWORD, "end"]
[DELIMITER, ";"]
[KEYWORD, "writeln"]
[DELIMITER, "("]
[IDENTIFIER, "fat"]
[DELIMITER, ")"]
[DELIMITER, ";"]
[KEYWORD, "end"]
[DELIMITER, "."]
[EOF, ""]
```

---

#### `--stage 3` — Parser Manual (Descida Recursiva)

Executa o parser manual (tp3) e imprime a AST construída a partir do programa.

```bash
java -jar target/comcet-1.0-SNAPSHOT.jar --stage 3 fatorial.pas
```

```
Program
  WhileCommand
    BinaryExpression >
      Identifier: n
      Literal: 0
    BlockCommand
      AssignmentCommand
        Identifier: fat
        BinaryExpression *
          Identifier: fat
          Identifier: n
      AssignmentCommand
        Identifier: n
        BinaryExpression -
          Identifier: n
          Literal: 1
  PrintCommand
    Identifier: fat
```

---

#### `--stage 4` — Parser ANTLR4

Executa o parser gerado pelo ANTLR4 com o MyVisitor e imprime a AST completa, incluindo declarações de variáveis e funções.

```bash
java -jar target/comcet-1.0-SNAPSHOT.jar --stage 4 fatorial.pas
```

```
Program
  VarDeclList
    VarDecl: n : integer
    VarDecl: fat : integer
  AssignmentCommand
    Identifier: n
    Literal: 5
  AssignmentCommand
    Identifier: fat
    Literal: 1
  WhileCommand
    BinaryExpression >
      Identifier: n
      Literal: 0
    BlockCommand
      AssignmentCommand
        Identifier: fat
        BinaryExpression *
          Identifier: fat
          Identifier: n
      AssignmentCommand
        Identifier: n
        BinaryExpression -
          Identifier: n
          Literal: 1
  PrintCommand
    Identifier: fat
```

---

#### `--stage 5` — Análise Semântica (Escopos)

Verifica se todas as variáveis foram declaradas antes de usar e se não há duplicatas. Imprime `OK` ou lista de erros.

```bash
java -jar target/comcet-1.0-SNAPSHOT.jar --stage 5 fatorial.pas
```

```
OK
```

Exemplo com erro (variável não declarada):
```
Erro Semântico na linha 8: Variável 'x' não declarada.
```

---

#### `--stage 6` — Verificação de Tipos

Executa semântica + checagem de tipos. Verifica se operações são compatíveis e se condições de `if`/`while` são booleanas. Imprime `OK` ou lista de erros.

```bash
java -jar target/comcet-1.0-SNAPSHOT.jar --stage 6 fatorial.pas
```

```
OK
```

Exemplo com erro (tipo incompatível):
```
Erro Semântico na linha 5: Operação '+' não suportada para tipos INTEGER e BOOLEAN.
Erro Semântico na linha 9: Condição do 'if' deve ser booleana.
```

---

#### `--stage 7` — Geração de Código (Compilação Completa)

Executa o pipeline completo: léxico → sintático → semântico → tipos → bytecode. Gera o arquivo `.class` no diretório atual.

```bash
java -jar target/comcet-1.0-SNAPSHOT.jar --stage 7 fatorial.pas
# Compilado com sucesso: fatorial.class

java fatorial
# 120
```

Equivalente ao formato legado:
```bash
java -jar target/comcet-1.0-SNAPSHOT.jar fatorial.pas
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

> Veja a saída completa de todos os estágios na seção [Modo por estágio](#modo-por-estágio) acima.

---

## Estrutura do Projeto

```
compilador_marcela/
├── fatorial.pas                        # Programa de exemplo
├── pom.xml                             # Dependências Maven (ANTLR4, ASM, JUnit5)
│
└── src/
    ├── main/
    │   ├── antlr4/br/com/comcet/tp4/parser/
    │   │   └── MiniPascal.g4           # Gramática completa da linguagem
    │   │
    │   └── java/br/com/comcet/
    │       ├── Main.java               # Ponto de entrada unificado (--stage N)
    │       │
    │       ├── tp0/
    │       │   └── App.java            # Estatísticas de texto (aquecimento)
    │       │
    │       ├── tp1/
    │       │   ├── MainAST.java
    │       │   ├── ast/
    │       │   │   ├── AstNode.java            # Classe base de todos os nós
    │       │   │   ├── Expression.java         # Base das expressões (tem evalType)
    │       │   │   ├── Command.java            # Base dos comandos
    │       │   │   ├── Program.java            # Nó raiz da AST
    │       │   │   ├── AssignmentCommand.java  # x := expr
    │       │   │   ├── IfCommand.java          # if/then/else
    │       │   │   ├── WhileCommand.java       # while/do
    │       │   │   ├── RepeatCommand.java      # repeat/until
    │       │   │   ├── BlockCommand.java       # begin...end
    │       │   │   ├── PrintCommand.java       # writeln(expr)
    │       │   │   ├── ReadCommand.java        # readln(id)
    │       │   │   ├── FunctionDecl.java       # declaração de função
    │       │   │   ├── FunctionCall.java       # chamada de função
    │       │   │   ├── BinaryExpression.java   # a + b, a > b, a and b
    │       │   │   ├── UnaryExpression.java    # not expr, -expr
    │       │   │   ├── Identifier.java         # variável
    │       │   │   ├── Literal.java            # 10, true, 'texto'
    │       │   │   ├── VarDecl.java            # declaração de variável
    │       │   │   ├── VarDeclList.java        # lista de declarações
    │       │   │   └── Type.java               # enum: INTEGER, BOOLEAN, STRING, VOID
    │       │   ├── scanner/
    │       │   │   ├── IScanner.java           # interface com nextToken()
    │       │   │   ├── Token.java              # par <tipo, texto, linha, coluna>
    │       │   │   └── TokenType.java          # KEYWORD, IDENTIFIER, NUMBER, OPERATOR...
    │       │   └── symbols/
    │       │       ├── Symbol.java             # nome + tipo + isFunction
    │       │       └── SymbolTable.java        # pilha de escopos (Deque<Map>)
    │       │
    │       ├── tp2/
    │       │   ├── Scanner.java            # Lexer manual incremental
    │       │   ├── LexicalException.java   # erro com linha e coluna
    │       │   └── MainScanner.java        # loop de impressão de tokens
    │       │
    │       ├── tp3/
    │       │   ├── Parser.java             # Parser manual de descida recursiva
    │       │   └── MainParser.java         # ponto de entrada do parser manual
    │       │
    │       ├── tp4/
    │       │   ├── MyVisitor.java          # ANTLR Visitor → constrói a AST
    │       │   └── MainAntlr.java          # ponto de entrada ANTLR
    │       │
    │       ├── tp5/
    │       │   ├── SemanticAnalyzer.java   # checa escopos e declarações
    │       │   └── SymbolTable.java        # tabela de símbolos do analisador
    │       │
    │       ├── tp6/
    │       │   └── TypeChecker.java        # inferência de tipos Bottom-Up
    │       │
    │       └── tp7/
    │           ├── CodeGenerator.java      # geração de bytecode JVM via ASM
    │           └── MainCompiler.java       # ponto de entrada legado (.pas direto)
    │
    └── test/
        └── java/br/com/comcet/
            ├── tp0/AppTest.java                # 3 testes: estatísticas de texto
            ├── tp1/AstAndSymbolTableTest.java  # 3 testes: AST e tabela de símbolos
            ├── tp2/ScannerTest.java            # 5 testes: tokens, lookahead, erros
            ├── tp3/ParserTest.java             # 2 testes: precedência de expressões
            ├── tp4/AntlrVisitorTest.java       # 1 teste: AST via ANTLR
            ├── tp5/SemanticScopeTest.java      # 3 testes: escopos e declarações
            └── tp6/
                ├── TypeCheckerTest.java        # 5 testes: verificação de tipos
                └── FrontendSmokeTest.java      # 3 testes: pipeline completo
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
