package br.com.comcet.tp1.ast;

import java.util.List;
import java.util.ArrayList;

public class Program extends AstNode {
    public List<Command> commands = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Command cmd : commands) {
            sb.append(cmd.toString()).append("\n");
        }
        return sb.toString();
    }
}