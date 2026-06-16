package br.com.comcet.tp6;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import br.com.comcet.tp1.ast.AstNode;
import br.com.comcet.tp4.MyVisitor;
import br.com.comcet.tp4.parser.MiniPascalLexer;
import br.com.comcet.tp4.parser.MiniPascalParser;
import br.com.comcet.tp5.SemanticAnalyzer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;

public class FrontendSmokeTest {
  
    private void compileFrontendOnly(String codigo) throws Exception {
        // 1. Análise Léxica e Sintática (ANTLR4)
        CharStream input = CharStreams.fromString(codigo);
        MiniPascalLexer lexer = new MiniPascalLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniPascalParser parser = new MiniPascalParser(tokens);

        ParseTree tree = parser.program();

        // Se o Parser do ANTLR detectar erros de sintaxe, lança exceção
        if (parser.getNumberOfSyntaxErrors() > 0) {
            throw new Exception("Erro Sintático detectado pelo ANTLR4");
        }

        // 2. Geração da Árvore Sintática (AST)
        MyVisitor visitor = new MyVisitor();
        AstNode ast = visitor.visit(tree);

        // 3. Análise Semântica (Escopo)
        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer();
        semanticAnalyzer.analyze(ast);

        // Se o SemanticAnalyzer registrar erros em sua lista interna, lança exceção
        if (semanticAnalyzer.hasErrors()) {
            throw new Exception("Erro Semântico: " + String.join(", ", semanticAnalyzer.getErrors()));
        }
    }

    @Test
    void testaProgramaValido() {
        // Um programa válido deve passar por todo o frontend sem lançar exceções.
        String codigo = "program p; var x: integer; begin x := 10; end.";
        
        assertDoesNotThrow(() -> {
            compileFrontendOnly(codigo); // Roda Léxico, Sintático e Semântico reais
        });
    }

    @Test
    void testaErroSintatico() {
        // Falta o ponto-e-vírgula depois de 'p'
        String codigo = "program p var x: integer; begin x := 10; end.";
        
        assertThrows(Exception.class, () -> {
            compileFrontendOnly(codigo);
        });
    }

    @Test
    void testaErroSemanticoVariavelNaoDeclarada() {
        // Uso de variável 'y' que não foi declarada
        String codigo = "program p; var x: integer; begin y := 10; end.";
        
        assertThrows(Exception.class, () -> {
            compileFrontendOnly(codigo);
        });
    }
}