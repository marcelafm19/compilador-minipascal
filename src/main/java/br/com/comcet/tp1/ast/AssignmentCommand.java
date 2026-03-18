package br.com.comcet.tp1.ast;

public class AssignmentCommand extends Command {
    public Identifier id;
    public Expression expr;

    public AssignmentCommand(Identifier id, Expression expr) {
        this.id = id;
        this.expr = expr;
    }

    @Override
    protected void printTree(StringBuilder sb, int level) {
        sb.append(indent(level)).append("AssignmentCommand\n");
        id.printTree(sb, level + 1);
        expr.printTree(sb, level + 1);
    }
}