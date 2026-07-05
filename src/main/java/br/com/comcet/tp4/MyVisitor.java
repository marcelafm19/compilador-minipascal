package br.com.comcet.tp4;

import java.util.ArrayList;
import java.util.List;

import br.com.comcet.tp1.ast.*;
import br.com.comcet.tp4.parser.MiniPascalBaseVisitor;
import br.com.comcet.tp4.parser.MiniPascalParser;

public class MyVisitor extends MiniPascalBaseVisitor<AstNode> {

    @Override
    public AstNode visitProgram(MiniPascalParser.ProgramContext ctx) {
        Program program = new Program();

        if (ctx.varDecl() != null) {
            program.variables = (VarDeclList) visit(ctx.varDecl());
        }

        // Lê todas as funções e adiciona na lista do Program
        for (MiniPascalParser.FunctionDeclContext fCtx : ctx.functionDecl()) {
            program.functions.add((FunctionDecl) visit(fCtx));
        }

        BlockCommand block = (BlockCommand) visit(ctx.block());
        program.commands.addAll(block.commands);
        return program;
    }

    @Override
    public AstNode visitFunctionDecl(MiniPascalParser.FunctionDeclContext ctx) {
        String name = ctx.ID().getText();
        String returnType = ctx.type().getText();
        FunctionDecl func = new FunctionDecl(name, returnType);

        // 1. Processa a lista de parâmetros
        if (ctx.paramList() != null) {
            MiniPascalParser.ParamListContext pCtx = ctx.paramList();
            for (int i = 0; i < pCtx.ID().size(); i++) {
                String paramName = pCtx.ID(i).getText();
                String paramType = pCtx.type(i).getText();
                func.parameters.add(new VarDecl(paramName, paramType));
            }
        }

        // 2. Processa as variáveis locais da função
        if (ctx.varDecl() != null) {
            func.variables = (VarDeclList) visit(ctx.varDecl());
        }

        // 3. Processa o corpo
        func.body = (BlockCommand) visit(ctx.block());

        return func;
    }

    @Override
    public AstNode visitFunctionCallExpr(MiniPascalParser.FunctionCallExprContext ctx) {
        String name = ctx.ID().getText();
        FunctionCall call = new FunctionCall(name);

        // Processa os argumentos passados
        if (ctx.argList() != null) {
            for (MiniPascalParser.ExpressionContext eCtx : ctx.argList().expression()) {
                call.arguments.add((Expression) visit(eCtx));
            }
        }
        return call;
    }

    @Override
    public AstNode visitVarDecl(MiniPascalParser.VarDeclContext ctx) {
        VarDeclList list = new VarDeclList();
        List<String> currentIds = new ArrayList<>();

        for (int i = 1; i < ctx.getChildCount(); i++) {
            String text = ctx.getChild(i).getText();

            if (text.equals(",") || text.equals(":") || text.equals(";")) {
                continue;
            }

            if (text.equals("integer") || text.equals("boolean") || text.equals("string")) {
                for (String id : currentIds) {
                    list.declarations.add(new VarDecl(id, text));
                }
                currentIds.clear();
            } else {
                currentIds.add(text);
            }
        }

        return list;
    }

    @Override
    public AstNode visitBlock(MiniPascalParser.BlockContext ctx) {
        BlockCommand block = new BlockCommand();
        if (ctx.commandList() != null) {
            for (MiniPascalParser.CommandContext cmdCtx : ctx.commandList().command()) {
                Command cmd = (Command) visit(cmdCtx);
                if (cmd != null) {
                    block.commands.add(cmd);
                }
            }
        }
        return block;
    }

    // --- COMANDOS ---

    @Override
    public AstNode visitAssignment(MiniPascalParser.AssignmentContext ctx) {
        Identifier id = new Identifier(ctx.ID().getText());
        Expression expr = (Expression) visit(ctx.expression());
        return new AssignmentCommand(id, expr);
    }

    @Override
    public AstNode visitIfCmd(MiniPascalParser.IfCmdContext ctx) {
        Expression condition = (Expression) visit(ctx.expression());
        Command thenBranch = (Command) visit(ctx.command(0));
        Command elseBranch = ctx.command().size() > 1 ? (Command) visit(ctx.command(1)) : null;
        return new IfCommand(condition, thenBranch, elseBranch);
    }

