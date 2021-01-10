package cool.structures;

import cool.compiler.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static cool.structures.BaseTypeSymbolFactory.MAIN_METHOD;
import static cool.structures.SymbolTable.*;

public class ResolutionPassVisitor extends BasePassVisitor {

    @Override
    public TypeSymbol visit(Program program) {

        for (var coolClass : program.getClasses()) {
            if (coolClass != null)
                coolClass.accept(this);
        }

        return null;
    }

    @Override
    public TypeSymbol visit(CoolClass coolClass) {
        String className = coolClass.getClassName().getToken().getText();
        ClassSymbol classSymbol = (ClassSymbol) globals.lookup(className);

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
        for (Feature feature : coolClass.getFeatures()) {
            feature.accept(this);
        }
        // Restore scope

        return null;
    }

    @Override
    public TypeSymbol visit(VarDef varDef) {
        Id varId = varDef.getName();
        TypeSymbol varDefinedType = varId.getSymbol().getType();

        // Get actual names of the variable and variable's type
        String varName = varId.getToken().getText();
        String typeName = varDefinedType.getName();
        IdSymbol symbol = varId.getSymbol();
        ClassSymbol currentScope = (ClassSymbol) varId.getScope();

        // Current scope is always the class as the variable definitions are made only within the class body
        String className = (currentScope).getName();

        // If attribute is defined in superclass
        if (getOverriddenSymbol(currentScope, symbol) != null) {
            String errorMsg = ErrorMessages.AttributeDefinitions.redefinesInherited(className, varName);
            error(varDef.getToken(), errorMsg);

            return null;
        }

        if (varDef.getExpr() != null) {
            // Visit initialization expression and check for errors
            TypeSymbol resolvedTypeSymbol = varDef.getExpr().accept(this);
            if (resolvedTypeSymbol == null) {
                return null;
            }

            String evaluatedTypeName = resolvedTypeSymbol.getName();

            // Also check if it's a subclass of the expected type
            ClassSymbol resolvedClass = (ClassSymbol) globals.lookup(resolvedTypeSymbol.getName());
            ClassSymbol expectingClass = (ClassSymbol) globals.lookup(varDefinedType.getName());

            // If initialization resolved type doesn't match the declared type
            if (!evaluatedTypeName.equals(typeName) && !isSuperclass(resolvedClass, expectingClass)) {
                String errorMsg = ErrorMessages.AttributeDefinitions.illegalInitExpr(varName, typeName, evaluatedTypeName);
                error(varDef.getExpr().getToken(), errorMsg);

                return null;
            }
        }

        return symbol.getType();
    }

    @Override
    public TypeSymbol visit(MethodDef methodDef) {
        Id methodId = methodDef.getName();
        ClassSymbol currentScope = (ClassSymbol) methodId.getScope();

        // Get actual name and symbol of the method
        String methodName = methodId.getToken().getText();
        MethodSymbol symbol = (MethodSymbol) methodId.getSymbol();

        // Current scope is always the class as the method definitions are made only within the class body
        String className = currentScope.getName();

        // If this method overrides a method in it's superclass
        MethodSymbol overriddenSymbol = getOverriddenSymbol(currentScope, symbol);
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
        }

        TypeSymbol lastTypeSymbol = null;
        Expression lastExpression = null;
        for (Expression expression : methodDef.getBody()) {
            lastTypeSymbol = expression.accept(this);
            lastExpression = expression;
        }

        // Doesn't matter if return type is Object
        if (symbol.getType().getName().equals(BaseTypeSymbolFactory.getOBJECT().getName()) ||
                lastTypeSymbol == null) {
            return symbol.getType();
        }

        // Also check if it's a subclass of the expected type
        ClassSymbol resolvedClass = (ClassSymbol) globals.lookup(lastTypeSymbol.getName());
        ClassSymbol expectingClass = (ClassSymbol) globals.lookup(symbol.getType().getName());

        // If body type doesn't match the declared type
        if (!methodName.equals(MAIN_METHOD) && !lastTypeSymbol.getName().equals(symbol.getType().getName()) &&
            !isSuperclass(resolvedClass, expectingClass)) {
            String errorMsg = ErrorMessages.MethodDefinitions.illegalBodyReturnType(methodName, symbol.getType().getName(), lastTypeSymbol.getName());
            error(lastExpression.getToken(), errorMsg);

            return null;
        }

