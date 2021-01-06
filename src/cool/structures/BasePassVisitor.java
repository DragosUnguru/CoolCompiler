package cool.structures;

import cool.compiler.*;

public abstract class BasePassVisitor implements ASTVisitor<Void> {

    protected final static String SELF = "self";
    protected final static String SELF_TYPE = "SELF_TYPE";

    @Override
    public Void visit(Program program) {
        return null;
    }

    @Override
    public Void visit(Type type) {
        return null;
    }

    @Override
    public Void visit(Operation operation) {
        return null;
    }

    @Override
    public Void visit(Id id) {
        return null;
    }

    @Override
    public Void visit(Assign assign) {
        return null;
    }

    @Override
    public Void visit(MethodCall methodCall) {
        return null;
    }

    @Override
    public Void visit(StaticMethodCall staticMethodCall) {
        return null;
    }

    @Override
    public Void visit(If ifStatement) {
        return null;
    }

    @Override
    public Void visit(While whileLoop) {
        return null;
    }

    @Override
    public Void visit(InstructionBlock instructionBlock) {
        return null;
    }

    @Override
    public Void visit(LetIn letIn) {
        return null;
    }

    @Override
    public Void visit(Case caseStatement) {
        return null;
    }

    @Override
    public Void visit(Int intt) {
        return null;
    }

    @Override
    public Void visit(CoolString string) {
        return null;
    }

    @Override
    public Void visit(Bool bool) {
        return null;
    }

    @Override
    public Void visit(New neww) {
        return null;
    }

    @Override
    public Void visit(Negation negation) {
        return null;
    }

    @Override
    public Void visit(IsVoid isVoid) {
        return null;
    }

    @Override
    public Void visit(Relational relational) {
        return null;
    }

    @Override
    public Void visit(Arithmetic arithmetic) {
        return null;
    }

    @Override
    public Void visit(Not not) {
        return null;
    }

    @Override
    public Void visit(Paren paren) {
        return null;
    }

    @Override
    public Void visit(Formal formal) {
        return null;
    }

    @Override
    public Void visit(InitedFormal initedFormal) {
        return null;
    }

    @Override
    public Void visit(VarDef varDef) {
        return null;
    }

    @Override
    public Void visit(MethodDef methodDef) {
        return null;
    }

    @Override
    public Void visit(CoolClass coolClass) {
        return null;
    }
}
