package br.com.comcet.tp1.ast;

public class Literal extends Expression {
    public Object value;

    public Literal(Object value) {
        this.value = value;
    }

    @Override
    protected void printTree(StringBuilder sb, int level) {
        if (AstNode.isEtapa3) {
            sb.append(indent(level)).append("Literal (").append(value).append(")\n");
        } else {
            sb.append(indent(level)).append("Literal(").append(value).append(")\n");
        }
    }
}