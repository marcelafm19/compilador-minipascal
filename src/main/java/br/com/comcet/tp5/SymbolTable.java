package br.com.comcet.tp5; 

import br.com.comcet.tp1.symbols.Symbol;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    private Deque<Map<String, Symbol>> scopes = new ArrayDeque<>();

    public SymbolTable() {
        scopes.push(new HashMap<>());
    }

    public void enterScope() {
        scopes.push(new HashMap<>());
    }

    public void exitScope() {
        if (scopes.size() > 1) {
            scopes.pop();
        }
    }

    public void add(String name, Symbol symbol) {
        Map<String, Symbol> currentScope = scopes.peek();
        if (currentScope.containsKey(name)) {
            throw new IllegalArgumentException("Erro: Variável '" + name + "' já declarada neste escopo.");
        }
        currentScope.put(name, symbol);
    }

    public Symbol lookup(String name) {
        for (Map<String, Symbol> scope : scopes) {
            if (scope.containsKey(name)) {
                return scope.get(name);
            }
        }
        return null;
    }
}