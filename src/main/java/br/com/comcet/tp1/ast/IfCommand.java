package br.com.comcet.tp1.ast;

public class IfCommand extends Command {
    public Expression condition;
    public Command thenBranch;
    public Command elseBranch;

    public IfCommand(Expression condition, Command thenBranch, Command elseBranch) {
        this.condition = condition;
        this.thenBranch = thenBranch;
        this.elseBranch = elseBranch;
    }

    @Override
    protected void printTree(StringBuilder sb, int level) {
        sb.append(indent(level)).append("IfCommand\n");
        condition.printTree(sb, level + 1);
        thenBranch.printTree(sb, level + 1);
        if (elseBranch != null) {
            elseBranch.printTree(sb, level + 1);
        }
    }
}