package br.com.comcet.tp3;

import br.com.comcet.tp1.ast.*;
import br.com.comcet.tp1.scanner.Token;
import br.com.comcet.tp1.scanner.TokenType;
import br.com.comcet.tp2.Scanner;

public class Parser {
    private Scanner scanner;
    private Token currentToken;

    public Parser(Scanner scanner) {
        this.scanner = scanner;
        this.currentToken = scanner.nextToken();
    }

    private void advance() {
        currentToken = scanner.nextToken();
    }

    private void match(TokenType expected) {
        if (currentToken.type() == expected) {
            advance();
        } else {
            throw new RuntimeException("Erro Sintático: Esperado " + expected +
                    " mas encontrado " + currentToken.type() + " na linha " + currentToken.line());
        }
    }

    public Command parseCommand() {
        try {
            if (currentToken.type() == TokenType.IDENTIFIER) {
                return parseAssignment();
            } else if (currentToken.type() == TokenType.IF) {
                return parseIf();
            } else if (currentToken.type() == TokenType.WHILE) {
                return parseWhile();
            } else if (currentToken.type() == TokenType.LBRACE) {
                return parseBlock();
            } else if (currentToken.type() == TokenType.PRINT) {
                return parsePrint();
            }
            throw new RuntimeException(
                    "Comando não reconhecido: " + currentToken.text() + " na linha " + currentToken.line());

        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            synchronize();
            return null;
        }
    }

    private Command parseBlock() {
        int line = currentToken.line();
        int col = currentToken.column();

        match(TokenType.LBRACE);
        BlockCommand block = new BlockCommand();
        block.line = line;
        block.column = col;

        while (currentToken.type() != TokenType.RBRACE && currentToken.type() != TokenType.EOF) {
            Command cmd = parseCommand();
            if (cmd != null) {
                block.commands.add(cmd);
            }
        }
        match(TokenType.RBRACE);
        return block;
    }

    private Command parseIf() {
        int line = currentToken.line();
        int col = currentToken.column();

        match(TokenType.IF);
        Expression condition = parseExpression();
        match(TokenType.THEN);
        Command thenBranch = parseCommand();

        Command elseBranch = null;
        if (currentToken.type() == TokenType.ELSE) {
            match(TokenType.ELSE);
            elseBranch = parseCommand();
        }

        IfCommand cmd = new IfCommand(condition, thenBranch, elseBranch);
        cmd.line = line;
        cmd.column = col;
        return cmd;
    }

    private Command parseWhile() {
        int line = currentToken.line();
        int col = currentToken.column();

        match(TokenType.WHILE);
        Expression condition = parseExpression();
        match(TokenType.DO);
        Command body = parseCommand();

        WhileCommand cmd = new WhileCommand(condition, body);
        cmd.line = line;
        cmd.column = col;
        return cmd;
    }

    private Command parsePrint() {
        int line = currentToken.line();
        int col = currentToken.column();

        match(TokenType.PRINT);
        Expression expr = parseExpression();
        match(TokenType.SEMICOLON);

        PrintCommand cmd = new PrintCommand(expr);
        cmd.line = line;
        cmd.column = col;
        return cmd;
    }

    private Command parseAssignment() {
        int line = currentToken.line();
        int col = currentToken.column();
        String idName = currentToken.text();

        match(TokenType.IDENTIFIER);

        if (currentToken.type() == TokenType.ASSIGN) {
            advance();
        } else {
            throw new RuntimeException("Erro: Esperado ':=' na linha " + currentToken.line());
        }

        Expression expr = parseExpression();
        match(TokenType.SEMICOLON);

        Identifier id = new Identifier(idName);
        id.line = line;
        id.column = col;

        AssignmentCommand cmd = new AssignmentCommand(id, expr);
        cmd.line = line;
        cmd.column = col;
        return cmd;
    }

    public Expression parseExpression() {
        int line = currentToken.line();
        int col = currentToken.column();
        Expression left = parseTerm();

        while (currentToken.type() == TokenType.PLUS || currentToken.type() == TokenType.MINUS) {
            String op = currentToken.text();
            advance();
            Expression right = parseTerm();

            BinaryExpression bin = new BinaryExpression(left, right, op);
            bin.line = line;
            bin.column = col;
            left = bin;
        }
        return left;
    }

    private Expression parseTerm() {
        int line = currentToken.line();
        int col = currentToken.column();
        Expression left = parseFactor();

        while (currentToken.type() == TokenType.TIMES || currentToken.type() == TokenType.DIV) {
            String op = currentToken.text();
            advance();
            Expression right = parseFactor();

            BinaryExpression bin = new BinaryExpression(left, right, op);
            bin.line = line;
            bin.column = col;
            left = bin;
        }
        return left;
    }

    private Expression parseFactor() {
        int line = currentToken.line();
        int col = currentToken.column();

        if (currentToken.type() == TokenType.LITERAL_INT) { 
            String val = currentToken.text();
            match(TokenType.LITERAL_INT); 
            Literal literal = new Literal(Integer.parseInt(val));
            literal.line = line;
            literal.column = col;
            return literal;

        } else if (currentToken.type() == TokenType.IDENTIFIER) {
            String name = currentToken.text();
            match(TokenType.IDENTIFIER);
            Identifier id = new Identifier(name);
            id.line = line;
            id.column = col;
            return id;

        } else if (currentToken.type() == TokenType.LPAREN) {
            advance();
            Expression expr = parseExpression();
            match(TokenType.RPAREN);
            return expr;
        }
        throw new RuntimeException(
                "Erro: Expressão inválida perto de '" + currentToken.text() + "' na linha " + currentToken.line());
    }

    private void synchronize() {
        while (currentToken.type() != TokenType.EOF && currentToken.type() != TokenType.SEMICOLON) {
            advance();
        }
        if (currentToken.type() == TokenType.SEMICOLON) {
            advance();
        }
    }
}