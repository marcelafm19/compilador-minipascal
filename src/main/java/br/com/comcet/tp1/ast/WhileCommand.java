package br.com.comcet.tp1.ast;

public class WhileCommand extends Command {
    public Expression condition;
    public Command body;

    public WhileCommand(Expression condition, Command body) {
        this.condition = condition;
        this.body = body;
    }

    @Override
    public String toString() {
        return "while (" + condition.toString() + ") do " + body.toString();
    }
}