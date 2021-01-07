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
//        String idName = id.getToken().getText();
//        IdSymbol symbol = new IdSymbol(idName);
//
//        id.setSymbol(symbol);
//        id.setScope(currentScope);

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
        TypeSymbol typeSymbol = new TypeSymbol(typeName);
        IdSymbol varSymbol = new IdSymbol(varName, typeSymbol);

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

    @Override
    public Void visit(MethodDef methodDef) {
        Id methodId = methodDef.getName();
        Type methodReturnType = methodDef.getReturnType();

        // Get actual names of the method and method's return type
        String methodName = methodId.getToken().getText();
        String returnTypeName = methodReturnType.getToken().getText();

        // Current scope is always the class as the method definitions are made only within the class body
        String className = ((ClassSymbol) currentScope).getName();

        // Create symbol for method and method's return type
        TypeSymbol typeSymbol = new TypeSymbol(returnTypeName);
        MethodSymbol methodSymbol = new MethodSymbol(methodName, currentScope, typeSymbol, methodDef);
        methodDef.getName().setSymbol(methodSymbol);
        methodDef.getName().setScope(currentScope);

        // If method is redefined within this scope
        if (!currentScope.add(methodSymbol)) {
            String errorMsg = ErrorMessages.MethodDefinitions.redefined(className, methodName);
            error(methodDef.getToken(), errorMsg);

            return null;
        }

        // If method return type is undefined
        if (globals.lookup(returnTypeName) == null) {
            String errorMsg = ErrorMessages.MethodDefinitions.undefinedReturnType(className, methodName, returnTypeName);
            error(methodDef.getReturnType().getToken(), errorMsg);

            return null;
        }

        // Visit method's body, formals
        currentScope = methodSymbol;
        methodDef.getName().accept(this);
        methodDef.getReturnType().accept(this);

        for (Formal formal : methodDef.getArgs()) {
            formal.accept(this);
        }

        for (Expression expression : methodDef.getBody()) {
            expression.accept(this);
        }
        currentScope = currentScope.getParent();

        return null;
    }

    @Override
    public Void visit(Formal formal) {
        Id formalId = formal.getName();
        Type formalType = formal.getType();
        String formalName = formalId.getToken().getText();
        String typeName = formalType.getToken().getText();

        TypeSymbol typeSymbol = new TypeSymbol(typeName);
        IdSymbol idSymbol = new IdSymbol(formalName, typeSymbol);

        // Current scope is the method symbol. Parent scope represents the class
        String methodName = ((MethodSymbol) currentScope).getName();
        String className = ((ClassSymbol) currentScope.getParent()).getName();

        // If formal is redefined
        if (!currentScope.add(idSymbol)) {
            String errorMsg = ErrorMessages.MethodArguments.redefined(className, methodName, formalName);
            error(formal.getToken(), errorMsg);

            return null;
        }

        // If formal has illegal name self
        if (formalName.equals(SELF)) {
            String errorMsg = ErrorMessages.MethodArguments.illegalNameSelf(className, methodName);
            error(formal.getToken(), errorMsg);

            return null;
        }

        // If formal is of illegal type SELF_TYPE
        if (typeName.equals(SELF_TYPE)) {
            String errorMsg = ErrorMessages.MethodArguments.illegalTypeSelfType(className, methodName, formalName);
            error(formal.getType().getToken(), errorMsg);

            return null;
        }

        // If formal is of undefined type
        if (globals.lookup(typeName) == null) {
            String errorMsg = ErrorMessages.MethodArguments.undefined(className, methodName, formalName, typeName);
            error(formal.getType().getToken(), errorMsg);

            return null;
        }

        return null;
    }

    @Override
    public Void visit(LetIn letIn) {
        LetInSymbol symbol = new LetInSymbol("name", currentScope);

        currentScope = symbol;
        for (InitedFormal formal : letIn.getFormals()) {
            formal.accept(this);
        }
        letIn.getBody().accept(this);
        currentScope = currentScope.getParent();
    }

    /**
     * Formals of this type (with possible initialization expression)
     * are exclusively part of letIn structures
     * @param initedFormal
     * @return
     */
    @Override
    public Void visit(InitedFormal initedFormal) {

    }
}