    @Override
    public AstNode visitWhileCmd(MiniPascalParser.WhileCmdContext ctx) {
        Expression condition = (Expression) visit(ctx.expression());
        Command body = (Command) visit(ctx.command());
        return new WhileCommand(condition, body);
    }

    @Override
    public AstNode visitRepeatCmd(MiniPascalParser.RepeatCmdContext ctx) {
        BlockCommand body = new BlockCommand();
        for (MiniPascalParser.CommandContext cmdCtx : ctx.commandList().command()) {
            Command cmd = (Command) visit(cmdCtx);
            if (cmd != null) {
                body.commands.add(cmd);
            }
        }
        Expression condition = (Expression) visit(ctx.expression());
        return new RepeatCommand(body, condition);
    }

    @Override
    public AstNode visitWritelnCmd(MiniPascalParser.WritelnCmdContext ctx) {
        Expression expr = (Expression) visit(ctx.expression());
        return new PrintCommand(expr);
    }

    @Override
    public AstNode visitReadlnCmd(MiniPascalParser.ReadlnCmdContext ctx) {
        Identifier id = new Identifier(ctx.ID().getText());
        return new ReadCommand(id);
    }

    @Override
    public AstNode visitBlockCmd(MiniPascalParser.BlockCmdContext ctx) {
        return visit(ctx.block());
    }

    // --- EXPRESSÕES ---

    @Override
    public AstNode visitMultiplicativeExpr(MiniPascalParser.MultiplicativeExprContext ctx) {
        Expression left = (Expression) visit(ctx.expression(0));
        Expression right = (Expression) visit(ctx.expression(1));
        return new BinaryExpression(left, right, ctx.op.getText());
    }

    @Override
    public AstNode visitAdditiveExpr(MiniPascalParser.AdditiveExprContext ctx) {
        Expression left = (Expression) visit(ctx.expression(0));
        Expression right = (Expression) visit(ctx.expression(1));
        return new BinaryExpression(left, right, ctx.op.getText());
    }

    @Override
    public AstNode visitRelationalExpr(MiniPascalParser.RelationalExprContext ctx) {
        Expression left = (Expression) visit(ctx.expression(0));
        Expression right = (Expression) visit(ctx.expression(1));
        return new BinaryExpression(left, right, ctx.op.getText());
    }

    @Override
    public AstNode visitAndExpr(MiniPascalParser.AndExprContext ctx) {
        Expression left = (Expression) visit(ctx.expression(0));
        Expression right = (Expression) visit(ctx.expression(1));
        return new BinaryExpression(left, right, "and");
    }

    @Override
    public AstNode visitOrExpr(MiniPascalParser.OrExprContext ctx) {
        Expression left = (Expression) visit(ctx.expression(0));
        Expression right = (Expression) visit(ctx.expression(1));
        return new BinaryExpression(left, right, "or");
    }

    @Override
    public AstNode visitNotExpr(MiniPascalParser.NotExprContext ctx) {
        Expression expr = (Expression) visit(ctx.expression());
        return new UnaryExpression(expr, "not");
    }

    @Override
    public AstNode visitNumberExpr(MiniPascalParser.NumberExprContext ctx) {
        int value = Integer.parseInt(ctx.NUMBER().getText());
        return new Literal(value);
    }

    @Override
    public AstNode visitIdExpr(MiniPascalParser.IdExprContext ctx) {
        return new Identifier(ctx.ID().getText());
    }

    @Override
    public AstNode visitParenExpr(MiniPascalParser.ParenExprContext ctx) {
        return visit(ctx.expression());
    }

    @Override
    public AstNode visitStringExpr(MiniPascalParser.StringExprContext ctx) {
        return new Literal(ctx.STRING_LITERAL().getText());
    }

    @Override
    public AstNode visitBooleanExpr(MiniPascalParser.BooleanExprContext ctx) {
        boolean value = ctx.TRUE() != null;
        return new Literal(value);
    }
}