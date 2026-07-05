package br.com.comcet.tp2;

import br.com.comcet.tp1.scanner.Token;
import br.com.comcet.tp1.scanner.TokenType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScannerTest {

    // ── Teste do enunciado ────────────────────────────────────────────────
    @Test
    void reconheceDeclaracaoSimples() {
        // Exemplo direto do enunciado: "var x : integer;"
        String codigo = "var x : integer;";
        Scanner scanner = new Scanner(codigo);

        Token t1 = scanner.nextToken();
        assertEquals(TokenType.KEYWORD,    t1.type());
        assertEquals("var",               t1.text());

        Token t2 = scanner.nextToken();
        assertEquals(TokenType.IDENTIFIER, t2.type());
        assertEquals("x",                 t2.text());

        Token t3 = scanner.nextToken();
        assertEquals(TokenType.DELIMITER,  t3.type());
        assertEquals(":",                  t3.text());

        Token t4 = scanner.nextToken();
        assertEquals(TokenType.KEYWORD,    t4.type());
        assertEquals("integer",            t4.text());

        Token t5 = scanner.nextToken();
        assertEquals(TokenType.DELIMITER,  t5.type());
        assertEquals(";",                  t5.text());

        Token eof = scanner.nextToken();
        assertEquals(TokenType.EOF,        eof.type());
    }

    // ── Atribuição e expressão aritmética ─────────────────────────────────
    @Test
    void reconheceAtribuicaoEExpressao() {
        String codigo = "x := 10 + 5";
        Scanner scanner = new Scanner(codigo);

        Token t1 = scanner.nextToken();
        assertEquals(TokenType.IDENTIFIER, t1.type());
        assertEquals("x",                 t1.text());

        Token t2 = scanner.nextToken();
        assertEquals(TokenType.OPERATOR,   t2.type());
        assertEquals(":=",                 t2.text());

        Token t3 = scanner.nextToken();
        assertEquals(TokenType.NUMBER,     t3.type());
        assertEquals("10",                 t3.text());

        Token t4 = scanner.nextToken();
        assertEquals(TokenType.OPERATOR,   t4.type());
        assertEquals("+",                  t4.text());

        Token t5 = scanner.nextToken();
        assertEquals(TokenType.NUMBER,     t5.type());
        assertEquals("5",                  t5.text());

        assertEquals(TokenType.EOF, scanner.nextToken().type());
    }

    // ── Operadores relacionais com lookahead ─────────────────────────────
    @Test
    void reconheceOperadoresRelacionais() {
        String codigo = "a <= b <> c >= d";
        Scanner scanner = new Scanner(codigo);

        assertEquals("a",   scanner.nextToken().text());
        Token le = scanner.nextToken();
        assertEquals(TokenType.OPERATOR, le.type());
        assertEquals("<=",               le.text());

        assertEquals("b",   scanner.nextToken().text());
        Token ne = scanner.nextToken();
        assertEquals(TokenType.OPERATOR, ne.type());
        assertEquals("<>",               ne.text());

        assertEquals("c",   scanner.nextToken().text());
        Token ge = scanner.nextToken();
        assertEquals(TokenType.OPERATOR, ge.type());
        assertEquals(">=",               ge.text());

        assertEquals("d",   scanner.nextToken().text());
        assertEquals(TokenType.EOF, scanner.nextToken().type());
    }

    // ── Comentários { } e (* *) devem ser ignorados ──────────────────────
    @Test
    void ignoraComentarios() {
        String codigo = "x { isto é um comentário } := (* outro comentário *) 42";
        Scanner scanner = new Scanner(codigo);

        Token t1 = scanner.nextToken();
        assertEquals(TokenType.IDENTIFIER, t1.type());
        assertEquals("x",                  t1.text());

        Token t2 = scanner.nextToken();
        assertEquals(TokenType.OPERATOR,   t2.type());
        assertEquals(":=",                  t2.text());

        Token t3 = scanner.nextToken();
        assertEquals(TokenType.NUMBER,     t3.type());
        assertEquals("42",                 t3.text());

        assertEquals(TokenType.EOF, scanner.nextToken().type());
    }

    // ── Erro léxico com linha e coluna corretos ──────────────────────────
    @Test
    void lancaExcecaoParaCaractereInvalido() {
        String codigo = "x := 10 @ 5";
        Scanner scanner = new Scanner(codigo);

        scanner.nextToken(); // x
        scanner.nextToken(); // :=
        scanner.nextToken(); // 10

        LexicalException ex = assertThrows(LexicalException.class, scanner::nextToken);
        assertTrue(ex.getMessage().contains("linha 1"));
        assertTrue(ex.getMessage().contains("coluna 9"));
    }
}