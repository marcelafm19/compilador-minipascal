package br.com.comcet.tp1.ast;

public class UnaryExpression extends Expression {
    public Expression expr;
    public String operator;

    public UnaryExpression(Expression expr, String operator) {
        this.expr = expr;
        this.operator = operator;
    }

    @Override
    protected void printTree(StringBuilder sb, int level) {
        sb.append(indent(level)).append("UnaryExpression ").append(operator).append("\n");
        expr.printTree(sb, level + 1);
    }
}