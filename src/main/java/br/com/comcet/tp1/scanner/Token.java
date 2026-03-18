package br.com.comcet.tp1.scanner;

public record Token(TokenType type, String text) {
    @Override
    public String toString() {
        return String.format("<%s, \"%s\">", type, text);
    }
}