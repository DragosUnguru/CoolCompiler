package cool.compiler;

public interface ASTVisitor<T> {
    T visit(Program program);
    T visit(Type type);
    T visit(Operation operation);
    T visit(Id id);
    T visit(Assign assign);
    T visit(MethodCall methodCall);
    T visit(StaticMethodCall staticMethodCall);
    T visit(If ifStatement);
    T visit(While whileLoop);
    T visit(InstructionBlock instructionBlock);
    T visit(LetIn letIn);
    T visit(Case caseStatement);
    T visit(Int intt);
    T visit(CoolString string);
    T visit(Bool bool);
    T visit(New neww);
    T visit(Negation negation);
    T visit(IsVoid isVoid);
    T visit(Relational relational);
    T visit(Arithmetic arithmetic);
    T visit(Not not);
    T visit(Paren paren);
    T visit(Formal formal);
    T visit(InitedFormal initedFormal);
    T visit(VarDef varDef);
    T visit(MethodDef methodDef);
    T visit(CoolClass coolClass);
}
