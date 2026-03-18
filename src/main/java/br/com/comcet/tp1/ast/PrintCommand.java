package br.com.comcet.tp1.ast;

public class PrintCommand extends Command {
    public Expression expression;

    public PrintCommand(Expression expression) {
        this.expression = expression;
    }

    @Override
    public String toString() {
        return "print(" + expression + ")";
    }
}