package cool.structures;

public class IdSymbol extends Symbol {

    protected TypeSymbol type;

    public IdSymbol(String name) {
        super(name);
    }

    public TypeSymbol getType() {
        return type;
    }

    public void setType(TypeSymbol type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        // If both reference the same object
        if (this == obj) {
            return true;
        }

        // If runtime types differ (different name spaces for method symbols and var symbols)
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        // If they are of the same type, have the same name and have the same return type
        IdSymbol sym = (IdSymbol) obj;
        return this.name.equals(sym.getName()) && this.type.getName().equals(sym.type.getName());
    }
}
