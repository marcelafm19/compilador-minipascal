package br.com.comcet.tp2;

public class LexicalException extends RuntimeException {
    public LexicalException(String message, char badChar, int line, int column) {
        super(String.format("%s: '%c' na linha %d, coluna %d", message, badChar, line, column));
    }
}