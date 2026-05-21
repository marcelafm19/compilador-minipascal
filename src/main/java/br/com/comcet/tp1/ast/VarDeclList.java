package br.com.comcet.tp1.ast;

import java.util.ArrayList;
import java.util.List;

public class VarDeclList extends AstNode {
    public List<VarDecl> declarations = new ArrayList<>();

    @Override
    protected void printTree(StringBuilder sb, int level) {
        sb.append(indent(level)).append("VarDeclList\n");
        for (VarDecl var : declarations) {
            var.printTree(sb, level + 1);
        }
    }
}