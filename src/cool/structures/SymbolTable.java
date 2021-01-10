package cool.structures;

import java.util.*;
import org.antlr.v4.runtime.*;

import static cool.compiler.Compiler.FILENAME;

public class SymbolTable {

    private static final Map<String, List<String>> BASE_CLASSES = Map.of(
            "Object",   new ArrayList<>(), // TODO add object's methods
            "Int",      new ArrayList<>(),
            "Bool",     List.of("true", "false"),
            "String",   List.of("length", "concat", "substr"),
            "IO",       List.of("out_string", "out_int", "in_string", "in_int")
    );

    protected static Scope globals;
    protected static Map<ClassSymbol, ClassSymbol> inheritances;
    private static boolean semanticErrors;

    /**
     * Defines the basic classes, their methods and return types of the COOL's language.
     * Defines the global scope and inserts the said classes in it alongside with other
     * utility structures
     */
    public static void defineBasicClasses() {
        List<ClassSymbol> baseClassesSymbols = new ArrayList<>();
        globals = new DefaultScope(null);
        inheritances = new LinkedHashMap<>();
        semanticErrors = false;
        
        // Populate global scope with default classes and their methods
        for (Map.Entry<String, List<String>> classEntry : BASE_CLASSES.entrySet()) {
            String className = classEntry.getKey();
            List<String> classMethods = classEntry.getValue();

            // Create class symbol with global scope as parent for the current base class
            ClassSymbol classSymbol = new ClassSymbol(className, null);
            classSymbol.setType(BaseTypeSymbolFactory.get(className));

            // Add the base methods of the said class to its scope
            for (String formalName : classMethods) {
                IdSymbol symbol;
                TypeSymbol returnType = BaseTypeSymbolFactory.get(className);

                // For Bool class, the formals are attributes, not methods
                if (className.equals("Bool")) {
                    symbol = new IdSymbol(formalName);
                } else {
                    symbol = new MethodSymbol(formalName, classSymbol);
                }

                // All return SELF_TYPE, excepting String's class 'length' that returns Int
                if (formalName.equals("length")) {
                    returnType = BaseTypeSymbolFactory.getINT();
                }

                symbol.setType(returnType);
                classSymbol.add(symbol);
            }

            // Add base class to global scope
            globals.add(classSymbol);

            // So we can later link inheritance relationships for all base classes to Object class
            if (!className.equals("Object")) {
                baseClassesSymbols.add(classSymbol);
            }
        }

        // All base classes inherit Object class
        ClassSymbol objectClass = (ClassSymbol) globals.lookup("Object");
        for (ClassSymbol classSymbol : baseClassesSymbols) {
            inheritances.put(classSymbol, objectClass);
        }
    }

    /**
     * By searching through the inheritance structure of the classes, search
     * and return the overridden attribute of the startingClassSymbol's superclass
     * @param startingClassSymbol the subclass symbol containing the definition of the "needle" attribute
     * @param needle subclass attribute id symbol
     * @return id symbol of the superclass overridden attribute, null if the there is no overridden
     * attribute in the inherited classes
     */
    public static IdSymbol getOverriddenSymbol(ClassSymbol startingClassSymbol, IdSymbol needle) {
        ClassSymbol classIterator = inheritances.get(startingClassSymbol);

        // Search in all inherited classes until we've reached the top
        // or encountered an inheritance cycle
        while (classIterator != null) {
            String needleName = needle.getName();
            Symbol result = classIterator.lookup(needleName);

            if (result instanceof IdSymbol) {
                return (IdSymbol) result;
            }

            classIterator = inheritances.get(classIterator);
        }

        return null;
    }

    /**
     * Overloaded method. By searching through the inheritance structure of the classes, search
     * and return the overridden method of the startingClassSymbol's superclass
     * @param startingClassSymbol the subclass symbol containing the definition of the "needle" method
     * @param needle subclass method symbol
     * @return method symbol of the superclass overridden method, null if the there is no overridden
     * method in the inherited classes
     */
    public static MethodSymbol getOverriddenSymbol(ClassSymbol startingClassSymbol, MethodSymbol needle) {
        ClassSymbol classIterator = inheritances.get(startingClassSymbol);

        // Search in all inherited classes until we've reached the top
        // or encountered an inheritance cycle
        while (classIterator != null) {
            String needleName = needle.getName();
            Symbol result = classIterator.lookup(needleName);

            if (result instanceof MethodSymbol) {
                return (MethodSymbol) result;
            }

            classIterator = inheritances.get(classIterator);
        }

        return null;
    }


    public static boolean isSuperclass(ClassSymbol subClass, ClassSymbol superClass) {
        ClassSymbol classIterator = inheritances.get(subClass);

        // Search in all inherited classes until we've reached the top
        // or encountered an inheritance cycle
        while (classIterator != null) {
            if (classIterator.getName().equals(superClass.getName())) {
                return true;
            }

            classIterator = inheritances.get(classIterator);
        }

        return false;
    }

    private static ClassSymbol getCommonSuperClass(ClassSymbol class1, ClassSymbol class2) {
        ClassSymbol classIterator = class1;
        List<String> class1SuperClasses = new ArrayList<>();

        // Compute the whole inheritance structure of one class
        while (classIterator != null) {
            class1SuperClasses.add(classIterator.getName());
            classIterator = inheritances.get(classIterator);
        }

        // Start from the class itself as it can already be a superclass of class1
        classIterator = class2;
        while (classIterator != null) {
            if (class1SuperClasses.contains(classIterator.getName())) {
                // Found the first common superclass
                return classIterator;
            }

            classIterator = inheritances.get(classIterator);
        }

        // Return Object class as it's the root of the inheritance graph
        return (ClassSymbol) globals.lookup(BaseTypeSymbolFactory.getOBJECT().getName());
    }

    public static ClassSymbol getCommonSuperclass(List<ClassSymbol> classes) {
        int noOfClasses = classes.size();

        if (noOfClasses == 0) {
            return (ClassSymbol) globals.lookup(BaseTypeSymbolFactory.getOBJECT().getName());
        }

        if (noOfClasses == 1) {
            return classes.get(0);
        }

        // Compute the common type of each pair of consecutive classes, using the result
        // from the previous computation to determine the superclass of all given classes
        ClassSymbol commonTypeSoFar = getCommonSuperClass(classes.get(0), classes.get(1));
        for (int i = 2; i < noOfClasses - 1; ++i) {
            // If it was evaluated to Object already, there is no way up from here
            if (commonTypeSoFar.getName().equals(BaseTypeSymbolFactory.getOBJECT().getName())) {
                return commonTypeSoFar;
            }

            commonTypeSoFar = getCommonSuperClass(commonTypeSoFar, classes.get(i));
        }

        return commonTypeSoFar;
    }

    public static MethodSymbol getDispatchedMethod(ClassSymbol startingClass, String method) {
        ClassSymbol classIterator = startingClass;

        while (classIterator != null) {
            Symbol dispatchedMethod = classIterator.searchInScope(method);

            if (dispatchedMethod instanceof MethodSymbol) {
                return (MethodSymbol) dispatchedMethod;
            }

            classIterator = inheritances.get(classIterator);
        }

        return null;
    }
    
    /**
     * Displays a semantic error message.
     *
     * @param info Used for line and column information.
     * @param str The error message.
     */
    public static void error(Token info, String str) {
        String message = "\"" + FILENAME
                + "\", line " + info.getLine()
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
