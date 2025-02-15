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

    public int getNumberOfArgs() {
        return methodDef == null ? BaseTypeSymbolFactory.getNoOfArgs(this.name) : methodDef.getNoOfArgs();
    }

    public TypeSymbol getArgType(int idx) {
        return methodDef == null ? BaseTypeSymbolFactory.getArgTypesOf(this.name).get(idx) :
                methodDef.getArgs().get(idx).getSymbol().getType();
    }

    public String getArgName(int idx) {
        return methodDef == null ? (name.equals("substr") ? "l" : "x") :
                methodDef.getArgs().get(idx).getName().getToken().getText();
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
