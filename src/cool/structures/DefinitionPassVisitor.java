package cool.structures;

import cool.compiler.*;

import static cool.structures.SymbolTable.*;

public class DefinitionPassVisitor extends BasePassVisitor {
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
        // Get class name and create class scope
        String className = coolClass.getClassName().getToken().getText();
        ClassSymbol classSymbol = (ClassSymbol) currentScope.lookup(className);
        ClassSymbol superClassSymbol = null;

        // If this class inherits a superclass
        if (coolClass.getSuperClass() != null) {
            String superClassName = coolClass.getSuperClass().getToken().getText();

            // Get symbols of the superclass, stored in global scope from the previous pass
            superClassSymbol = (ClassSymbol) currentScope.lookup(superClassName);

            // If the superclass isn't defined
            if (superClassSymbol == null) {
                String errorMsg = ErrorMessages.ClassDefinitions.undefinedParent(className, superClassName);
                error(coolClass.getSuperClass().getToken(), errorMsg);

                return null;
            }
        }

        // Store symbols in <class, superclass> global pair
        inheritances.put(classSymbol, superClassSymbol);

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
    public Void visit(Id id) {
        String idName = id.getToken().getText();
        Symbol symbol = currentScope.lookup(idName);

        if (symbol == null) {
            // ErrorMsg not defined
            return null;
        }

        id.setSymbol((IdSymbol) symbol);
        id.setScope(currentScope);

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

        // Create symbol for variable and variable's type
        IdSymbol varSymbol = new IdSymbol(varName);
        TypeSymbol typeSymbol = new TypeSymbol(typeName);

        varSymbol.setType(typeSymbol);
        varId.setScope(currentScope);
        varId.setSymbol(varSymbol);

        // If attribute name is "self"
        if (varName.equals(SELF)) {
            String errorMsg = ErrorMessages.AttributeDefinitions.illegalNameSelf(className);
            error(varDef.getToken(), errorMsg);

            return null;
        }

        // If attribute is redefined within this scope
        if (!currentScope.add(varSymbol)) {
            String errorMsg = ErrorMessages.AttributeDefinitions.redefined(className, varName);
            error(varDef.getToken(), errorMsg);

            return null;
        }

        // If attribute is of undefined type
        if (globals.lookup(typeName) == null) {
            String errorMsg = ErrorMessages.AttributeDefinitions.undefinedType(className, varName, typeName);
            error(varDef.getType().getToken(), errorMsg);

            return null;
        }

        varDef.getType().accept(this);
        varDef.getName().accept(this);
        if (varDef.getExpr() != null) {
            varDef.getExpr().accept(this);
        }

        return null;
    }
}
