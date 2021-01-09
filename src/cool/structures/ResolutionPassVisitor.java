package cool.structures;

import cool.compiler.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

        // Get actual names of the variable and variable's type
        String varName = varId.getToken().getText();
        String typeName = varDef.getType().getToken().getText();
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

            // If initialization resolved type doesn't match the declared type
            if (!evaluatedTypeName.equals(typeName)) {
                String errorMsg = ErrorMessages.AttributeDefinitions.illegalInitExpr(varName, typeName, evaluatedTypeName);
                error(varDef.getType().getToken(), errorMsg);

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
        for (Expression expression : methodDef.getBody()) {
            lastTypeSymbol = expression.accept(this);
        }

        // The evaluated method's body type is given by the last expression
        if (lastTypeSymbol == null) {
            return null;
        }

        System.out.println("Method " + methodName + " resolved type to: " + lastTypeSymbol.getName());

        // If body type doesn't match the declared type
        if (!methodName.equals("main") && !lastTypeSymbol.getName().equals(symbol.getType().getName())) {
            String errorMsg = ErrorMessages.MethodDefinitions.illegalBodyReturnType(methodName, symbol.getType().getName(), lastTypeSymbol.getName());
            error(methodDef.getReturnType().getToken(), errorMsg);

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
            if (!resolvedTypeName.equals(declaredTypeSymbol.getName())) {
                String errorMsg = ErrorMessages.LetIn.illegalInitExprType(formalName, declaredTypeName, resolvedType.getName());
                error(formal.getType().getToken(), errorMsg);

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

        int noOfFormals = declaredTypes.size();
        for (int i = 0; i < noOfFormals; ++i) {
            TypeSymbol declaredType = declaredTypes.get(i);
            TypeSymbol resolvedType = resolvedTypes.get(i);

            // Final type of the case structure is the resolved type of the expression of the branch
            // that has the formal with the same type as the condition expression
            if (declaredType != null && resolvedType != null && declaredType.getName().equals(conditionType.getName())) {
                symbol.setType(resolvedType);
                return resolvedType;
            }
        }

        return null;
    }

    @Override
    public TypeSymbol visit(Assign assign) {
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

        // If evaluated type of the assignment doesn't match with the defined type of the variable
        if (!evaluatedType.getName().equals(assigneeType.getName())) {
            String errorMsg = ErrorMessages.Assignments.illegalType(assigneeSymbol.getName(),
                    assigneeType.getName(), evaluatedType.getName());
            error(assign.getName().getToken(), errorMsg);

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
}