        return symbol.getType();
    }

    @Override
    public TypeSymbol visit(Id id) {
        // If it doesn't have a scope defined, it wasn't visited
        // in the definition pass
        Scope currentScope = id.getScope();
        if (currentScope == null) {
            String errorMsg = ErrorMessages.Variables.undefined(id.getToken().getText());
            error(id.getToken(), errorMsg);

            return null;
        }

        // Symbol was visited and assigned a scope in the definition pass,
        // but wasn't forward defined either
        IdSymbol symbol = (IdSymbol) currentScope.lookup(id.getToken().getText());

        if (symbol == null) {
            String errorMsg = ErrorMessages.Variables.undefined(id.getToken().getText());
            error(id.getToken(), errorMsg);

            return null;
        }

        id.setSymbol(symbol);
        return symbol.getType();
    }

    @Override
    public TypeSymbol visit(Formal formal) {
        return formal.getSymbol() == null ? null : formal.getSymbol().getType();
    }

    @Override
    public TypeSymbol visit(InitedFormal initedFormal) {
        Formal formal = initedFormal.getFormal();
        Id formalId = formal.getName();
        String formalName = formalId.getToken().getText();

        // Visit the uninitialized part of the formal and check for errors
        TypeSymbol declaredTypeSymbol = formal.accept(this);
        if (declaredTypeSymbol == null) {
            return null;
        }

        // If an initialization expression is defined
        Expression initExpr = initedFormal.getInitExpr();
        if (initExpr != null) {
            // Visit initialization expression and check for errors in expression resolving
            TypeSymbol resolvedType = initExpr.accept(this);
            if (resolvedType == null) {
                return null;
            }

            String resolvedTypeName = resolvedType.getName();
            String declaredTypeName = declaredTypeSymbol.getName();

            // If the declared type and the resolved type don't match
            ClassSymbol gotClass = (ClassSymbol) globals.lookup(resolvedType.getName());
            ClassSymbol expectedClass = (ClassSymbol) globals.lookup(declaredTypeSymbol.getName());

            if (!resolvedTypeName.equals(declaredTypeSymbol.getName()) && !isSuperclass(gotClass, expectedClass)) {
                String errorMsg = ErrorMessages.LetIn.illegalInitExprType(formalName, declaredTypeName, resolvedType.getName());
                error(initExpr.getToken(), errorMsg);

                return null;
            }
        }

        return declaredTypeSymbol;
    }

    @Override
    public TypeSymbol visit(LetIn letIn) {
        LetInSymbol symbol = letIn.getSymbol();

        // Visit formals
        for (InitedFormal formal : letIn.getFormals()) {
            formal.accept(this);
        }

        // Visit body and check for occurring errors
        TypeSymbol resolvedType = letIn.getBody().accept(this);
        if (resolvedType == null) {
            return null;
        }

        symbol.setType(resolvedType);
        return resolvedType;
    }



    @Override
    public TypeSymbol visit(Case caseStatement) {
        List<TypeSymbol> declaredTypes = new ArrayList<>();
        List<TypeSymbol> resolvedTypes = new ArrayList<>();
        CaseSymbol symbol = caseStatement.getSymbol();

        // Visit condition expression and check for returned errors
        TypeSymbol conditionType = caseStatement.getCond().accept(this);
        if (conditionType == null) {
            return null;
        }

        // Visit formals and store types
        for (Formal formal : caseStatement.getFormals()) {
            declaredTypes.add(formal.accept(this));
        }

        // Visit formal's expressions
        for (Expression expression : caseStatement.getThen()) {
            resolvedTypes.add(expression.accept(this));
        }

        // Compute the lowest class that inherits all resolved types
        List<ClassSymbol> classSymbols = new ArrayList<>();
        for (TypeSymbol resolvedType : resolvedTypes) {
            if (resolvedType != null) {
                classSymbols.add((ClassSymbol) globals.lookup(resolvedType.getName()));
            }
        }

        return getCommonSuperclass(classSymbols).getType();
    }

    @Override
    public TypeSymbol visit(Assign assign) {
        // Cannot assign to self
        if (assign.getName().getToken().getText().equals(SELF)) {
            String errorMsg = ErrorMessages.Assignments.illegalAssignToSelf();
            error(assign.getName().getToken(), errorMsg);

            return null;
        }
        // Visit Id to set it's symbol and check if it's undefined
        TypeSymbol assigneeType = assign.getName().accept(this);
        if (assigneeType == null) {
            return null;
        }

        // Visit assigned expression and check for errors
        IdSymbol assigneeSymbol = assign.getName().getSymbol();
        TypeSymbol evaluatedType = assign.getExpr().accept(this);
        if (evaluatedType == null) {
            return null;
        }

        ClassSymbol gotClass = (ClassSymbol) globals.lookup(evaluatedType.getName());
        ClassSymbol expectedClass = (ClassSymbol) globals.lookup(assigneeType.getName());

        // If evaluated type of the assignment doesn't match with the defined type of the variable
        if (!evaluatedType.getName().equals(assigneeType.getName()) && !isSuperclass(gotClass, expectedClass)) {
            String errorMsg = ErrorMessages.Assignments.illegalType(assigneeSymbol.getName(),
                    assigneeType.getName(), evaluatedType.getName());
            error(assign.getExpr().getToken(), errorMsg);

            return null;
        }
        return assigneeType;
    }

    @Override
    public TypeSymbol visit(Arithmetic arithmetic) {
        // Visit operands to resolve their types and check for errors
        TypeSymbol leftType = arithmetic.getLeft().accept(this);
        TypeSymbol rightType = arithmetic.getRight().accept(this);

        String operation = arithmetic.getOperation().getToken().getText();
        String expectingType = BaseTypeSymbolFactory.getINT().getName();

        // If any of the operands isn't of type Int
        if (leftType != null && !leftType.getName().equals(expectingType)) {
            String errorMsg = ErrorMessages.Operators.operandNotInt(operation, leftType.getName());
            error(arithmetic.getLeft().getToken(), errorMsg);

            return null;
        }

        if (rightType != null && !rightType.getName().equals(expectingType)) {
            String errorMsg = ErrorMessages.Operators.operandNotInt(operation, rightType.getName());
            error(arithmetic.getRight().getToken(), errorMsg);

            return null;
        }

        return BaseTypeSymbolFactory.getINT();
    }

    @Override
    public TypeSymbol visit(Relational relational) {
        TypeSymbol leftType = relational.getLeft().accept(this);
        TypeSymbol rightType = relational.getRight().accept(this);
        TypeSymbol intType = BaseTypeSymbolFactory.getINT();
        String currentOperation = relational.getOperation().getToken().getText();
        final List<String> LESS_SYMBOLS = List.of("<", "<=");

        // Different error message and logic for less than symbols
        if (LESS_SYMBOLS.contains(currentOperation)) {
            // If one of the operands isn't of type Int
            if (leftType != null && !leftType.getName().equals(intType.getName())) {
                String errorMsg = ErrorMessages.Operators.operandNotInt("<", leftType.getName());
                error(relational.getLeft().getToken(), errorMsg);

                return null;
            }
            if (rightType != null && !rightType.getName().equals(intType.getName())) {
                String errorMsg = ErrorMessages.Operators.operandNotInt("<", rightType.getName());
                error(relational.getRight().getToken(), errorMsg);

                return null;
            }
        }
        // Different error messages and logic for equals
        else {
            // If no error occurred in any of the operands and both are of different primitive types
            if (leftType != null && rightType != null &&
                BaseTypeSymbolFactory.isPrimitive(leftType.getName()) &&
                BaseTypeSymbolFactory.isPrimitive(rightType.getName()) &&
                !leftType.getName().equals(rightType.getName())) {

                String errorMsg = ErrorMessages.Operators.illegalCompare(leftType.getName(), rightType.getName());
                error(relational.getOperation().getToken(), errorMsg);

                return null;
            }
        }

        // Pretty dumb logic if you ask me
        return BaseTypeSymbolFactory.getBOOL();
    }

    @Override
    public TypeSymbol visit(Negation negation) {
        // Evaluate expression and check for errors
        TypeSymbol resolvedType = negation.getExpr().accept(this);
        if (resolvedType == null) {
            return null;
        }

        // Negation accepts ints exclusively, reject any other type
        if (!resolvedType.getName().equals(BaseTypeSymbolFactory.getINT().getName())) {
            String errorMsg = ErrorMessages.Operators.operandNotInt("~", resolvedType.getName());
            error(negation.getExpr().getToken(), errorMsg);

            return null;
        }

        return BaseTypeSymbolFactory.getINT();
    }

    @Override
    public TypeSymbol visit(Not not) {
        TypeSymbol acceptedType = BaseTypeSymbolFactory.getBOOL();

        // Visit expression and check for errors
        TypeSymbol resolvedType = not.getExpr().accept(this);
        if (resolvedType == null) {
            return null;
        }

        if (!resolvedType.getName().equals(acceptedType.getName())) {
            String errorMsg = ErrorMessages.Operators.operandNotBool("not", resolvedType.getName());
            error(not.getExpr().getToken(), errorMsg);

            return null;
        }

        return acceptedType;
    }

    @Override
    public TypeSymbol visit(Int intt) {
        return BaseTypeSymbolFactory.getINT();
    }

    @Override
    public TypeSymbol visit(CoolString string) {
        if (string.getToken().getText().equals("true") || string.getToken().getText().equals("false")) {
            return BaseTypeSymbolFactory.getBOOL();
        }
        return BaseTypeSymbolFactory.getSTRING();
    }

    @Override
    public TypeSymbol visit(Bool bool) {
        return BaseTypeSymbolFactory.getBOOL();
    }

    @Override
    public TypeSymbol visit(Paren paren) {
        return paren.getExpr().accept(this);
    }

    @Override
    public TypeSymbol visit(New neww) {
        String typeName = neww.getType().getToken().getText();

        if (globals.lookup(typeName) == null) {
            String errorMsg = ErrorMessages.Assignments.undefinedTypeWhenInstancing(typeName);
            error(neww.getType().getToken(), errorMsg);

            return null;
        }

        return new TypeSymbol(neww.getType().getToken().getText());
    }

    @Override
    public TypeSymbol visit(IsVoid isVoid) {
        return BaseTypeSymbolFactory.getBOOL();
    }

    @Override
    public TypeSymbol visit(While whileLoop) {
        // Resolve and check condition for errors
        TypeSymbol condSymbol = whileLoop.getCond().accept(this);
        if (condSymbol == null) {
            return null;
        }

        // Check condition type
        if (!condSymbol.getName().equals(BaseTypeSymbolFactory.getBOOL().getName())) {
            String errorMsg = ErrorMessages.Conditions.illegalWhileCond(condSymbol.getName(), "While");
            error(whileLoop.getCond().getToken(), errorMsg);
        }

        if (whileLoop.getBody().accept(this) == null) {
            return null;
        }

        return BaseTypeSymbolFactory.getOBJECT();
    }

    @Override
    public TypeSymbol visit(If ifStatement) {
        // Resolve and check condition for errors
        TypeSymbol condSymbol = ifStatement.getCond().accept(this);
        if (condSymbol == null) {
            return BaseTypeSymbolFactory.getOBJECT();
        }

        // Check condition type
        if (!condSymbol.getName().equals(BaseTypeSymbolFactory.getBOOL().getName())) {
            String errorMsg = ErrorMessages.Conditions.illegalWhileCond(condSymbol.getName(), "If");
            error(ifStatement.getCond().getToken(), errorMsg);

            return BaseTypeSymbolFactory.getOBJECT();
        }

        TypeSymbol thenBranch = ifStatement.getThen().accept(this);
        TypeSymbol elseBranch = ifStatement.getElseOutcome().accept(this);

        // Visit bodies and check for errors
        if (thenBranch == null || elseBranch == null) {
            return null;
        }

        return thenBranch;
    }

    // TODO: vezi ca de aici isi lua cu muie test 19
    @Override
    public TypeSymbol visit(InstructionBlock instructionBlock) {
        TypeSymbol lastSymbol = BaseTypeSymbolFactory.getOBJECT();
        for (Expression expression : instructionBlock.getBody()) {
            lastSymbol = expression.accept(this);
        }

        return lastSymbol;
    }

    // TODO: todo plm
    @Override
    public TypeSymbol visit(StaticMethodCall staticMethodCall) {
        List<TypeSymbol> resolvedArgTypes = new ArrayList<>();

        // Fetch scope and lookup from the said scope for the method's definition
        Id methodId = staticMethodCall.getMethodName();
        Scope callerScope = methodId.getScope();
        MethodSymbol methodSymbol = (MethodSymbol) callerScope.lookup(methodId.getToken().getText());

        return returnType;
    }

    @Override
    public TypeSymbol visit(MethodCall methodCall) {
        String methodName = methodCall.getMethodName().getToken().getText();
        TypeSymbol callerSymbolType = methodCall.getCaller().accept(this);
        ClassSymbol callerType;

        // Resolve static dispatch
        if (methodCall.getImposedType() != null) {
            String staticDispatch = methodCall.getImposedType().getToken().getText();

            // If static dispatch is of type SELF_TYPE
            if (staticDispatch.equals(SELF_TYPE)) {
                String errorMsg = ErrorMessages.MethodCall.selfTypeStaticDispach();
                error(methodCall.getImposedType().getToken(), errorMsg);

                return null;
            }

            // If static dispatch type is undefined
            callerType = (ClassSymbol) globals.lookup(staticDispatch);
            if (callerType == null) {
                String errorMsg = ErrorMessages.MethodCall.undefinedTypeOfStaticDispatch(staticDispatch);
                error(methodCall.getImposedType().getToken(), errorMsg);

                return null;
            }

            // If static dispatch isn't a subclass of caller
            ClassSymbol idClass = (ClassSymbol) globals.lookup(callerSymbolType.getName());
            if (!isSuperclass(idClass, callerType)) {
                String errorMsg = ErrorMessages.MethodCall.notASuperclassStaticDispatch(callerType.getName(), idClass.getName());
                error(methodCall.getImposedType().getToken(), errorMsg);

                return null;
            }
        } else {
            // No static dispatch, get class symbol of method by the caller
            callerType = (ClassSymbol) globals.lookup(callerSymbolType.getName());
        }

        // Search method in resolved caller's class
        MethodSymbol dispatchedMethodSymbol = getDispatchedMethod(callerType, methodName);
        if (dispatchedMethodSymbol == null) {
            String errorMsg = ErrorMessages.MethodCall.undefinedMethod(callerType.getName(), methodName);
            error(methodCall.getMethodName().getToken(), errorMsg);

            return null;
        }

        // If the number of arguments doesn't match
        int noOfArguments = methodCall.getNoOfArgs();
        if (noOfArguments != dispatchedMethodSymbol.methodDef.getNoOfArgs()) {
            String errorMsg = ErrorMessages.MethodCall.wrongNumberOfArguments(callerType.getName(), methodName);
            error(methodCall.getMethodName().getToken(), errorMsg);

            return dispatchedMethodSymbol.getType();
        }

        for (int i = 0; i < noOfArguments; ++i) {
            Expression expression = methodCall.getArgs().get(i);
            TypeSymbol callerArgType = expression.accept(this);
            TypeSymbol methodDefArgType = dispatchedMethodSymbol.methodDef.getArgs().get(i).getSymbol().getType();

            // Check for superclass types as well
            ClassSymbol callClass = (ClassSymbol) globals.lookup(callerArgType.getName());
            ClassSymbol defClass = (ClassSymbol) globals.lookup(methodDefArgType.getName());

            // If argument type doesn't match
            if (!callerArgType.getName().equals(methodDefArgType.getName()) && !isSuperclass(callClass, defClass)) {
                String errorMsg = ErrorMessages.MethodCall.wrongArgumentType(callerType.getName(), methodName,
                        dispatchedMethodSymbol.methodDef.getArgs().get(i).getName().getToken().getText(),
                        methodDefArgType.getName(), callerArgType.getName());
                error(methodCall.getArgs().get(i).getToken(), errorMsg);
            }
        }

        return dispatchedMethodSymbol.getType();
    }

}
