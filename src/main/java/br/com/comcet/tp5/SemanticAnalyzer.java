package br.com.comcet.tp5;

import br.com.comcet.tp1.ast.*;
import br.com.comcet.tp1.symbols.Symbol;

import java.util.ArrayList;
import java.util.List;

public class SemanticAnalyzer {
    private SymbolTable table;
    private List<String> errors;

    public SemanticAnalyzer() {
        this.table = new SymbolTable();
        this.errors = new ArrayList<>();
    }

    public void analyze(AstNode node) {
        if (node == null)
            return;

        if (node instanceof Program) {
            visitProgram((Program) node);
        } else if (node instanceof VarDeclList) {
            visitVarDeclList((VarDeclList) node);
        } else if (node instanceof VarDecl) {
            visitVarDecl((VarDecl) node);
        } else if (node instanceof BlockCommand) {
            visitBlock((BlockCommand) node);
        } else if (node instanceof AssignmentCommand) {
            visitAssignment((AssignmentCommand) node);
        } else if (node instanceof IfCommand) {
            visitIf((IfCommand) node);
        } else if (node instanceof WhileCommand) {
            visitWhile((WhileCommand) node);
        } else if (node instanceof PrintCommand) {
            visitPrint((PrintCommand) node);
        } else if (node instanceof BinaryExpression) {
            visitBinary((BinaryExpression) node);
        } else if (node instanceof Identifier) {
            visitIdentifier((Identifier) node);
        }
    }

    private void visitProgram(Program node) {
        table.enterScope();

        if (node.variables != null) {
            analyze(node.variables);
        }

        for (Command cmd : node.commands) {
            analyze((AstNode) cmd);
        }

        table.exitScope();
    }

    private void visitVarDeclList(VarDeclList node) {
        for (VarDecl var : node.declarations) {
            analyze(var);
        }
    }

    private void visitVarDecl(VarDecl node) {
        String name = node.name;

        Symbol existing = table.lookup(name);

        if (existing != null) {
            errors.add("Erro semântico: Variável já declarada: " + name);
        } else {
            Symbol sym = new Symbol(name, node.type);
            table.add(name, sym);
        }
    }

    private void visitBlock(BlockCommand node) {
        for (Command cmd : node.commands) {
            analyze((AstNode) cmd);
        }
    }

    private void visitAssignment(AssignmentCommand node) {
        analyze((AstNode) node.id);
        analyze((AstNode) node.expr);
    }

    private void visitIf(IfCommand node) {
        analyze((AstNode) node.condition);
        analyze((AstNode) node.thenBranch);
        if (node.elseBranch != null) {
            analyze((AstNode) node.elseBranch);
        }
    }

    private void visitWhile(WhileCommand node) {
        analyze((AstNode) node.condition);
        analyze((AstNode) node.body);
    }

    private void visitPrint(PrintCommand node) {
        analyze((AstNode) node.expression);
    }

    private void visitBinary(BinaryExpression node) {
        analyze((AstNode) node.left);
        analyze((AstNode) node.right);
    }

    private void visitIdentifier(Identifier node) {
        String name = node.name;
        Symbol sym = table.lookup(name);
        if (sym == null) {
            errors.add("Erro: Variável ou função não declarada: " + name);
        }
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }
}