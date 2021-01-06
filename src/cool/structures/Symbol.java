package cool.structures;

public class Symbol {
    private static int ID_COUNT;

    private final Integer id;
    protected String name;
    
    public Symbol(String name) {
        this.id = ID_COUNT++;
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public String toString() {
        return getName();
    }

    @Override
    public int hashCode() {
        final int prime = 31;

        return this.getName().hashCode() * this.getClass().hashCode() * prime + id.hashCode();
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

        Symbol sym = (Symbol) obj;
        return this.name.equals(sym.getName());
    }
}
