package br.com.comcet.tp1.scanner;

public enum TokenType {
    // Palavras reservadas
    IF, THEN, ELSE, WHILE, DO, PRINT,
    
    // Operadores e pontuação
    ASSIGN, PLUS, MINUS, TIMES, DIV, 
    LPAREN, RPAREN, LBRACE, RBRACE,
    
    // Dinâmicos
    IDENTIFIER, LITERAL_INT, LITERAL_BOOL, LITERAL_STR,
    
    // Fim de arquivo
    EOF
}