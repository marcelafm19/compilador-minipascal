package br.com.comcet.tp1.ast;

public class AssignmentCommand extends Command {
    public Identifier id;
    public Expression expr;

    public AssignmentCommand(Identifier id, Expression expr) {
        this.id = id;
        this.expr = expr;
    }

    @Override
    public String toString() {
        return id + " := " + expr;
    }
}