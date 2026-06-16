package br.com.comcet.tp1.ast;

import java.util.ArrayList;
import java.util.List;

public class FunctionCall extends Expression {
    public String functionName;
    public List<Expression> arguments = new ArrayList<>();

    public FunctionCall(String functionName) {
        this.functionName = functionName;
    }

    @Override
    protected void printTree(StringBuilder sb, int level) {
        sb.append(indent(level)).append("FunctionCall(").append(functionName).append(")\n");
        for (Expression arg : arguments) arg.printTree(sb, level + 1);
    }
}