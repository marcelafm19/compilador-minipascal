package br.com.comcet.tp3;

import br.com.comcet.tp1.ast.*;
import br.com.comcet.tp2.Scanner;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    @Test
    void respeitaPrecedenciaMultiplicacao() {
        String codigo = "x := 10 + 5 * 2;";

        Scanner scanner = new Scanner(codigo);
        Parser parser = new Parser(scanner);

        Command cmd = parser.parseCommand();

        assertTrue(cmd instanceof AssignmentCommand);
        AssignmentCommand a = (AssignmentCommand) cmd;

        assertTrue(a.expr instanceof BinaryExpression);
        BinaryExpression plus = (BinaryExpression) a.expr;
        assertEquals("+", plus.operator);

        assertTrue(plus.left instanceof Literal);
        assertTrue(plus.right instanceof BinaryExpression);

        BinaryExpression mult = (BinaryExpression) plus.right;
        assertEquals("*", mult.operator);
    }

    @Test
    void parentesesAlteramPrecedencia() {
        String codigo = "x := (10 + 5) * 2;";

        Scanner scanner = new Scanner(codigo);
        Parser parser = new Parser(scanner);

        Command cmd = parser.parseCommand();

        assertTrue(cmd instanceof AssignmentCommand);
        AssignmentCommand a = (AssignmentCommand) cmd;

        assertTrue(a.expr instanceof BinaryExpression);
        BinaryExpression mult = (BinaryExpression) a.expr;
        assertEquals("*", mult.operator);

        assertTrue(mult.left instanceof BinaryExpression);
        BinaryExpression plus = (BinaryExpression) mult.left;
        assertEquals("+", plus.operator);
    }
}