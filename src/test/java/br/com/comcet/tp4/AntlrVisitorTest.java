package br.com.comcet.tp4;

import br.com.comcet.tp1.ast.*;
import br.com.comcet.tp4.parser.MiniPascalLexer;
import br.com.comcet.tp4.parser.MiniPascalParser;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AntlrVisitorTest {

    @Test
    void visitorConstroiAstParaAtribuicaoComPrecedencia() {
        String codigo = "program p; var x: integer; begin x := 10 + 5 * 2; end.";
        
        CharStream input = CharStreams.fromString(codigo);
        MiniPascalLexer lexer = new MiniPascalLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniPascalParser parser = new MiniPascalParser(tokens);
        
        ParseTree tree = parser.program();
        MyVisitor visitor = new MyVisitor();
        AstNode ast = visitor.visit(tree);
        
        assertNotNull(ast);
        assertTrue(ast instanceof Program);
        
        Program p = (Program) ast;
        assertEquals(1, p.commands.size());
        
        Command cmd = p.commands.get(0);
        assertTrue(cmd instanceof AssignmentCommand);
        
        AssignmentCommand assignment = (AssignmentCommand) cmd;
        assertEquals("x", assignment.id.name);
        
        assertTrue(assignment.expr instanceof BinaryExpression);
        BinaryExpression plus = (BinaryExpression) assignment.expr;
        assertEquals("+", plus.operator);
        
        assertTrue(plus.right instanceof BinaryExpression);
        BinaryExpression mult = (BinaryExpression) plus.right;
        assertEquals("*", mult.operator);
    }
}