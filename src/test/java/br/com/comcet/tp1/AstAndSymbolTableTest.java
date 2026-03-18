package br.com.comcet.tp1;

import br.com.comcet.tp1.ast.*;
import br.com.comcet.tp1.symbols.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class AstAndSymbolTableTest {

    @Test
    void criaAstAtribuicaoSoma() {
        // Representando: x := 10 + 5;
        Expression dez = new Literal(10);
        Expression cinco = new Literal(5);
        Expression soma = new BinaryExpression(dez, cinco, "+");
        Command atrib = new AssignmentCommand(new Identifier("x"), soma);

        // Verifica a estrutura
        assertTrue(atrib instanceof AssignmentCommand);
        AssignmentCommand a = (AssignmentCommand) atrib;
        assertEquals("x", a.id.name);

        assertTrue(a.expr instanceof BinaryExpression);
        BinaryExpression b = (BinaryExpression) a.expr;
        assertEquals("+", b.operator);
    }

    @Test
    void symbolTableAddGet() {
        SymbolTable st = new SymbolTable();
        Symbol sx = new Symbol("x", "Integer");
        st.add("x", sx);

        assertSame(sx, st.get("x"));
        assertNull(st.get("y"));
    }

    @Test
    void symbolTableNaoPermiteDuplicado() {
        SymbolTable st = new SymbolTable();
        st.add("x", new Symbol("x", "Integer"));

        // O professor pediu IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> {
            st.add("x", new Symbol("x", "Integer"));
        });
    }
}