package cool.structures;

import cool.compiler.*;

public abstract class BasePassVisitor implements ASTVisitor<TypeSymbol> {

    protected final static String SELF = "self";
    protected final static String SELF_TYPE = "SELF_TYPE";

    @Override
    public TypeSymbol visit(Program program) {
        return null;
    }

    @Override
    public TypeSymbol visit(Type type) {
        return null;
    }

    @Override
    public TypeSymbol visit(Operation operation) {
        return null;
    }

    @Override
    public TypeSymbol visit(Id id) {
        return null;
    }

    @Override
    public TypeSymbol visit(Assign assign) {
        return null;
    }

    @Override
    public TypeSymbol visit(MethodCall methodCall) {
        return null;
    }

    @Override
    public TypeSymbol visit(StaticMethodCall staticMethodCall) {
        return null;
    }

    @Override
    public TypeSymbol visit(If ifStatement) {
        return null;
    }

    @Override
    public TypeSymbol visit(While whileLoop) {
        return null;
    }

    @Override
    public TypeSymbol visit(InstructionBlock instructionBlock) {
        return null;
    }

    @Override
    public TypeSymbol visit(LetIn letIn) {
        return null;
    }

    @Override
    public TypeSymbol visit(Case caseStatement) {
        return null;
    }

    @Override
    public TypeSymbol visit(Int intt) {
        return null;
    }

    @Override
    public TypeSymbol visit(CoolString string) {
        return null;
    }

    @Override
    public TypeSymbol visit(Bool bool) {
        return null;
    }

    @Override
    public TypeSymbol visit(New neww) {
        return null;
    }

    @Override
    public TypeSymbol visit(Negation negation) {
        return null;
    }

    @Override
    public TypeSymbol visit(IsVoid IsVoid) {
        return null;
    }

    @Override
    public TypeSymbol visit(Relational relational) {
        return null;
    }

    @Override
    public TypeSymbol visit(Arithmetic arithmetic) {
        return null;
    }

    @Override
    public TypeSymbol visit(Not not) {
        return null;
    }

    @Override
    public TypeSymbol visit(Paren paren) {
        return null;
    }

    @Override
    public TypeSymbol visit(Formal formal) {
        return null;
    }

    @Override
    public TypeSymbol visit(InitedFormal initedFormal) {
        return null;
    }

    @Override
    public TypeSymbol visit(VarDef varDef) {
        return null;
    }

    @Override
    public TypeSymbol visit(MethodDef methodDef) {
        return null;
    }

    @Override
    public TypeSymbol visit(CoolClass coolClass) {
        return null;
    }
}
