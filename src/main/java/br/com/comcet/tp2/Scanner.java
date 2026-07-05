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

    // Todas as palavras reservadas do MiniPascal mapeadas para KEYWORD
    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("program",   TokenType.KEYWORD);
        keywords.put("var",       TokenType.KEYWORD);
        keywords.put("begin",     TokenType.KEYWORD);
        keywords.put("end",       TokenType.KEYWORD);
        keywords.put("integer",   TokenType.KEYWORD);
        keywords.put("boolean",   TokenType.KEYWORD);
        keywords.put("string",    TokenType.KEYWORD);
        keywords.put("if",        TokenType.KEYWORD);
        keywords.put("then",      TokenType.KEYWORD);
        keywords.put("else",      TokenType.KEYWORD);
        keywords.put("while",     TokenType.KEYWORD);
        keywords.put("do",        TokenType.KEYWORD);
        keywords.put("repeat",    TokenType.KEYWORD);
        keywords.put("until",     TokenType.KEYWORD);
        keywords.put("function",  TokenType.KEYWORD);
        keywords.put("procedure", TokenType.KEYWORD);
        keywords.put("and",       TokenType.KEYWORD);
        keywords.put("or",        TokenType.KEYWORD);
        keywords.put("not",       TokenType.KEYWORD);
        keywords.put("true",      TokenType.KEYWORD);
        keywords.put("false",     TokenType.KEYWORD);
        keywords.put("writeln",   TokenType.KEYWORD);
        keywords.put("readln",    TokenType.KEYWORD);
        keywords.put("div",       TokenType.KEYWORD);
        keywords.put("mod",       TokenType.KEYWORD);
    }

    public Scanner(String source) {
        this.source = source;
        this.pos    = 0;
        this.line   = 1;
        this.column = 1;
    }

    // Retorna o caractere atual sem consumir; '\0' se chegou ao fim
    private char peek() {
        if (pos >= source.length()) return '\0';
        return source.charAt(pos);
    }

    // Retorna o caractere seguinte ao atual sem consumir (lookahead de 2)
    private char peekNext() {
        if (pos + 1 >= source.length()) return '\0';
        return source.charAt(pos + 1);
    }

    // Consome o caractere atual e atualiza pos / line / column
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

    // Pula espaços em branco e comentários { } e (* *)
    private void skipWhitespaceAndComments() {
        while (true) {
            char c = peek();

            // Espaços e quebras de linha
            if (Character.isWhitespace(c)) {
                advance();

            // Comentário estilo { ... }
            } else if (c == '{') {
                advance();
                while (peek() != '}' && peek() != '\0') advance();
                if (peek() == '}') advance();

            // Comentário estilo (* ... *)
            } else if (c == '(' && peekNext() == '*') {
                advance(); // (
                advance(); // *
                while (!(peek() == '*' && peekNext() == ')') && peek() != '\0') {
                    advance();
                }
                if (peek() == '*') { advance(); advance(); } // consome *)

            } else {
                break;
            }
        }
    }

    @Override
    public Token nextToken() {
        skipWhitespaceAndComments();

        int startLine = line;
        int startCol  = column;

        // Fim de arquivo
        if (pos >= source.length()) {
            return new Token(TokenType.EOF, "", startLine, startCol);
        }

        char c = peek();

        // ── Identificador ou palavra reservada ──────────────────────────
        if (Character.isLetter(c)) {
            StringBuilder sb = new StringBuilder();
            while (Character.isLetterOrDigit(peek()) || peek() == '_') {
                sb.append(advance());
            }
            String text = sb.toString();
            TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);
            return new Token(type, text, startLine, startCol);
        }

        // ── Número inteiro ───────────────────────────────────────────────
        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            while (Character.isDigit(peek())) sb.append(advance());
            return new Token(TokenType.NUMBER, sb.toString(), startLine, startCol);
        }

        // ── String literal com aspas simples '...' ───────────────────────
        if (c == '\'') {
            advance(); // consome '
            StringBuilder sb = new StringBuilder();
            while (peek() != '\'' && peek() != '\0') sb.append(advance());
            if (peek() == '\'') advance(); // consome '
            return new Token(TokenType.LITERAL_STR, sb.toString(), startLine, startCol);
        }

        // ── Operadores e delimitadores ────────────────────────────────────
        c = advance();
        switch (c) {
            // Operadores aritméticos
            case '+': return new Token(TokenType.OPERATOR, "+",  startLine, startCol);
            case '-': return new Token(TokenType.OPERATOR, "-",  startLine, startCol);
            case '*': return new Token(TokenType.OPERATOR, "*",  startLine, startCol);
            case '/': return new Token(TokenType.OPERATOR, "/",  startLine, startCol);

            // Igualdade (Pascal usa '=' para comparação)
            case '=': return new Token(TokenType.OPERATOR, "=",  startLine, startCol);

            // := (atribuição) ou : (delimitador)
            case ':':
                if (peek() == '=') { advance(); return new Token(TokenType.OPERATOR,  ":=", startLine, startCol); }
                return new Token(TokenType.DELIMITER, ":", startLine, startCol);

            // <= ou <> ou <
            case '<':
                if (peek() == '=') { advance(); return new Token(TokenType.OPERATOR, "<=", startLine, startCol); }
                if (peek() == '>') { advance(); return new Token(TokenType.OPERATOR, "<>", startLine, startCol); }
                return new Token(TokenType.OPERATOR, "<", startLine, startCol);

            // >= ou >
            case '>':
                if (peek() == '=') { advance(); return new Token(TokenType.OPERATOR, ">=", startLine, startCol); }
                return new Token(TokenType.OPERATOR, ">", startLine, startCol);

            // Delimitadores
            case ';': return new Token(TokenType.DELIMITER, ";", startLine, startCol);
            case ',': return new Token(TokenType.DELIMITER, ",", startLine, startCol);
            case '.': return new Token(TokenType.DELIMITER, ".", startLine, startCol);
            case '(': return new Token(TokenType.DELIMITER, "(", startLine, startCol);
            case ')': return new Token(TokenType.DELIMITER, ")", startLine, startCol);

            default:
                throw new LexicalException("Caractere inválido", c, startLine, startCol);
        }
    }
}