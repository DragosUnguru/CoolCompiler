package cool.structures;

public class BaseTypeSymbolFactory {

    private static final TypeSymbol INT = new TypeSymbol("Int");
    private static final TypeSymbol BOOL = new TypeSymbol("Bool");
    private static final TypeSymbol STRING = new TypeSymbol("String");
    private static final TypeSymbol IO = new TypeSymbol("IO");


    private BaseTypeSymbolFactory() { }

    public static TypeSymbol getBOOL() {
        return BOOL;
    }

    public static TypeSymbol getIO() {
        return IO;
    }

    public static TypeSymbol getSTRING() {
        return STRING;
    }

    public static TypeSymbol getINT() {
        return INT;
    }
}
