package cool.structures;

import java.util.Map;

public class TypeSymbol extends Symbol {

    private static Map<String, TypeSymbol> BASE_CLASSES = Map.of(
            "IO", new TypeSymbol("IO"),
            "String", new TypeSymbol("String"),
            "Int", new TypeSymbol("Int"),
            "Bool", new TypeSymbol("Bool"),
            "Object", new TypeSymbol("Object")
    );

    public TypeSymbol getType(String className) {
        return BASE_CLASSES.get(className);
    }

    public void defineType(String className) {
        BASE_CLASSES.putIfAbsent(className, new TypeSymbol(className));
    }

    public TypeSymbol(String name) {
        super(name);
    }
}
