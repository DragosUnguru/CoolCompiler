package cool.structures;

import java.util.LinkedHashMap;
import java.util.Map;

import static cool.structures.BasePassVisitor.SELF;

public class ClassSymbol extends IdSymbol implements Scope {
    protected Map<String, Symbol> attributeSymbols = new LinkedHashMap<>();
    protected Map<String, Symbol> methodSymbols = new LinkedHashMap<>();
    protected Scope parent;

    public ClassSymbol(String name, Scope parent) {
        super(name);
        this.parent = parent;

        // Also define "self" as an attribute of current's class type as return type
        this.attributeSymbols.put(SELF, new IdSymbol(SELF, new TypeSymbol(name)));
    }

    @Override
    public boolean add(Symbol sym) {
        String symbolName = sym.getName();

        if (attributeSymbols.containsKey(symbolName)) {
            return false;
        }

        attributeSymbols.put(symbolName, sym);
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
        return attributeSymbols.containsKey(str) ? attributeSymbols.get(str) : methodSymbols.get(str);
    }

    @Override
    public Symbol searchInScope(String str) {
        return attributeSymbols.containsKey(str) ? attributeSymbols.get(str) : methodSymbols.get(str);
    }

    @Override
    public Scope getParent() {
        return this.parent;
    }
}
