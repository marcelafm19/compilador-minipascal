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
    public String toString() {
        String res = "if (" + condition.toString() + ") then " + thenBranch.toString();
        if (elseBranch != null) {
            res += " else " + elseBranch.toString();
        }
        return res;
    }
}