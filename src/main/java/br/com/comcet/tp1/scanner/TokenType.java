package br.com.comcet.tp1.scanner;

public enum TokenType {
    KEYWORD, DELIMITER, OPERATOR, NUMBER,
    // Palavras reservadas
    IF, THEN, ELSE, WHILE, DO, PRINT, VAR, INTEGER,
    
    // Operadores e pontuação
    ASSIGN, PLUS, MINUS, TIMES, DIV, 
    LPAREN, RPAREN, LBRACE, RBRACE,
    COLON, SEMICOLON, // <-- Adicionados aqui!
    
    // Dinâmicos
    IDENTIFIER, LITERAL_INT, LITERAL_BOOL, LITERAL_STR,
    
    // Fim de arquivo
    EOF
}