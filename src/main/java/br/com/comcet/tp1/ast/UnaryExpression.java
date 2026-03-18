package br.com.comcet.tp1.ast;

public class UnaryExpression extends Expression {
    public Expression expr;
    public String operator;

    public UnaryExpression(Expression expr, String operator) {
        this.expr = expr;
        this.operator = operator;
    }

    @Override
    public String toString() {
        return operator + expr;
    }
}