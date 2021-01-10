package cool.structures;

import java.util.*;

import org.antlr.v4.runtime.*;

import static cool.compiler.Compiler.FILENAME;
import static cool.structures.BasePassVisitor.SELF_TYPE;

public class SymbolTable {

    private static final Map<String, List<String>> BASE_CLASSES = Map.of(
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
        globals = new DefaultScope(null);
        inheritances = new LinkedHashMap<>();
        semanticErrors = false;
        Map<String, String> objectMethods = Map.of(
                "abort", "Object",
                "type_name", "String",
                "copy", SELF_TYPE
        );

        // Create Object class separately so we can manage all the different return types of its methods
        // and to also add the rest of the base classes as children
        ClassSymbol objectClass = new ClassSymbol("Object", null);
        objectClass.setType(BaseTypeSymbolFactory.getOBJECT());
        for (Map.Entry<String, String> methodEntry : objectMethods.entrySet()) {
            String methodName = methodEntry.getKey();
            String methodReturnType = methodEntry.getValue();

            MethodSymbol objectMethod = new MethodSymbol(methodName, objectClass);
            objectMethod.setType(new TypeSymbol(methodReturnType));

            objectClass.add(objectMethod);
        }
        globals.add(objectClass);

        // Populate global scope with default classes and their methods
        for (Map.Entry<String, List<String>> classEntry : BASE_CLASSES.entrySet()) {
            String className = classEntry.getKey();
            List<String> formals = classEntry.getValue();

            // Create class symbol with global scope as parent for the current base class
            ClassSymbol classSymbol = new ClassSymbol(className, null);
            classSymbol.setType(BaseTypeSymbolFactory.get(className));

            // Add the base methods of the said class to its scope
            for (String formalName : formals) {
                IdSymbol symbol;
                TypeSymbol returnType = BaseTypeSymbolFactory.get(className);

                // For Bool class, the formals are attributes, not methods
                if (className.equals("Bool")) {
                    symbol = new IdSymbol(formalName);
                } else {
                    symbol = new MethodSymbol(formalName, classSymbol);
                }

                // All return SELF_TYPE, excepting String's class 'length' that returns Int
                if (formalName.equals("length") || formalName.equals("in_int")) {
                    returnType = BaseTypeSymbolFactory.getINT();
                }
                else if (formalName.equals("in_string")) {
                    returnType = BaseTypeSymbolFactory.getSTRING();
                }

                symbol.setType(returnType);
                classSymbol.add(symbol);
            }

            // Add base class to global scope and inherit Object class
            globals.add(classSymbol);
            inheritances.put(classSymbol, objectClass);
        }
    }

    /**
     * By searching through the inheritance structure of the classes, search
     * and return the overridden attribute of the startingClassSymbol's superclass
     *
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
     * Overloaded class for the scenario where the Symbol isn't reachable and won't search for a strong-typed instance.
     * By searching through the inheritance structure of the classes, search
     * and return the overridden symbol of the startingClassSymbol's superclass
     *
     * @param startingClassSymbol the subclass symbol containing the definition of the "needle" symbol
     * @param needle name of the symbol to search for
     * @return symbol of the superclass overridden attribute, null if the there is no overridden
     * attribute in the inherited classes
     */
    public static Symbol getOverriddenSymbol(ClassSymbol startingClassSymbol, String needle) {
        ClassSymbol classIterator = inheritances.get(startingClassSymbol);

        // Search in all inherited classes until we've reached the top
        // or encountered an inheritance cycle
        while (classIterator != null) {
            Symbol result = classIterator.lookup(needle);

            if (result != null) {
                return result;
            }

            classIterator = inheritances.get(classIterator);
        }

        return null;
    }

    /**
     * Overloaded method. By searching through the inheritance structure of the classes, search
     * and return the overridden method of the startingClassSymbol's superclass
     *
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


    /**
     * Checks whether or not subclass inherits superclass
     *
     * @param subClass symbol of the subclass
     * @param superClass symbol of the superclass
     * @return true if subclass inherits superclass, false otherwise
     */
    public static boolean isSuperclass(ClassSymbol subClass, ClassSymbol superClass) {
        ClassSymbol classIterator = inheritances.get(subClass);

        if (superClass == null) {
            return false;
        }

        // Search in all inherited classes until we've reached the top
        // or encountered an inheritance cycle
        while (classIterator != null) {
            if (classIterator.equals(superClass)) {
                return true;
            }

            classIterator = inheritances.get(classIterator);
        }

        return false;
    }

    /**
     * Private helper class that determines the lowest common superclass of two given classes
     *
     * @param class1 classsymbol of class1
     * @param class2 classsymbol of class2
     * @return the lowest common superclass of the given classes
     */
    private static ClassSymbol getCommonSuperClass(ClassSymbol class1, ClassSymbol class2) {
        ClassSymbol classIterator = class1;
        List<ClassSymbol> class1SuperClasses = new ArrayList<>();

        // Compute the whole inheritance structure of one class
        while (classIterator != null) {
            class1SuperClasses.add(classIterator);
            classIterator = inheritances.get(classIterator);
        }

        // Start from the class itself as it can already be a superclass of class1
        classIterator = class2;
        while (classIterator != null) {
            if (class1SuperClasses.contains(classIterator)) {
                // Found the first common superclass
                return classIterator;
            }

            classIterator = inheritances.get(classIterator);
        }

        // Return Object class as it's the root of the inheritance graph
        return (ClassSymbol) globals.lookup(BaseTypeSymbolFactory.getOBJECT().getName());
    }

    /**
     * Determines the lowest common superclass of a list of given classes
     *
     * @param classes list of classes taken into account when computing the lowest superclass
     * @return the lowest common superclass of the given classes
     */
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

    /**
     * Searches for method in the inheritance hierarchy starting from startingClass
     *
     * @param startingClass starting class symbol of the inheritance hierarchy from where to search for the demanded method
     * @param method the searched for method name
     * @return methodsymbol of the method's definition found in the inheritance hierarchy, null if not found
     */
    public static MethodSymbol getDispatchedMethod(ClassSymbol startingClass, String method) {
        // First, go up the parent scope hierarchy until we've reached the class scope
        Scope classIterator = startingClass;

        // Got the class scope, search in inheritance hierarchy
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
     * Given a scope, finds and returns the first ClassSymbol in the scope hierarchy
     *
     * @param currentScope Scope of the starting point
     * @return the first ClassSymbol encountered while going up the parent hierarchy
     */
    public static ClassSymbol getClassOfCurrentScope(Scope currentScope) {
        Scope classIterator = currentScope;

        while (!(classIterator instanceof ClassSymbol)) {
            classIterator = classIterator.getParent();
        }
        return (ClassSymbol) classIterator;
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
