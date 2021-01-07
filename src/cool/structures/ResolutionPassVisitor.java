package cool.structures;

import cool.compiler.*;

import java.util.HashSet;
import java.util.Set;

import static cool.structures.SymbolTable.*;

public class ResolutionPassVisitor extends BasePassVisitor {
    private Scope currentScope;

    @Override
    public Void visit(Program program) {
        currentScope = globals;

        for (var coolClass : program.getClasses()) {
            if (coolClass != null)
                coolClass.accept(this);
        }

        return null;
    }

    @Override
    public Void visit(CoolClass coolClass) {
        String className = coolClass.getClassName().getToken().getText();
        ClassSymbol classSymbol = (ClassSymbol) currentScope.lookup(className);

        // Using a set, determine if there is any inheritance cycles starting from this class
        ClassSymbol superclassIter = inheritances.get(classSymbol);
        Set<ClassSymbol> classSet = new HashSet<>();
        classSet.add(classSymbol);

        while (superclassIter != null) {
            if (!classSet.add(superclassIter)) {
                String errorMsg = ErrorMessages.ClassDefinitions.inheritanceCycle(className);
                error(coolClass.getClassName().getToken(), errorMsg);

                return null;
            }

            superclassIter = inheritances.get(superclassIter);
        }

        // Visit class var defs and method defs
        currentScope = classSymbol;
        for (Feature feature : coolClass.getFeatures()) {
            feature.accept(this);
        }
        // Restore scope
        currentScope = globals;

        return null;
    }

    @Override
    public Void visit(VarDef varDef) {
        Id varId = varDef.getName();

        // Get actual names of the variable and variable's type
        String varName = varId.getToken().getText();
        IdSymbol symbol = varId.getSymbol();

        // Current scope is always the class as the variable definitions are made only within the class body
        String className = ((ClassSymbol) currentScope).getName();

        // If attribute is defined in superclass
        if (getOverriddenSymbol((ClassSymbol) currentScope, symbol) != null) {
            String errorMsg = ErrorMessages.AttributeDefinitions.redefinesInherited(className, varName);
            error(varDef.getToken(), errorMsg);

            return null;
        }

        return null;
    }

    @Override
    public Void visit(MethodDef methodDef) {
        Id methodId = methodDef.getName();

        // Get actual name and symbol of the method
        String methodName = methodId.getToken().getText();
        MethodSymbol symbol = (MethodSymbol) methodId.getSymbol();

        // Current scope is always the class as the method definitions are made only within the class body
        String className = ((ClassSymbol) currentScope).getName();

        // If this method overrides a method in it's superclass
        MethodSymbol overriddenSymbol = getOverriddenSymbol((ClassSymbol) currentScope, symbol);
        if (overriddenSymbol != null) {
            MethodDef overriddenMethod = overriddenSymbol.methodDef;
            int noOfArgs = overriddenMethod.getNoOfArgs();

            // Overrides method with wrong number of arguments
            if (noOfArgs != methodDef.getNoOfArgs()) {
                String errorMsg = ErrorMessages.MethodDefinitions.overrideDifferentArgNumber(className, methodName);
                error(methodDef.getToken(), errorMsg);

                return null;
            }

            // Overrides method with wrong return type
            String overriddenMethodType = overriddenMethod.getReturnType().getToken().getText();
            String methodType = methodDef.getReturnType().getToken().getText();
            if (!overriddenMethodType.equals(methodType)) {
                String errorMsg = ErrorMessages.MethodDefinitions.overrideDifferentReturnType(className, methodName,
                        overriddenMethodType, methodType);
                error(methodDef.getReturnType().getToken(), errorMsg);

                return null;
            }

            // Argument types differ from the overridden method
            for (int i = 0; i < noOfArgs; ++i) {
                Formal overriddenFormal = overriddenMethod.getArgs().get(i);
                Formal currentFormal = methodDef.getArgs().get(i);

                String overriddenFormalType = overriddenFormal.getType().getToken().getText();
                String currentFormalType = currentFormal.getType().getToken().getText();

                if (!overriddenFormalType.equals(currentFormalType)) {
                    String currentFormalName = currentFormal.getName().getToken().getText();
                    String errorMsg = ErrorMessages.MethodDefinitions.overrideDifferentArgType(className, methodName,
                            currentFormalName, overriddenFormalType, currentFormalType);
                    error(currentFormal.getType().getToken(), errorMsg);

                    return null;
                }
            }

            return null;
        }

        return null;
    }
}
