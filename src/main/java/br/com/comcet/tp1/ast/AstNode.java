package br.com.comcet.tp1.ast;

public abstract class AstNode {
    public int line;
    public int column;

    @Override
    public abstract String toString();
}