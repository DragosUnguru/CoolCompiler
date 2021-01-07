package cool.structures;

import cool.compiler.MethodDef;

import java.util.LinkedHashMap;
import java.util.Map;

public class MethodSymbol extends IdSymbol implements Scope {

    protected Map<String, Symbol> symbols = new LinkedHashMap<>();
    protected Scope parent;
    protected MethodDef methodDef;

    public MethodSymbol(String name, Scope parent) {
        super(name);
        this.parent = parent;
    }

    public MethodSymbol(String name, Scope parent, TypeSymbol typeSymbol, MethodDef methodDef) {
        super(name, typeSymbol);
        this.parent = parent;
        this.methodDef = methodDef;
    }

    @Override
    public boolean add(Symbol sym) {
        String symbolName = sym.getName();

        if (symbols.containsKey(symbolName)) {
            return false;
        }

        symbols.put(symbolName, sym);
        return true;
    }

    @Override
    public Symbol lookup(String str) {
        Symbol sym = symbols.get(str);

        if (sym != null)
            return sym;

        // Symbol couldn't be found. Search in parent's scope
        if (parent != null)
            return parent.lookup(str);

        return null;
    }

    @Override
    public Scope getParent() {
        return this.parent;
    }
}
