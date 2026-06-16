package br.com.comcet.tp1.ast;

import java.util.List;
import java.util.ArrayList;

public class Program extends AstNode {
    public VarDeclList variables = null; 
    public List<FunctionDecl> functions = new ArrayList<>();
    public List<Command> commands = new ArrayList<>();

    @Override
    protected void printTree(StringBuilder sb, int level) {
        sb.append(indent(level)).append("Program\n");
        
        if (variables != null) {
            variables.printTree(sb, level + 1);
        }
        
        for (FunctionDecl func : functions) {
            func.printTree(sb, level + 1);
        }
        
        sb.append(indent(level + 1)).append("CommandList\n");
        for (Command cmd : commands) {
            cmd.printTree(sb, level + 2);
        }
    }
}