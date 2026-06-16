package br.com.comcet.tp1.symbols;

import java.util.List;
import java.util.ArrayList;

public class Symbol {
    public String name;
    public String type;
    public Object value;
    
    public boolean isFunction = false;
    public List<String> paramTypes = new ArrayList<>();

    public Symbol(String name, String type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Symbol[name=" + name + ", type=" + type + ", value=" + value + ", isFunction=" + isFunction + "]";
    }
}