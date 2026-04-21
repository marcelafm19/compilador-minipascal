package br.com.comcet.tp4;

import br.com.comcet.tp1.ast.AstNode;
import br.com.comcet.tp4.parser.MiniPascalLexer;
import br.com.comcet.tp4.parser.MiniPascalParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

public class MainAntlr {
    public static void main(String[] args) {
        String codigo = "program teste;\n" +
                        "var x: integer;\n" +
                        "begin\n" +
                        "  x := 10 + 5 * 2;\n" +
                        "end.";

        try {
            CharStream input = CharStreams.fromString(codigo);
            MiniPascalLexer lexer = new MiniPascalLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);
            MiniPascalParser parser = new MiniPascalParser(tokens);

            ParseTree tree = parser.program();
            
            MyVisitor visitor = new MyVisitor();
            AstNode ast = visitor.visit(tree);

            AstNode.isEtapa3 = true;
            System.out.println("AST gerada pelo ANTLR4 com sucesso:\n");
            System.out.println(ast.printTree());

        } catch (Exception e) {
            System.err.println("Erro durante a análise: " + e.getMessage());
        }
    }
}