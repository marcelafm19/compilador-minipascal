package br.com.comcet.tp1.ast;

import java.util.List;
import java.util.ArrayList;

public class Program extends AstNode {
    public List<Command> commands = new ArrayList<>();

    @Override
    protected void printTree(StringBuilder sb, int level) {
        sb.append(indent(level)).append("Program\n");
        for (Command cmd : commands) {
            cmd.printTree(sb, level + 1);
        }
    }
}