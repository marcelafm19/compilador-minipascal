package br.com.comcet.tp2;

import br.com.comcet.tp1.scanner.Token;
import br.com.comcet.tp1.scanner.TokenType;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ScannerTest {

    @Test
    void reconheceAtribuicaoEExpressao() {
        String codigo = "x := 10 + 5";
        Scanner scanner = new Scanner(codigo);

        Token t1 = scanner.nextToken();
        assertEquals(TokenType.IDENTIFIER, t1.type());
        assertEquals("x", t1.text());

        Token t2 = scanner.nextToken();
        assertEquals(TokenType.ASSIGN, t2.type());
        assertEquals(":=", t2.text());

        Token t3 = scanner.nextToken();
        assertEquals(TokenType.LITERAL_INT, t3.type());
        assertEquals("10", t3.text());

        Token t4 = scanner.nextToken();
        assertEquals(TokenType.PLUS, t4.type());
        assertEquals("+", t4.text());
        
        Token t5 = scanner.nextToken();
        assertEquals(TokenType.LITERAL_INT, t5.type());
        assertEquals("5", t5.text());

        Token eof = scanner.nextToken();
        assertEquals(TokenType.EOF, eof.type());
    }

    @Test
    void lancaExcecaoParaCaractereInvalido() {
        String codigo = "x := 10 @ 5";
        Scanner scanner = new Scanner(codigo);
        
        scanner.nextToken(); // consome 'x'
        scanner.nextToken(); // consome ':='
        scanner.nextToken(); // consome '10'
        
        LexicalException exception = assertThrows(LexicalException.class, () -> {
            scanner.nextToken();
        });
        
        assertTrue(exception.getMessage().contains("linha 1"));
        assertTrue(exception.getMessage().contains("coluna 9"));
    }
}