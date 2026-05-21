package br.com.comcet.tp1.ast;

public class VarDecl extends AstNode {
    public String name;
    public String type;

    public VarDecl(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    protected void printTree(StringBuilder sb, int level) {
        sb.append(indent(level)).append("VarDecl: ").append(name).append(" (").append(type).append(")\n");
    }
}