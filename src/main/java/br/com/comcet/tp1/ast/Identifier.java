package br.com.comcet.tp1.ast;

public class Identifier extends Expression {
    public String name;

    public Identifier(String name) {
        this.name = name;
    }

    @Override
    protected void printTree(StringBuilder sb, int level) {
        sb.append(indent(level)).append("Identifier(").append(name).append(")\n");
    }
}