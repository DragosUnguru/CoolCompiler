package cool.compiler;

import org.antlr.v4.runtime.Token;

public class Arithmetic extends Expression {
    private Expression left;
    private Expression right;
    private Operation operation;

    public Expression getLeft() {
        return left;
    }

    public void setLeft(Expression left) {
        this.left = left;
    }

    public Expression getRight() {
        return right;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public Operation getOperation() {
        return operation;
    }

    public void setOperation(Operation operation) {
        this.operation = operation;
    }

    Arithmetic(Expression left, Expression right, Operation operation, Token token) {
        super(token);
        this.left = left;
        this.right = right;
        this.operation = operation;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
