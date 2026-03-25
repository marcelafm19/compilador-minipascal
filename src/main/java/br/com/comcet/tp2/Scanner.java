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

    // Mapeamento de Palavras Reservadas (conforme o seu TokenType atual)
    private static final Map<String, TokenType> keywords = new HashMap<>();
    static {
        keywords.put("var", TokenType.KEYWORD);
        keywords.put("integer", TokenType.KEYWORD);
        keywords.put("if", TokenType.KEYWORD);
        keywords.put("then", TokenType.KEYWORD);
        keywords.put("else", TokenType.KEYWORD);
        keywords.put("while", TokenType.KEYWORD);
        keywords.put("do", TokenType.KEYWORD);
        keywords.put("print", TokenType.KEYWORD);
    }

    public Scanner(String source) {
        this.source = source;
        this.pos = 0;
        this.line = 1;
        this.column = 1;
    }

    // Retorna o caractere atual sem consumi-lo
    private char peek() {
        if (pos >= source.length())
            return '\0';
        return source.charAt(pos);
    }

    // Consome o caractere atual e avança a posição (atualizando linha/coluna)
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

    // Pula espaços em branco, quebras de linha, tabulações E comentários
    private void skipWhitespaceAndComments() {
        while (true) {
            char c = peek();
            if (Character.isWhitespace(c)) {
                advance();
            } else if (c == '{') {
                // Ignora tudo até encontrar o fechamento do comentário '}'
                advance(); // Consome o '{'
                while (peek() != '}' && peek() != '\0') {
                    advance();
                }
                if (peek() == '}') {
                    advance(); // Consome o '}'
                }
            } else {
                break; // Não é espaço nem comentário, sai do loop
            }
        }
    }

    @Override
    public Token nextToken() {
        skipWhitespaceAndComments();

        if (pos >= source.length()) {
            return new Token(TokenType.EOF, "");
        }

        char c = peek();

        // 1. Identificadores e Palavras Reservadas
        if (Character.isLetter(c)) {
            StringBuilder sb = new StringBuilder();
            while (Character.isLetterOrDigit(peek())) {
                sb.append(advance());
            }
            String text = sb.toString();
            TokenType type = keywords.getOrDefault(text, TokenType.IDENTIFIER);
            return new Token(type, text);
        }

        // 2. Números Inteiros
        if (Character.isDigit(c)) {
            StringBuilder sb = new StringBuilder();
            while (Character.isDigit(peek())) {
                sb.append(advance());
            }
            return new Token(TokenType.NUMBER, sb.toString());
        }

        // 3. Operadores e Pontuações
        c = advance();
        switch (c) {
            case '+':
                return new Token(TokenType.OPERATOR, "+");
            case '-':
                return new Token(TokenType.OPERATOR, "-");
            case '*':
                return new Token(TokenType.OPERATOR, "*");
            case '/':
                return new Token(TokenType.OPERATOR, "/");
            case '(':
                return new Token(TokenType.DELIMITER, "(");
            case ')':
                return new Token(TokenType.DELIMITER, ")");
            case '{':
                return new Token(TokenType.DELIMITER, "{");
            case '}':
                return new Token(TokenType.DELIMITER, "}");
            case ':':
                // Lookahead para identificar atribuição ":="
                if (peek() == '=') {
                    advance(); // Consome o '='
                    return new Token(TokenType.OPERATOR, ":="); // Era ASSIGN
                }
                // Se não for '=', é um dois-pontos (delimitador)
                return new Token(TokenType.DELIMITER, ":"); // Era COLON
            case '=':
                return new Token(TokenType.OPERATOR, "=");
            case ';':
                return new Token(TokenType.DELIMITER, ";"); // Era SEMICOLON
            default:
                throw new LexicalException("Caractere inválido", c, line, column - 1);
        }
    }
}