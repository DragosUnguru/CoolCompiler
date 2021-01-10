package cool.structures;

import cool.compiler.*;


import static cool.structures.SymbolTable.*;

public class DefinitionPassVisitor extends BasePassVisitor {
    private static Integer ID = 0;
    private Scope currentScope;

    @Override
    public TypeSymbol visit(Program program) {
        currentScope = globals;

        for (var coolClass : program.getClasses()) {
            if (coolClass != null)
                coolClass.accept(this);
        }

        return null;
    }

    @Override
    public TypeSymbol visit(CoolClass coolClass) {
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
    public TypeSymbol visit(Id id) {
        if (id.getScope() == null) {
            id.setScope(currentScope);
        }

        return null;
    }

    @Override
    public TypeSymbol visit(VarDef varDef) {
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

//        varDef.getType().accept(this);
//        varDef.getName().accept(this);
        if (varDef.getExpr() != null) {
            varDef.getExpr().accept(this);
        }

        return null;
    }

    @Override
    public TypeSymbol visit(MethodDef methodDef) {
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
        if (!((ClassSymbol) currentScope).add(methodSymbol)) {
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
//        methodDef.getName().accept(this);
//        methodDef.getReturnType().accept(this);

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
    public TypeSymbol visit(Formal formal) {
        Id formalId = formal.getName();
        Type formalType = formal.getType();
        String formalName = formalId.getToken().getText();
        String typeName = formalType.getToken().getText();

        TypeSymbol typeSymbol = new TypeSymbol(typeName);
        IdSymbol idSymbol = new IdSymbol(formalName, typeSymbol);

        // If formal is a method argument and has been redefined
        if (currentScope instanceof MethodSymbol && !currentScope.add(idSymbol)) {
            String methodName = ((MethodSymbol) currentScope).getName();
            String className = ((ClassSymbol) currentScope.getParent()).getName();

            String errorMsg = ErrorMessages.MethodArguments.redefined(className, methodName, formalName);
            error(formal.getToken(), errorMsg);

            return null;
        }

        // If formal has illegal name self
        if (formalName.equals(SELF)) {
            String errorMsg = ErrorMessages.illegalNameSelf(currentScope);
            error(formal.getToken(), errorMsg);

            return null;
        }

        // If formal is of illegal type SELF_TYPE
        if (!(currentScope instanceof LetInSymbol) && typeName.equals(SELF_TYPE)) {
            String errorMsg = ErrorMessages.illegalTypeSelfType(currentScope, formalName);
            error(formal.getType().getToken(), errorMsg);

            return null;
        }

        // If formal is of undefined type
        if (globals.lookup(typeName) == null) {
            String errorMsg = ErrorMessages.undefinedType(currentScope, formalName, typeName);
            error(formal.getType().getToken(), errorMsg);

            return null;
        }

//        // LetIn and Case structures don't have redefinition errors
//        if (!(currentScope instanceof MethodSymbol) && !currentScope.add(idSymbol)) {
//            if (!currentScope.add(idSymbol)) {
//                return null;
//            }
//        }
        currentScope.add(idSymbol);
        formal.setSymbol(idSymbol);
        formal.getName().setScope(currentScope);
        formal.getName().setSymbol(idSymbol);

        return null;
    }

    @Override
    public TypeSymbol visit(LetIn letIn) {
        LetInSymbol symbol = new LetInSymbol((ID++).toString(), currentScope);

        letIn.setSymbol(symbol);
        currentScope.add(symbol);

        currentScope = symbol;
        for (InitedFormal formal : letIn.getFormals()) {
            formal.accept(this);
        }
        letIn.getBody().accept(this);
        currentScope = currentScope.getParent();

        return null;
    }

    /**
     * Formals of this type (with possible initialization expression)
     * are exclusively part of letIn structures
     * @param initedFormal
     * @return
     */
    @Override
    public TypeSymbol visit(InitedFormal initedFormal) {
        initedFormal.getFormal().accept(this);

        // LetIn's initialization expressions of formals
        if (initedFormal.getInitExpr() != null) {
            initedFormal.getInitExpr().accept(this);
        }
        return null;
    }

    @Override
    public TypeSymbol visit(Case caseStatement) {
        CaseSymbol symbol = new CaseSymbol((ID++).toString(), currentScope);
        caseStatement.setSymbol(symbol);

        currentScope.add(symbol);
        currentScope = symbol;
        caseStatement.getCond().accept(this);
        for (Formal formal : caseStatement.getFormals()) {
            formal.accept(this);
        }
        for (Expression expression : caseStatement.getThen()) {
            expression.accept(this);
        }
        currentScope = currentScope.getParent();

        return null;
    }

    @Override
    public TypeSymbol visit(Assign assign) {
        assign.getName().accept(this);
        assign.getExpr().accept(this);

        return null;
    }

    @Override
    public TypeSymbol visit(Arithmetic arithmetic) {
        arithmetic.getLeft().accept(this);
        arithmetic.getRight().accept(this);

        return null;
    }

    @Override
    public TypeSymbol visit(Relational relational) {
        relational.getLeft().accept(this);
        relational.getRight().accept(this);

        return null;
    }

    @Override
    public TypeSymbol visit(Negation negation) {
        negation.getExpr().accept(this);
        return null;
    }

    @Override
    public TypeSymbol visit(Paren paren) {
        paren.getExpr().accept(this);
        return null;
    }

    @Override
    public TypeSymbol visit(Not not) {
        not.getExpr().accept(this);
        return null;
    }

    @Override
    public TypeSymbol visit(While whileLoop) {
        whileLoop.getCond().accept(this);
        whileLoop.getBody().accept(this);

        return null;
    }

    @Override
    public TypeSymbol visit(If ifStatement) {
        ifStatement.getCond().accept(this);
        ifStatement.getThen().accept(this);
        ifStatement.getElseOutcome().accept(this);

        return null;
    }

    @Override
    public TypeSymbol visit(InstructionBlock instructionBlock) {
        for (Expression expression : instructionBlock.getBody()) {
            expression.accept(this);
        }
        return null;
    }


    @Override
    public TypeSymbol visit(StaticMethodCall staticMethodCall) {
        // Visit Id to set the scope of the token
        staticMethodCall.getMethodName().accept(this);
        for (Expression expression : staticMethodCall.getArgs()) {
            expression.accept(this);
        }
        return null;
    }

    @Override
    public TypeSymbol visit(MethodCall methodCall) {
        // Visit Id and caller to set the scope of the tokens
        methodCall.getMethodName().accept(this);
        methodCall.getCaller().accept(this);

        for (Expression expression : methodCall.getArgs()) {
            expression.accept(this);
        }

        return null;
    }
}
