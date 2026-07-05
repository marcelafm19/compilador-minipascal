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

    // ── Predicados auxiliares ────────────────────────────────────────────
    // Agora o Scanner retorna categorias amplas (OPERATOR, KEYWORD, DELIMITER)
    // O Parser distingue pelo texto do token

    private boolean isOp(String op) {
        return currentToken.type() == TokenType.OPERATOR
                && currentToken.text().equals(op);
    }

    private boolean isKeyword(String kw) {
        return currentToken.type() == TokenType.KEYWORD
                && currentToken.text().equalsIgnoreCase(kw);
    }

    private boolean isDelimiter(String d) {
        return currentToken.type() == TokenType.DELIMITER
                && currentToken.text().equals(d);
    }

    // ── Métodos de match ─────────────────────────────────────────────────

    private void match(TokenType expected) {
        if (currentToken.type() == expected) {
            advance();
        } else {
            throw new RuntimeException("Erro Sintático: Esperado " + expected
                    + " mas encontrado " + currentToken.type()
                    + " na linha " + currentToken.line());
        }
    }

    private void matchKeyword(String kw) {
        if (isKeyword(kw)) {
            advance();
        } else {
            throw new RuntimeException("Erro Sintático: Esperado palavra-chave '"
                    + kw + "' mas encontrado '" + currentToken.text()
                    + "' na linha " + currentToken.line());
        }
    }

    private void matchDelimiter(String d) {
        if (isDelimiter(d)) {
            advance();
        } else {
            throw new RuntimeException("Erro Sintático: Esperado '"
                    + d + "' mas encontrado '" + currentToken.text()
                    + "' na linha " + currentToken.line());
        }
    }

    // ── Parsing do programa completo ─────────────────────────────────────

    /**
     * Faz o parse de um programa Pascal completo:
     *   program NAME ; [var ...;]* begin ... end .
     */
    public Program parseProgram() {
        Program program = new Program();

        // program NAME ;
        if (isKeyword("program")) {
            advance();                    // consome 'program'
            match(TokenType.IDENTIFIER);  // nome do programa
            matchDelimiter(";");
        }

        // var NAME : TYPE ; (pode ter várias declarações)
        while (isKeyword("var")) {
            advance(); // consome 'var'
            // pula declarações: NAME : TYPE ;
            while (!isKeyword("begin") && !isKeyword("function")
                    && !isKeyword("procedure") && currentToken.type() != TokenType.EOF) {
                advance();
            }
        }

        // begin ... end .
        if (isKeyword("begin")) {
            Command block = parseBlock();
            if (block instanceof BlockCommand bc) {
                program.commands.addAll(bc.commands);
            }
        }

        return program;
    }

    // ── Parsing de comandos ───────────────────────────────────────────────

    public Command parseCommand() {
        try {
            if (currentToken.type() == TokenType.IDENTIFIER) {
                return parseAssignment();
            } else if (isKeyword("if")) {
                return parseIf();
            } else if (isKeyword("while")) {
                return parseWhile();
            } else if (isKeyword("begin")) {
                return parseBlock();
            } else if (isKeyword("writeln") || isKeyword("print")) {
                return parsePrint();
            }
            throw new RuntimeException(
                    "Comando não reconhecido: '" + currentToken.text()
                    + "' na linha " + currentToken.line());

        } catch (RuntimeException e) {
            System.err.println(e.getMessage());
            synchronize();
            return null;
        }
    }

    private Command parseBlock() {
        int line = currentToken.line();
        int col  = currentToken.column();

        matchKeyword("begin");
        BlockCommand block = new BlockCommand();
        block.line   = line;
        block.column = col;

        while (!isKeyword("end") && currentToken.type() != TokenType.EOF) {
            Command cmd = parseCommand();
            if (cmd != null) block.commands.add(cmd);
        }
        matchKeyword("end");
        return block;
    }

    private Command parseIf() {
        int line = currentToken.line();
        int col  = currentToken.column();

        matchKeyword("if");
        Expression condition = parseExpression();
        matchKeyword("then");
        Command thenBranch = parseCommand();

        Command elseBranch = null;
        if (isKeyword("else")) {
            advance();
            elseBranch = parseCommand();
        }

        IfCommand cmd = new IfCommand(condition, thenBranch, elseBranch);
        cmd.line   = line;
        cmd.column = col;
        return cmd;
    }

    private Command parseWhile() {
        int line = currentToken.line();
        int col  = currentToken.column();

        matchKeyword("while");
        Expression condition = parseExpression();
        matchKeyword("do");
        Command body = parseCommand();

        WhileCommand cmd = new WhileCommand(condition, body);
        cmd.line   = line;
        cmd.column = col;
        return cmd;
    }

    private Command parsePrint() {
        int line = currentToken.line();
        int col  = currentToken.column();

        advance(); // consome writeln ou print
        Expression expr = parseExpression();
        matchDelimiter(";");

        PrintCommand cmd = new PrintCommand(expr);
        cmd.line   = line;
        cmd.column = col;
        return cmd;
    }

    private Command parseAssignment() {
        int line   = currentToken.line();
        int col    = currentToken.column();
        String idName = currentToken.text();

        match(TokenType.IDENTIFIER);

        if (isOp(":=")) {
            advance();
        } else {
            throw new RuntimeException(
                    "Erro: Esperado ':=' na linha " + currentToken.line());
        }

        Expression expr = parseExpression();
        matchDelimiter(";");

        Identifier id = new Identifier(idName);
        id.line   = line;
        id.column = col;

        AssignmentCommand cmd = new AssignmentCommand(id, expr);
        cmd.line   = line;
        cmd.column = col;
        return cmd;
    }

    // ── Parsing de expressões (precedência: + - < > = << >> ) ────────────

    public Expression parseExpression() {
        int line = currentToken.line();
        int col  = currentToken.column();
        Expression left = parseTerm();

        while (isOp("+") || isOp("-") || isOp("<") || isOp(">")
                || isOp("=") || isOp("<=") || isOp(">=") || isOp("<>")) {
            String op = currentToken.text();
            advance();
            Expression right = parseTerm();

            BinaryExpression bin = new BinaryExpression(left, right, op);
            bin.line   = line;
            bin.column = col;
            left = bin;
        }
        return left;
    }

    private Expression parseTerm() {
        int line = currentToken.line();
        int col  = currentToken.column();
        Expression left = parseFactor();

        while (isOp("*") || isOp("/") || isKeyword("div") || isKeyword("mod")) {
            String op = currentToken.text();
            advance();
            Expression right = parseFactor();

            BinaryExpression bin = new BinaryExpression(left, right, op);
            bin.line   = line;
            bin.column = col;
            left = bin;
        }
        return left;
    }

    private Expression parseFactor() {
        int line = currentToken.line();
        int col  = currentToken.column();

        if (currentToken.type() == TokenType.NUMBER) {
            String val = currentToken.text();
            advance();
            Literal literal = new Literal(Integer.parseInt(val));
            literal.line   = line;
            literal.column = col;
            return literal;

        } else if (currentToken.type() == TokenType.IDENTIFIER) {
            String name = currentToken.text();
            advance();
            Identifier id = new Identifier(name);
            id.line   = line;
            id.column = col;
            return id;

        } else if (isDelimiter("(")) {
            advance();
            Expression expr = parseExpression();
            matchDelimiter(")");
            return expr;

        } else if (isKeyword("not")) {
            advance();
            Expression operand = parseFactor();
            UnaryExpression u = new UnaryExpression(operand, "not");
            u.line   = line;
            u.column = col;
            return u;
        }

        throw new RuntimeException(
                "Erro: Expressão inválida perto de '"
                + currentToken.text() + "' na linha " + currentToken.line());
    }

    private void synchronize() {
        while (currentToken.type() != TokenType.EOF && !isDelimiter(";")) {
            advance();
        }
        if (isDelimiter(";")) advance();
    }
}