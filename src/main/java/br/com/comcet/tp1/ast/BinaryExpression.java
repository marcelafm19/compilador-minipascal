package br.com.comcet.tp1.ast;

public class BinaryExpression extends Expression {
    public Expression left;
    public Expression right;
    public String operator;

    public BinaryExpression(Expression left, Expression right, String operator) {
        this.left = left;
        this.right = right;
        this.operator = operator;
    }

    @Override
    protected void printTree(StringBuilder sb, int level) {
        if (AstNode.isEtapa3) {
            sb.append(indent(level)).append("BinaryExpression (").append(operator).append(")\n");
        } else {
            sb.append(indent(level)).append("BinaryExpression ").append(operator).append("\n");
        }
        left.printTree(sb, level + 1);
        right.printTree(sb, level + 1);
    }
}