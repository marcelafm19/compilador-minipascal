package br.com.comcet.tp7;

import br.com.comcet.tp1.ast.Program;
import br.com.comcet.tp4.MyVisitor;
import br.com.comcet.tp4.parser.MiniPascalLexer;
import br.com.comcet.tp4.parser.MiniPascalParser;
import br.com.comcet.tp5.SemanticAnalyzer;
import br.com.comcet.tp6.TypeChecker;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

import java.nio.file.Path;
import java.io.FileOutputStream;

public class MainCompiler {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java br.com.comcet.tp7.MainCompiler <arquivo.pas>");
            return;
        }

        String arquivoPas = args[0];
        String className = arquivoPas.replace(".pas", "");

        try {
            // 1. e 2. Ler, Analisar (Lexer) e Fazer o Parser (Sintático via ANTLR4)
            CharStream input = CharStreams.fromPath(Path.of(arquivoPas));
            MiniPascalLexer lexer = new MiniPascalLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MiniPascalParser parser = new MiniPascalParser(tokens);
            
            ParseTree tree = parser.program();
            MyVisitor visitor = new MyVisitor();
            Program ast = (Program) visitor.visit(tree);

            // 3. Semântico e Verificação de Tipos
            SemanticAnalyzer semantic = new SemanticAnalyzer();
            semantic.analyze(ast);
            
            TypeChecker typeChecker = new TypeChecker();
            typeChecker.check(ast);

            if (semantic.hasErrors() || typeChecker.hasErrors()) {
                System.err.println("Erros encontrados! Compilação abortada.");
                if (semantic.hasErrors()) semantic.getErrors().forEach(System.err::println);
                if (typeChecker.hasErrors()) typeChecker.getErrors().forEach(System.err::println);
                return;
            }

            // 4. Geração de Código ASM (Bytecode)
            CodeGenerator codegen = new CodeGenerator();
            byte[] bytecode = codegen.generate(ast, className);

            // 5. Salva o executável gerado
            try (FileOutputStream fos = new FileOutputStream(className + ".class")) {
                fos.write(bytecode);
            }
            
            System.out.println("Compilado com sucesso: " + className + ".class");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}