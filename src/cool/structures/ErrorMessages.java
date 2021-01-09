package cool.structures;

public final class ErrorMessages {
    private ErrorMessages() { }

    public static String illegalNameSelf(Scope currentScope) {
        if (currentScope instanceof MethodSymbol) {
            // Current scope is the method, parent scope is the class. Extract names
            String methodName = ((MethodSymbol) currentScope).getName();
            String className = ((ClassSymbol) currentScope.getParent()).getName();

            return ErrorMessages.MethodArguments.illegalNameSelf(className, methodName);
        }
        else if (currentScope instanceof LetInSymbol) {
            return ErrorMessages.LetIn.illegalNameSelf();
        }
        // Else it's instance of CaseSymbol
        return ErrorMessages.Case.illegalNameSelf();
    }

    public static String undefinedType(Scope currentScope, String formalName, String typeName) {
        if (currentScope instanceof MethodSymbol) {
            // Current scope is the method, parent scope is the class. Extract names
            String methodName = ((MethodSymbol) currentScope).getName();
            String className = ((ClassSymbol) currentScope.getParent()).getName();

            return ErrorMessages.MethodArguments.undefined(className, methodName, formalName, typeName);
        }
        else if (currentScope instanceof LetInSymbol) {
            return ErrorMessages.LetIn.undefinedVarType(formalName, typeName);
        }
        // Else it's instance of CaseSymbol
        return ErrorMessages.Case.undefinedVarType(formalName, typeName);
    }

    public static String illegalTypeSelfType(Scope currentScope, String formalName) {
        if (currentScope instanceof MethodSymbol) {
            // Current scope is the method, parent scope is the class. Extract names
            String methodName = ((MethodSymbol) currentScope).getName();
            String className = ((ClassSymbol) currentScope.getParent()).getName();

            return ErrorMessages.MethodArguments.illegalTypeSelfType(className, methodName, formalName);
        }
        // Else it's instance of CaseSymbol
        return ErrorMessages.Case.illegalTypeSelfType(formalName);
    }

    public static final class ClassDefinitions {
        public static String illegalNameSelfType() {
            return "Class has illegal name SELF_TYPE";
        }

        public static String redefined(String className) {
            return "Class " + className + " is redefined";
        }

        public static String illegalParent(String className, String parentName) {
            return "Class " + className + " has illegal parent " + parentName;
        }

        public static String undefinedParent(String className, String parentName) {
            return "Class " + className + " has undefined parent " + parentName;
        }

        public static String inheritanceCycle(String className) {
            return "Inheritance cycle for class " + className;
        }

        private ClassDefinitions() { }
    }

    public static final class AttributeDefinitions {
        public static String illegalNameSelf(String className) {
            return "Class " + className + " has attribute with illegal name self";
        }

        public static String redefined(String className, String attributeName) {
            return "Class " + className + " redefines attribute " + attributeName;
        }

        public static String redefinesInherited(String className, String attributeName) {
            return "Class " + className + " redefines inherited attribute " + attributeName;
        }

        public static String undefinedType(String className, String attributeName, String typeName) {
            return "Class " + className + " has attribute " + attributeName + " with undefined type " + typeName;
        }

        public static String illegalInitExpr(String attributeName, String expectingType, String gotType) {
            return "Type " + gotType + " of initialization expression of attribute " + attributeName +
                    " is incompatible with declared type " + expectingType;
        }

        private AttributeDefinitions() { }
    }

    public static final class MethodDefinitions {
        public static String redefined(String className, String methodName) {
            return "Class " + className + " redefines method " + methodName;
        }

        public static String undefinedReturnType(String className, String methodName, String returnType) {
            return "Class " + className + " has method " + methodName + " with undefined return type " + returnType;
        }

        public static String overrideDifferentArgNumber(String className, String methodName) {
            return "Class " + className + " overrides method " + methodName + " with different number of formal parameters";
        }

        public static String overrideDifferentArgType(String className, String methodName, String argName, String expectingType, String gotType) {
            return "Class " + className + " overrides method " + methodName + " but changes type of formal parameter " +
                    argName + " from " + expectingType + " to " + gotType;
        }

        public static String overrideDifferentReturnType(String className, String methodName, String expectingType, String gotType) {
            return "Class " + className + " overrides method " + methodName + " but changes return type from " +
                    expectingType + " to " + gotType;
        }

        public static String illegalBodyReturnType(String methodName, String declaredType, String bodyType) {
            return "Type " + bodyType + " of the body of method " + methodName + " is incompatible with declared " +
                    "return type " + declaredType;
        }

        private MethodDefinitions() { }
    }

    public static final class MethodArguments {
        public static String illegalNameSelf(String className, String methodName) {
            return "Method " + methodName + " of class " + className + " has formal parameter with illegal name self";
        }

        public static String redefined(String className, String methodName, String argName) {
            return "Method " + methodName + " of class " + className + " redefines formal parameter " + argName;
        }

        public static String illegalTypeSelfType(String className, String methodName, String argName) {
            return "Method " + methodName + " of class " + className + " has formal parameter " + argName +
                    " with illegal type SELF_TYPE";
        }

        public static String undefined(String className, String methodName, String argName, String typeName) {
            return "Method " + methodName + " of class " + className + " has formal parameter " + argName +
                    " with undefined type " + typeName;
        }

        private MethodArguments() { }
    }

    public static final class LetIn {
        public static String illegalNameSelf() {
            return "Let variable has illegal name self";
        }

        public static String undefinedVarType(String varName, String typeName) {
            return "Let variable " + varName + " has undefined type " + typeName;
        }

        public static String illegalInitExprType(String varName, String declaredType, String gotType) {
            return "Type " + gotType + " of initialization expression of identifier " + varName +
                    " is incompatible with declared type " + declaredType;
        }

        private LetIn() { }
    }

    public static final class Case {
        public static String illegalNameSelf() {
            return "Case variable has illegal name self";
        }

        public static String illegalTypeSelfType(String varName) {
            return "Case variable " + varName + " has illegal type SELF_TYPE";
        }

        public static String undefinedVarType(String varName, String typeName) {
            return "Case variable " + varName + " has undefined type " + typeName;
        }

        private Case() { }
    }

    public static final class Conditions {
        public static String illegalWhileCond(String typeName, String structureName) {
            return structureName + " condition has type " + typeName + " instead of Bool";
        }

        private Conditions() { }
    }

    public static final class Variables {
        public static String undefined(String varName) {
            return "Undefined identifier " + varName;
        }

        private Variables() { }
    }

    public static final class Operators {
        public static String operandNotInt(String operation, String foundType) {
            return "Operand of " + operation + " has type " + foundType + " instead of Int";
        }

        public static String operandNotBool(String operation, String foundType) {
            return "Operand of " + operation + " has type " + foundType + " instead of Bool";
        }

        public static String illegalCompare(String operandType1, String operandType2) {
            return "Cannot compare " + operandType1 + " with " + operandType2;
        }

        public static String illegalNotOperation(String givenType) {
            return "Operand of not has type " + givenType + " instead of Bool";
        }

        private Operators() { }
    }

    public static final class Assignments {
        public static String illegalAssignToSelf() {
            return "Cannot assign to self";
        }

        public static String illegalType(String varName, String declaredType, String assignedType) {
            return "Type " + assignedType + " of assigned expression is incompatible with declared type " +
                    declaredType + " of identifier " + varName;
        }

        public static String undefinedTypeWhenInstancing(String typeName) {
            return "new is used with undefined type " + typeName;
        }

        private Assignments() { }
    }
}
