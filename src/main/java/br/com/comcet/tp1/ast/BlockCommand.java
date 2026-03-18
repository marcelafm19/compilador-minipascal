package br.com.comcet.tp1.ast;

import java.util.List;
import java.util.ArrayList;

public class BlockCommand extends Command {
    public List<Command> commands = new ArrayList<>();

    @Override
    public String toString() {
        return commands.toString();
    }
}