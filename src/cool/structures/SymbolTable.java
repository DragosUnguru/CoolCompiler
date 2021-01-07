package cool.structures;

import java.util.*;

import org.antlr.v4.runtime.*;

public class SymbolTable {

    private static final Map<String, List<String>> BASE_CLASSES = Map.of(
            "Object",   new ArrayList<>(),
            "Int",      new ArrayList<>(),
            "Bool",     new ArrayList<>(),
            "String",   List.of("length", "concat", "substr"),
            "IO",       List.of("out_string", "out_int", "in_string", "in_int")
    );

    protected static Scope globals;
    protected static Map<ClassSymbol, ClassSymbol> inheritances;
    private static boolean semanticErrors;
    
    public static void defineBasicClasses() {
        globals = new DefaultScope(null);
        inheritances = new LinkedHashMap<>();
        semanticErrors = false;
        
        // Populate global scope with default classes and their methods
        for (Map.Entry<String, List<String>> classEntry : BASE_CLASSES.entrySet()) {
            String className = classEntry.getKey();
            List<String> classMethods = classEntry.getValue();

            // Create class symbol with global scope as parent for the current base class
            ClassSymbol classSymbol = new ClassSymbol(className, null);

            // Add the base methods of the said class to its scope
            for (String method : classMethods) {
                MethodSymbol methodSymbol = new MethodSymbol(method, classSymbol);
                TypeSymbol returnType = new TypeSymbol(className);

                if (method.equals("length")) {
                    returnType = new TypeSymbol("Int");
                }

                methodSymbol.setType(returnType);
                classSymbol.add(methodSymbol);
            }

            // Add base class to global scope
            globals.add(classSymbol);
        }
    }

    public static IdSymbol getOverriddenSymbol(ClassSymbol startingClassSymbol, IdSymbol needle) {
        ClassSymbol classIterator = inheritances.get(startingClassSymbol);
        Set<ClassSymbol> classSet = new HashSet<>();

        // Search in all inherited classes until we've reached the top
        // or encountered an inheritance cycle
        while (classIterator != null || !classSet.add(classIterator)) {
            String needleName = needle.getName();
            Symbol result = classIterator.lookup(needleName);

            if (result instanceof IdSymbol) {
                return (IdSymbol) result;
            }

            classIterator = inheritances.get(classIterator);
        }

        return null;
    }

    public static MethodSymbol getOverriddenSymbol(ClassSymbol startingClassSymbol, MethodSymbol needle) {
        ClassSymbol classIterator = inheritances.get(startingClassSymbol);
        Set<ClassSymbol> classSet = new HashSet<>();

        // Search in all inherited classes until we've reached the top
        // or encountered an inheritance cycle
        while (classIterator != null || !classSet.add(classIterator)) {
            String needleName = needle.getName();
            Symbol result = classIterator.lookup(needleName);

            if (result instanceof MethodSymbol) {
                return (MethodSymbol) result;
            }

            classIterator = inheritances.get(classIterator);
        }

        return null;
    }
    
    /**
     * Displays a semantic error message.
     * 
     * @param ctx Used to determine the enclosing class context of this error,
     *            which knows the file name in which the class was defined.
     * @param info Used for line and column information.
     * @param str The error message.
     */
    public static void error(/*ParserRuleContext ctx, */Token info, String str) {
        /*while (! (ctx.getParent() instanceof CoolParser.ProgramContext))
            ctx = ctx.getParent();*/
        
        String message = /*"\"" + new File(Compiler.fileNames.get(ctx)).getName()
                + */"\", line " + info.getLine()
                + ":" + (info.getCharPositionInLine() + 1)
                + ", Semantic error: " + str;
        
        System.err.println(message);
        semanticErrors = true;
    }
    
    public static void error(String str) {
        String message = "Semantic error: " + str;
        
        System.err.println(message);
        semanticErrors = true;
    }
    
    public static boolean hasSemanticErrors() {
        return semanticErrors;
    }
}
