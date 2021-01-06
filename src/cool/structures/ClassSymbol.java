package cool.structures;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ClassSymbol extends IdSymbol implements Scope {
    protected Map<String, List<Symbol>> attributeSymbols = new LinkedHashMap<>();
    protected Map<String, Symbol> methodSymbols = new LinkedHashMap<>();
    protected Scope parent;

    public ClassSymbol(String name, Scope parent) {
        super(name);
        this.parent = parent;
    }

    @Override
    public boolean add(Symbol sym) {
        String symbolName = sym.getName();

        if (attributeSymbols.containsKey(symbolName)) {
            attributeSymbols.get(symbolName).add(sym);
            return false;
        }

        ArrayList<Symbol> newValue = new ArrayList<>();
        newValue.add(sym);
        attributeSymbols.put(symbolName, newValue);
        return true;
    }

    public boolean add(MethodSymbol sym) {
        String symbolName = sym.getName();

        if (methodSymbols.containsKey(symbolName)) {
            return false;
        }

        methodSymbols.put(symbolName, sym);
        return true;
    }

    @Override
    public Symbol lookup(String str) {
        return attributeSymbols.containsKey(str) ? attributeSymbols.get(str).get(0) : methodSymbols.get(str);
    }

    @Override
    public Scope getParent() {
        return this.parent;
    }
}
