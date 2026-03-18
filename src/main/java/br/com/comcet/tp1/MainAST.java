package br.com.comcet.tp1;

import br.com.comcet.tp1.ast.*;

public class MainAST {
    public static void main(String[] args) {
        Expression dez = new Literal(10);
        Expression cinco = new Literal(5);
        Expression soma = new BinaryExpression(dez, cinco, "+");
        
        AssignmentCommand atrib = new AssignmentCommand(new Identifier("x"), soma);
        
        System.out.println("\nAST criada com sucesso:");
        System.out.println("\n" + atrib + "\n");
    }
}