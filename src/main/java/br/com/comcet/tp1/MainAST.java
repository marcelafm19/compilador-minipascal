package br.com.comcet.tp1;

import br.com.comcet.tp1.ast.*;

public class MainAST {
    public static void main(String[] args) {
        // Representando: x := 10 + 5;
        Expression dez = new Literal("10");
        Expression cinco = new Literal("5");
        Expression soma = new BinaryExpression(dez, cinco, "+");
        
        Command atrib = new AssignmentCommand(new Identifier("x"), soma);

        System.out.println("\nAST criada com sucesso:");
        System.out.println("\n" + atrib.printTree());

    }
}