package br.com.comcet.tp1.ast;

public class RepeatCommand extends Command {
    public BlockCommand body;
    public Expression condition;

    public RepeatCommand(BlockCommand body, Expression condition) {
        this.body = body;
        this.condition = condition;
    }

    @Override
    public void printTree(StringBuilder sb, int level) {
        for (int i = 0; i < level; i++) {
            sb.append("  ");
        }
        sb.append("RepeatCommand:\n");
        if (body != null) {
            body.printTree(sb, level + 1);
        }
        if (condition != null) {
            condition.printTree(sb, level + 1);
        }
    }
}