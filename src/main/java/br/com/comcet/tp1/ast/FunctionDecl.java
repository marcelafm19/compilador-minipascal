package br.com.comcet.tp1.ast;

import java.util.ArrayList;
import java.util.List;

public class FunctionDecl extends AstNode {
    public String name;
    public String returnType;
    public List<VarDecl> parameters = new ArrayList<>();
    public VarDeclList variables;
    public BlockCommand body;

    public FunctionDecl(String name, String returnType) {
        this.name = name;
        this.returnType = returnType;
    }

    @Override
    protected void printTree(StringBuilder sb, int level) {
        sb.append(indent(level)).append("FunctionDecl(").append(name).append(": ").append(returnType).append(")\n");
        for (VarDecl p : parameters) p.printTree(sb, level + 1);
        if (variables != null) variables.printTree(sb, level + 1);
        if (body != null) body.printTree(sb, level + 1);
    }
}