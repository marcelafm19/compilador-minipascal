package br.com.comcet;

import br.com.comcet.tp1.ast.AstNode;
import br.com.comcet.tp1.ast.Program;
import br.com.comcet.tp1.scanner.Token;
import br.com.comcet.tp1.scanner.TokenType;
import br.com.comcet.tp2.Scanner;
import br.com.comcet.tp3.Parser;
import br.com.comcet.tp4.MyVisitor;
import br.com.comcet.tp4.parser.MiniPascalLexer;
import br.com.comcet.tp4.parser.MiniPascalParser;
import br.com.comcet.tp5.SemanticAnalyzer;
import br.com.comcet.tp6.TypeChecker;
import br.com.comcet.tp7.CodeGenerator;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.FileOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Ponto de entrada unificado do compilador MiniPascal.
 *
 * Uso:
 *   java -jar target/comcet-1.0-SNAPSHOT.jar --stage <N> <arquivo.pas>
 *
 *   --stage 2  → Analisador Léxico   : imprime tokens  [TIPO, "lexema"]
 *   --stage 3  → Parser Manual       : imprime a AST
 *   --stage 4  → Parser ANTLR4       : imprime a AST via ANTLR
 *   --stage 5  → Análise Semântica   : checa escopos e variáveis
 *   --stage 6  → Verificação de Tipos: checa tipos das expressões
 *   --stage 7  → Geração de Código   : produz o arquivo .class
 */
public class Main {

    public static void main(String[] args) {
        // ── Validação dos argumentos ──────────────────────────────────────
        if (args.length < 3 || !args[0].equals("--stage")) {
            printUsage();
            System.exit(1);
        }

        int stage;
        try {
            stage = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Erro: estágio inválido '" + args[1] + "'. Use um número entre 2 e 7.");
            System.exit(1);
            return;
        }

        String filePath = args[2];

        if (!filePath.endsWith(".pas")) {
            System.err.println("Erro: arquivo deve ter extensão .pas");
            System.exit(1);
        }

        // ── Roteamento por estágio ────────────────────────────────────────
        try {
            switch (stage) {
                case 2  -> runStage2(filePath);
                case 3  -> runStage3(filePath);
                case 4  -> runStage4(filePath);
                case 5  -> runStage5(filePath);
                case 6  -> runStage6(filePath);
                case 7  -> runStage7(filePath);
                default -> {
                    System.err.println("Erro: estágio " + stage + " não reconhecido. Use 2 a 7.");
                    System.exit(1);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            System.exit(1);
        }
    }

    // ── Estágio 2: Analisador Léxico ──────────────────────────────────────
    // Saída: [TIPO, "lexema"] por linha, até EOF
    private static void runStage2(String filePath) throws Exception {
        String source = Files.readString(Path.of(filePath));
        Scanner scanner = new Scanner(source);
        Token token;
        do {
            token = scanner.nextToken();
            System.out.printf("[%s, \"%s\"]%n", token.type(), token.text());
        } while (token.type() != TokenType.EOF);
    }

    // ── Estágio 3: Parser Manual ──────────────────────────────────────────
    // Saída: árvore AST em texto
    private static void runStage3(String filePath) throws Exception {
        String source = Files.readString(Path.of(filePath));
        Scanner scanner = new Scanner(source);
        Parser parser   = new Parser(scanner);

        Program ast = parser.parseProgram();

        AstNode.isEtapa3 = true;
        System.out.println(ast.printTree());
    }

    // ── Estágio 4: Parser ANTLR4 ─────────────────────────────────────────
    // Saída: árvore AST em texto (construída via MyVisitor)
    private static void runStage4(String filePath) throws Exception {
        CharStream input = CharStreams.fromPath(Path.of(filePath));
        MiniPascalLexer lexer   = new MiniPascalLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniPascalParser parser  = new MiniPascalParser(tokens);

        ParseTree tree = parser.program();
        AstNode ast    = new MyVisitor().visit(tree);

        AstNode.isEtapa3 = true;
        System.out.println(ast.printTree());
    }

    // ── Estágio 5: Análise Semântica ─────────────────────────────────────
    // Saída: "OK" ou lista de erros semânticos
    private static void runStage5(String filePath) throws Exception {
        Program ast = buildAst(filePath);

        SemanticAnalyzer semantic = new SemanticAnalyzer();
        semantic.analyze(ast);

        if (semantic.hasErrors()) {
            semantic.getErrors().forEach(System.err::println);
            System.exit(1);
        } else {
            System.out.println("OK");
        }
    }

    // ── Estágio 6: Verificação de Tipos ──────────────────────────────────
    // Saída: "OK" ou lista de erros de tipo
    private static void runStage6(String filePath) throws Exception {
        Program ast = buildAst(filePath);

        SemanticAnalyzer semantic = new SemanticAnalyzer();
        semantic.analyze(ast);

        TypeChecker typeChecker = new TypeChecker();
        typeChecker.check(ast);

        boolean hasErrors = semantic.hasErrors() || typeChecker.hasErrors();
        if (hasErrors) {
            if (semantic.hasErrors())    semantic.getErrors().forEach(System.err::println);
            if (typeChecker.hasErrors()) typeChecker.getErrors().forEach(System.err::println);
            System.exit(1);
        } else {
            System.out.println("OK");
        }
    }

    // ── Estágio 7: Geração de Código ─────────────────────────────────────
    // Saída: arquivo .class gerado no diretório atual
    private static void runStage7(String filePath) throws Exception {
        Program ast       = buildAst(filePath);
        String className  = Path.of(filePath).getFileName().toString().replace(".pas", "");

        SemanticAnalyzer semantic = new SemanticAnalyzer();
        semantic.analyze(ast);

        TypeChecker typeChecker = new TypeChecker();
        typeChecker.check(ast);

        if (semantic.hasErrors() || typeChecker.hasErrors()) {
            System.err.println("Compilação abortada: erros semânticos encontrados.");
            if (semantic.hasErrors())    semantic.getErrors().forEach(System.err::println);
            if (typeChecker.hasErrors()) typeChecker.getErrors().forEach(System.err::println);
            System.exit(1);
        }

        CodeGenerator codegen = new CodeGenerator();
        byte[] bytecode = codegen.generate(ast, className);

        try (FileOutputStream fos = new FileOutputStream(className + ".class")) {
            fos.write(bytecode);
        }

        System.out.println("Compilado com sucesso: " + className + ".class");
    }

    // ── Utilitário: constrói a AST via ANTLR4 (usado pelos estágios 5-7) ─
    private static Program buildAst(String filePath) throws Exception {
        CharStream input         = CharStreams.fromPath(Path.of(filePath));
        MiniPascalLexer lexer    = new MiniPascalLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniPascalParser parser  = new MiniPascalParser(tokens);

        ParseTree tree = parser.program();
        return (Program) new MyVisitor().visit(tree);
    }

    // ── Ajuda ─────────────────────────────────────────────────────────────
    private static void printUsage() {
        System.out.println("Uso: java -jar comcet-1.0-SNAPSHOT.jar --stage <N> <arquivo.pas>");
        System.out.println();
        System.out.println("  --stage 2   Analisador Léxico  → tokens [TIPO, \"lexema\"]");
        System.out.println("  --stage 3   Parser Manual      → AST em texto");
        System.out.println("  --stage 4   Parser ANTLR4      → AST em texto");
        System.out.println("  --stage 5   Análise Semântica  → OK ou erros");
        System.out.println("  --stage 6   Checagem de Tipos  → OK ou erros");
        System.out.println("  --stage 7   Geração de Código  → arquivo .class");
    }
}