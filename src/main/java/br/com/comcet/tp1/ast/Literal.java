package br.com.comcet.tp1.ast;

public class Literal extends Expression {
    public Object value;

    public Literal(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value.toString();
    }
}