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
        if (node == null) {
            return;
        }

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
        } else if (node instanceof RepeatCommand) {
            visitRepeat((RepeatCommand) node);
        } else if (node instanceof PrintCommand) {
            visitPrint((PrintCommand) node);
        } else if (node instanceof ReadCommand) {
            visitRead((ReadCommand) node);
        } else if (node instanceof BinaryExpression) {
            visitBinary((BinaryExpression) node);
        } else if (node instanceof UnaryExpression) {
            visitUnary((UnaryExpression) node);
        } else if (node instanceof Literal) {
            visitLiteral((Literal) node);
        } else if (node instanceof Identifier) {
            visitIdentifier((Identifier) node);
        } else if (node instanceof FunctionCall) {
            visitFunctionCall((FunctionCall) node);
        }
    }

    // Métodos de Visita

    private void visitProgram(Program node) {
        table.enterScope();

        // 1. Registrar as funções no escopo global primeiro
        for (FunctionDecl func : node.functions) {
            Symbol sym = new Symbol(func.name, func.returnType);
            sym.isFunction = true;
            for (VarDecl param : func.parameters) {
                sym.paramTypes.add(param.type); 
            }
            table.add(func.name, sym);
        }

        if (node.variables != null) {
            check(node.variables);
        }

        // 2. Validar o corpo de cada função
        for (FunctionDecl func : node.functions) {
            visitFunctionDecl(func);
        }

        for (Command cmd : node.commands) {
            check(cmd);
        }

        table.exitScope();
    }

    private void visitFunctionDecl(FunctionDecl node) {
        table.enterScope(); 

        // Adiciona os parâmetros como variáveis utilizáveis dentro da função
        for (VarDecl param : node.parameters) {
            table.add(param.name, new Symbol(param.name, param.type));
        }

        if (node.variables != null) {
            check(node.variables);
        }

        if (node.body != null) {
            check(node.body);
        }

        table.exitScope(); 
    }

    private void visitVarDeclList(VarDeclList node) {
        for (VarDecl var : node.declarations) {
            check(var);
        }
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
        for (Command cmd : node.commands) {
            check(cmd);
        }
    }

    private void visitAssignment(AssignmentCommand node) {
        check(node.id);
        check(node.expr);

        if (node.id.evalType != Type.UNKNOWN && node.expr.evalType != Type.UNKNOWN) {
            if (node.id.evalType != node.expr.evalType) {
                errors.add("Erro Semântico na linha " + node.line + ": Atribuição inválida. Variável '" + node.id.name
                        + "' é do tipo " + node.id.evalType + ", mas recebeu " + node.expr.evalType);
            }
        }
    }

    private void visitIf(IfCommand node) {
        check(node.condition);

        if (node.condition.evalType != Type.BOOLEAN && node.condition.evalType != Type.UNKNOWN) {
            errors.add("Erro Semântico na linha " + node.line + ": Condição do 'if' deve ser booleana.");
        }

        check(node.thenBranch);

        if (node.elseBranch != null) {
            check(node.elseBranch);
        }
    }

    private void visitWhile(WhileCommand node) {
        check(node.condition);

        if (node.condition.evalType != Type.BOOLEAN && node.condition.evalType != Type.UNKNOWN) {
            errors.add("Erro Semântico na linha " + node.line + ": Condição do 'while' deve ser booleana.");
        }

        check(node.body);
    }

    private void visitRepeat(RepeatCommand node) {
        check(node.body);
        check(node.condition);

        if (node.condition != null && node.condition.evalType != Type.BOOLEAN && node.condition.evalType != Type.UNKNOWN) {
            errors.add("Erro Semântico na linha " + node.line + ": Condição do 'repeat...until' deve ser booleana.");
        }
    }

    private void visitPrint(PrintCommand node) {
        check(node.expression);
    }

    private void visitRead(ReadCommand node) {
        if (node.identifier != null) {
            check(node.identifier);
        }
    }

    private void visitBinary(BinaryExpression node) {
        check(node.left);
        check(node.right);

        String op = node.operator;

        // Operadores Aritméticos
        if (op.equals("+") || op.equals("-") || op.equals("*") || op.equals("/")) {
            if (node.left.evalType == Type.INTEGER && node.right.evalType == Type.INTEGER) {
                node.evalType = Type.INTEGER;
            } else {
                errors.add("Erro Semântico na linha " + node.line + ": Operação '" + op + "' não suportada para tipos "
                        + node.left.evalType + " e " + node.right.evalType);
                node.evalType = Type.UNKNOWN;
            }
        }
        // Operadores Relacionais (Mini-Pascal utiliza '=' e '<>')
        else if (op.equals("=") || op.equals("<>") || op.equals("==") || op.equals("!=") || op.equals(">") || op.equals("<") || op.equals(">=")
                || op.equals("<=")) {
            if (node.left.evalType != Type.UNKNOWN && node.right.evalType != Type.UNKNOWN) {
                node.evalType = Type.BOOLEAN; 
            } else {
                node.evalType = Type.UNKNOWN;
            }
        }
        // Operadores Lógicos binários ('and', 'or')
        else if (op.equalsIgnoreCase("and") || op.equalsIgnoreCase("or")) {
            if (node.left.evalType == Type.BOOLEAN && node.right.evalType == Type.BOOLEAN) {
                node.evalType = Type.BOOLEAN;
            } else {
                errors.add("Erro Semântico na linha " + node.line + ": Operação lógica '" + op + "' exige operandos booleanos.");
                node.evalType = Type.UNKNOWN;
            }
        }
    }

    private void visitUnary(UnaryExpression node) {
        check(node.expr); 

        if (node.operator.equalsIgnoreCase("not")) {
            if (node.expr.evalType == Type.BOOLEAN) {
                node.evalType = Type.BOOLEAN;
            } else {
                errors.add("Erro Semântico na linha " + node.line + ": Operador 'not' exige uma expressão booleana.");
                node.evalType = Type.UNKNOWN;
            }
        } else {
            node.evalType = Type.UNKNOWN;
        }
    }

    private void visitLiteral(Literal node) {
        if (node.value instanceof Integer) {
            node.evalType = Type.INTEGER;
        } else if (node.value instanceof Boolean) {
            node.evalType = Type.BOOLEAN;
        } else if (node.value instanceof String) {
            node.evalType = Type.STRING;
        } else {
            node.evalType = Type.UNKNOWN;
        }
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

    private void visitFunctionCall(FunctionCall node) {
        Symbol sym = table.lookup(node.functionName);

        if (sym == null) {
            errors.add("Erro Semântico: Função não declarada: " + node.functionName);
            node.evalType = Type.UNKNOWN;
            return;
        }

        if (!sym.isFunction) {
            errors.add("Erro Semântico: O identificador '" + node.functionName + "' não é uma função.");
            node.evalType = Type.UNKNOWN;
            return;
        }

        node.evalType = parseType(sym.type);

        // Validar quantidade de parâmetros
        if (node.arguments.size() != sym.paramTypes.size()) {
            errors.add("Erro Semântico: Número de argumentos incorreto para a função '" + node.functionName + "'");
        } else {
            // Validar tipos de cada parâmetro passado
            for (int i = 0; i < node.arguments.size(); i++) {
                Expression arg = node.arguments.get(i);
                check(arg);
                Type expectedType = parseType(sym.paramTypes.get(i));

                if (arg.evalType != Type.UNKNOWN && arg.evalType != expectedType) {
                    errors.add("Erro Semântico: Tipo incompatível no argumento " + (i + 1) + " da função '"
                            + node.functionName + "'. Esperado " + expectedType + " mas recebeu " + arg.evalType);
                }
            }
        }
    }

    // Métodos Auxiliares

    private Type parseType(String typeStr) {
        if (typeStr.equals("integer"))
            return Type.INTEGER;
        if (typeStr.equals("boolean"))
            return Type.BOOLEAN;
        if (typeStr.equals("string"))
            return Type.STRING;
        return Type.UNKNOWN;
    }

    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    public List<String> getErrors() {
        return errors;
    }
}