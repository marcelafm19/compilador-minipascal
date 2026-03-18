package br.com.comcet.tp1.ast;

public class WhileCommand extends Command {
    public Expression condition;
    public Command body;

    public WhileCommand(Expression condition, Command body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    protected void printTree(StringBuilder sb, int level) {
        sb.append(indent(level)).append("WhileCommand\n");
        condition.printTree(sb, level + 1);
        body.printTree(sb, level + 1);
    }
}