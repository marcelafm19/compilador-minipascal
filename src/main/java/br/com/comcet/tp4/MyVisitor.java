package br.com.comcet.tp4;

import br.com.comcet.tp1.ast.*;
import br.com.comcet.tp4.parser.MiniPascalBaseVisitor;
import br.com.comcet.tp4.parser.MiniPascalParser;

public class MyVisitor extends MiniPascalBaseVisitor<AstNode> {

    @Override
    public AstNode visitProgram(MiniPascalParser.ProgramContext ctx) {
        Program program = new Program();
        BlockCommand block = (BlockCommand) visit(ctx.block());
        program.commands.addAll(block.commands);
        return program;
    }

    @Override
    public AstNode visitBlock(MiniPascalParser.BlockContext ctx) {
        BlockCommand block = new BlockCommand();
        for (MiniPascalParser.CommandContext cmdCtx : ctx.command()) {
            Command cmd = (Command) visit(cmdCtx);
            if (cmd != null) {
                block.commands.add(cmd);
            }
        }
        return block;
    }

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
    public AstNode visitPrintCmd(MiniPascalParser.PrintCmdContext ctx) {
        Expression expr = (Expression) visit(ctx.expression());
        return new PrintCommand(expr);
    }

    @Override
    public AstNode visitBinaryExpr(MiniPascalParser.BinaryExprContext ctx) {
        Expression left = (Expression) visit(ctx.expression(0));
        Expression right = (Expression) visit(ctx.expression(1));
        String op = ctx.op.getText();
        return new BinaryExpression(left, right, op);
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
}