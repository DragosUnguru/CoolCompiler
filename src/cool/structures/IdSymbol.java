package cool.structures;

public class IdSymbol extends Symbol {

    protected TypeSymbol type;

    public IdSymbol(String name) {
        super(name);
    }

    public IdSymbol(String name, TypeSymbol typeSymbol) {
        super(name);
        this.type = typeSymbol;
    }

    public TypeSymbol getType() {
        return type;
    }

    public void setType(TypeSymbol type) {
        this.type = type;
    }
}
