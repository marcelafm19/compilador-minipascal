package br.com.comcet.tp1.ast;

public class Identifier extends Expression {
    public String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}