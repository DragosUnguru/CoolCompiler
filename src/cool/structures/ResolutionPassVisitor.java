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
        Type varType = varDef.getType();

        // Get actual names of the variable and variable's type
        String varName = varId.getToken().getText();
        String typeName = varType.getToken().getText();

        // Current scope is always the class as the variable definitions are made only within the class body
        String className = ((ClassSymbol) currentScope).getName();

        // If attribute is defined in superclass
        if (getOverriddenSymbol((ClassSymbol) currentScope, varDef.getName().getSymbol()) != null) {
            String errorMsg = ErrorMessages.AttributeDefinitions.redefinesInherited(className, varName);
            error(varDef.getToken(), errorMsg);

            return null;
        }

        return null;
    }
}
