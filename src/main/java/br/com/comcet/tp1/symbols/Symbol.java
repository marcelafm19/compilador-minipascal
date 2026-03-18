package br.com.comcet.tp1.symbols;

public class Symbol {
    public String name;
    public String type;
    public Object value;

    public Symbol(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Symbol[name=" + name + ", type=" + type + ", value=" + value + "]";
    }
}