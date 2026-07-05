package br.com.comcet.tp1.ast;

public class ReadCommand extends Command {
    public Identifier identifier;

    public ReadCommand(Identifier identifier) {
        this.identifier = identifier;
    }

    @Override
    public void printTree(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append("  "); 
        }
        sb.append("ReadCommand:\n");
        if (identifier != null) {
            identifier.printTree(sb, level + 1);
        }
    }
}