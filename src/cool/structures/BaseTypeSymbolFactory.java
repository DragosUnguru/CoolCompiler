package cool.structures;

import java.util.Map;

public class BaseTypeSymbolFactory {

    private static final TypeSymbol INT = new TypeSymbol("Int");
    private static final TypeSymbol BOOL = new TypeSymbol("Bool");
    private static final TypeSymbol STRING = new TypeSymbol("String");
    private static final TypeSymbol IO = new TypeSymbol("IO");
    private static final Map<String, TypeSymbol> MAPPED = Map.of(
            "Int", INT,
            "Bool", BOOL,
            "String", STRING,
            "IO", IO
    );


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

    public static TypeSymbol get(String type) {
        return MAPPED.get(type);
    }

    public static boolean isPrimitive(String type) {
        return MAPPED.containsKey(type);
    }
}
