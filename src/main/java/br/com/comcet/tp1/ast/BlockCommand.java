package br.com.comcet.tp1.ast;

import java.util.List;
import java.util.ArrayList;

public class BlockCommand extends Command {
    public List<Command> commands = new ArrayList<>();

    @Override
    protected void printTree(StringBuilder sb, int level) {
        sb.append(indent(level)).append("BlockCommand\n");
        for (Command cmd : commands) {
            cmd.printTree(sb, level + 1);
        }
    }
}