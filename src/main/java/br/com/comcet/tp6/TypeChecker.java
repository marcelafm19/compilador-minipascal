package br.com.comcet.tp6;

import br.com.comcet.tp1.ast.*;
import br.com.comcet.tp1.symbols.Symbol;
import br.com.comcet.tp5.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class TypeChecker {
    private SymbolTable table;
    private List<String> errors;

    public TypeChecker() {
        this.table = new SymbolTable();
        this.errors = new ArrayList<>();
    }

    public void check(AstNode node) {
        if (node == null) return;

        if (node instanceof Program) visitProgram((Program) node);
        else if (node instanceof VarDeclList) visitVarDeclList((VarDeclList) node);
        else if (node instanceof VarDecl) visitVarDecl((VarDecl) node);
        else if (node instanceof BlockCommand) visitBlock((BlockCommand) node);
        else if (node instanceof AssignmentCommand) visitAssignment((AssignmentCommand) node);
        else if (node instanceof IfCommand) visitIf((IfCommand) node);
        else if (node instanceof WhileCommand) visitWhile((WhileCommand) node);
        else if (node instanceof PrintCommand) visitPrint((PrintCommand) node);
        else if (node instanceof BinaryExpression) visitBinary((BinaryExpression) node);
        else if (node instanceof Literal) visitLiteral((Literal) node);
        else if (node instanceof Identifier) visitIdentifier((Identifier) node);
    }

    private void visitProgram(Program node) {
        table.enterScope();
        if (node.variables != null) check(node.variables);
        for (Command cmd : node.commands) check(cmd);
        table.exitScope();
    }

    private void visitVarDeclList(VarDeclList node) {
        for (VarDecl var : node.declarations) check(var);
    }

    private void visitVarDecl(VarDecl node) {
        Symbol existing = table.lookup(node.name);
        if (existing != null) {
            errors.add("Erro Semântico: Variável já declarada: " + node.name);
        } else {
            table.add(node.name, new Symbol(node.name, node.type));
        }
    }

    private void visitBlock(BlockCommand node) {
        for (Command cmd : node.commands) check(cmd);
    }

    private void visitLiteral(Literal node) {
        // A gramática atual suporta apenas inteiros.
        node.evalType = Type.INTEGER;
    }

    private void visitIdentifier(Identifier node) {
        Symbol sym = table.lookup(node.name);
        if (sym == null) {
            errors.add("Erro Semântico na linha " + node.line + ": Variável não declarada: " + node.name);
            node.evalType = Type.UNKNOWN;
        } else {
            node.evalType = parseType(sym.type);
        }
    }

    private Type parseType(String typeStr) {
        if (typeStr.equals("integer")) return Type.INTEGER;
        if (typeStr.equals("boolean")) return Type.BOOLEAN;
        if (typeStr.equals("string")) return Type.STRING;
        return Type.UNKNOWN;
    }

    private void visitBinary(BinaryExpression node) {
        check(node.left);
        check(node.right);

        // Operadores Aritméticos exigem inteiros
        if (node.operator.equals("+") || node.operator.equals("-") || node.operator.equals("*") || node.operator.equals("/")) {
            if (node.left.evalType == Type.INTEGER && node.right.evalType == Type.INTEGER) {
                node.evalType = Type.INTEGER;
            } else {
                errors.add("Erro Semântico na linha " + node.line + ": Operação '" + node.operator + "' não suportada para tipos " + node.left.evalType + " e " + node.right.evalType);
                node.evalType = Type.UNKNOWN;
            }
        }
    }

    private void visitAssignment(AssignmentCommand node) {
        check(node.id);
        check(node.expr);
        
        if (node.id.evalType != Type.UNKNOWN && node.expr.evalType != Type.UNKNOWN) {
            if (node.id.evalType != node.expr.evalType) {
                errors.add("Erro Semântico na linha " + node.line + ": Atribuição inválida. Variável '" + node.id.name + "' é do tipo " + node.id.evalType + ", mas recebeu " + node.expr.evalType);
            }
        }
    }

    private void visitIf(IfCommand node) {
        check(node.condition);
        if (node.condition.evalType != Type.BOOLEAN && node.condition.evalType != Type.UNKNOWN) {
            errors.add("Erro Semântico na linha " + node.line + ": Condição do 'if' deve ser booleana.");
        }
        check(node.thenBranch);
        if (node.elseBranch != null) check(node.elseBranch);
    }

    private void visitWhile(WhileCommand node) {
        check(node.condition);
        if (node.condition.evalType != Type.BOOLEAN && node.condition.evalType != Type.UNKNOWN) {
            errors.add("Erro Semântico na linha " + node.line + ": Condição do 'while' deve ser booleana.");
        }
        check(node.body);
    }

    private void visitPrint(PrintCommand node) {
        check(node.expression);
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }
}