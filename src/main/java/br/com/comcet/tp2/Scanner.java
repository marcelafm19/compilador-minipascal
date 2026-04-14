package br.com.comcet.tp2;

import br.com.comcet.tp1.scanner.IScanner;
import br.com.comcet.tp1.scanner.Token;
import br.com.comcet.tp1.scanner.TokenType;

import java.util.HashMap;
import java.util.Map;

public class Scanner implements IScanner {
    private final String source;
    private int pos;
    private int line;
    private int column;

    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("var", TokenType.KEYWORD);
        keywords.put("integer", TokenType.KEYWORD);
        keywords.put("if", TokenType.IF); // Atualizado para usar os tipos corretos do Enum
        keywords.put("then", TokenType.THEN);
        keywords.put("else", TokenType.ELSE);
        keywords.put("while", TokenType.WHILE);
        keywords.put("do", TokenType.DO);
        keywords.put("print", TokenType.PRINT);
    }

    public Scanner(String source) {
        this.source = source;
        this.pos = 0;
        this.line = 1;
        this.column = 1;
    }

    private char peek() {
        if (pos >= source.length())
            return '\0';
        return source.charAt(pos);
    }

    private char advance() {
        char c = source.charAt(pos++);
        if (c == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        return c;
    }

    private void skipWhitespaceAndComments() {
        while (true) {
            char c = peek();
            if (Character.isWhitespace(c)) {
                advance();
            } else if (c == '{') {
                advance();
                while (peek() != '}' && peek() != '\0') {
                    advance();
                }
                if (peek() == '}') advance();
            } else {
                break;
            }
        }
    }

    @Override
    public Token nextToken() {
        skipWhitespaceAndComments();
        
        int startLine = line;
        int startCol = column;

        if (pos >= source.length()) {
            return new Token(TokenType.EOF, "", startLine, startCol);
        }

        char c = peek();

        if (Character.isLetter(c)) {
            StringBuilder sb = new StringBuilder();
            while (Character.isLetterOrDigit(peek())) {
                sb.append(advance());
            }
            String text = sb.toString();
            TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);
            return new Token(type, text, startLine, startCol);
        }

        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            while (Character.isDigit(peek())) {
                sb.append(advance());
            }
            return new Token(TokenType.NUMBER, sb.toString(), startLine, startCol);
        }

        c = advance();
        switch (c) {
            case '+': return new Token(TokenType.PLUS, "+", startLine, startCol);
            case '-': return new Token(TokenType.MINUS, "-", startLine, startCol);
            case '*': return new Token(TokenType.TIMES, "*", startLine, startCol);
            case '/': return new Token(TokenType.DIV, "/", startLine, startCol);
            case '(': return new Token(TokenType.LPAREN, "(", startLine, startCol);
            case ')': return new Token(TokenType.RPAREN, ")", startLine, startCol);
            case '{': return new Token(TokenType.LBRACE, "{", startLine, startCol);
            case '}': return new Token(TokenType.RBRACE, "}", startLine, startCol);
            case ':':
                if (peek() == '=') {
                    advance();
                    return new Token(TokenType.ASSIGN, ":=", startLine, startCol);
                }
                return new Token(TokenType.COLON, ":", startLine, startCol);
            case '=': return new Token(TokenType.OPERATOR, "=", startLine, startCol);
            case ';': return new Token(TokenType.SEMICOLON, ";", startLine, startCol);
            default:
                throw new LexicalException("Caractere inválido", c, startLine, startCol);
        }
    }
}