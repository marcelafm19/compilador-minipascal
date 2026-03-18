package br.com.comcet.tp1.ast;

public abstract class AstNode {
    public int line;
    public int column;

    public final String printTree() {
        StringBuilder sb = new StringBuilder();
        printTree(sb, 0);
        return sb.toString();
    }

    protected abstract void printTree(StringBuilder sb, int level);

    protected String indent(int level) {
        return "  ".repeat(level); 
    }

    @Override
    public String toString() {
        return printTree();
    }
}