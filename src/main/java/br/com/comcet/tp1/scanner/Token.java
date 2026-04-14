package br.com.comcet.tp1.scanner;

public record Token(TokenType type, String text, int line, int column) {
    @Override
    public String toString() {
        return String.format("<%s, \"%s\"> (linha: %d, col: %d)", type, text, line, column);
    }
}