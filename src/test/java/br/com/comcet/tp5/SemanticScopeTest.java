package br.com.comcet.tp5;

import br.com.comcet.tp1.ast.AstNode;
import br.com.comcet.tp4.MyVisitor;
import br.com.comcet.tp4.parser.MiniPascalLexer;
import br.com.comcet.tp4.parser.MiniPascalParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SemanticScopeTest {

    private AstNode parse(String codigo) {
        CharStream input = CharStreams.fromString(codigo);
        MiniPascalLexer lexer = new MiniPascalLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniPascalParser parser = new MiniPascalParser(tokens);
        ParseTree tree = parser.program();
        return new MyVisitor().visit(tree);
    }

    @Test
    void falhaVariavelNaoDeclarada() {
        // Tenta usar o 'x' sem declarar no bloco var
        String codigo = "program p; begin x := 1; end.";
        AstNode ast = parse(codigo);

        SemanticAnalyzer sem = new SemanticAnalyzer();
        sem.analyze(ast); 

        assertTrue(sem.hasErrors(), "Deveria detetar erro de variável não declarada."); 
        assertTrue(sem.getErrors().stream().anyMatch(e -> e.contains("não declarada")), 
            "A mensagem deve informar que a variável não foi declarada.");
    }

    @Test
    void falhaDuplaDeclaracao() {
        // Declara a variável 'x' duas vezes no mesmo escopo
        String codigo = "program p; var x: integer; x: boolean; begin x := 1; end.";
        AstNode ast = parse(codigo);

        SemanticAnalyzer sem = new SemanticAnalyzer();
        sem.analyze(ast);

        assertTrue(sem.hasErrors(), "Deveria detetar erro de dupla declaração.");
        assertTrue(sem.getErrors().stream().anyMatch(e -> e.contains("já declarada")), 
            "A mensagem deve informar que a variável já foi declarada.");
    }

    @Test
    void sucessoVariavelDeclarada() {
        // Código correto, a variável é declarada e depois usada
        String codigo = "program p; var x: integer; begin x := 1; end.";
        AstNode ast = parse(codigo);

        SemanticAnalyzer sem = new SemanticAnalyzer();
        sem.analyze(ast);

        assertFalse(sem.hasErrors(), "Não deveria ter erros, a variável foi declarada corretamente.\nErros encontrados: " + sem.getErrors());
    }
}