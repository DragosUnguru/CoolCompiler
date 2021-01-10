package cool.structures;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class BaseTypeSymbolFactory {
    public static final String MAIN_METHOD = "main";

    private static final TypeSymbol INT = new TypeSymbol("Int");
    private static final TypeSymbol BOOL = new TypeSymbol("Bool");
    private static final TypeSymbol STRING = new TypeSymbol("String");
    private static final TypeSymbol IO = new TypeSymbol("IO");
    private static final TypeSymbol OBJECT = new TypeSymbol("Object");
    private static final Map<String, TypeSymbol> MAPPED = Map.of(
            "Int", INT,
            "Bool", BOOL,
            "String", STRING,
            "IO", IO,
            "Object", OBJECT
    );

    private static final List<String> METHOD_NAMES = List.of(
            "length", "concat", "substr", "out_string", "out_int", "in_string", "in_int", "abort", "type_name", "copy");
    private static final List<List<TypeSymbol>> METHOD_ARG_TYPE = List.of(
            Collections.emptyList(), List.of(STRING), List.of(INT, INT), List.of(STRING), List.of(INT), Collections.emptyList(),
            Collections.emptyList(), Collections.emptyList(), Collections.emptyList(), Collections.emptyList());


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

    public static TypeSymbol getOBJECT() {
        return OBJECT;
    }

    public static TypeSymbol get(String type) {
        return MAPPED.get(type);
    }

    public static boolean isPrimitive(String type) {
        return MAPPED.containsKey(type);
    }

    public static boolean isBaseMethod(String methodName) {
        return METHOD_NAMES.contains(methodName);
    }

    public static int getNoOfArgs(String methodName) {
        return METHOD_ARG_TYPE.get(METHOD_NAMES.indexOf(methodName)).size();
    }

    public static List<TypeSymbol> getArgTypesOf(String methodName) {
        return METHOD_ARG_TYPE.get(METHOD_NAMES.indexOf(methodName));
    }
}
