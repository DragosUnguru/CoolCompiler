package cool.structures;

import java.util.LinkedHashMap;
import java.util.Map;

public class LetInSymbol extends IdSymbol implements Scope {

    protected Map<String, Symbol> symbols = new LinkedHashMap<>();
    protected Scope parent;

    public LetInSymbol(String name, Scope parent) {
        super(name);
        this.parent = parent;
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
    public Symbol searchInScope(String str) {
        return symbols.get(str);
    }

    @Override
    public Scope getParent() {
        return this.parent;
    }
}
