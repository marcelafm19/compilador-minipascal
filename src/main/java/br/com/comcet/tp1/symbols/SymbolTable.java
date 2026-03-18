package br.com.comcet.tp1.symbols;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Map<String, Symbol> symbols = new HashMap<>();

    public void add(String name, Symbol symbol) {
        if (symbols.containsKey(name)) {
            throw new RuntimeException("Erro: Variável '" + name + "' já declarada.");
        }
        symbols.put(name, symbol);
    }

    public Symbol get(String name) {
        return symbols.get(name);
    }

    @Override
    public String toString() {
        return "SymbolTable: " + symbols.values().toString();
    }
}