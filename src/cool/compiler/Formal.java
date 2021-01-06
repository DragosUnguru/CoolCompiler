package cool.compiler;

import org.antlr.v4.runtime.Token;

/**
 * Any type of formals (initialized or uninitialized)
 * Uninitialized formals will be instantiated with null initExpr
 */
public class Formal extends ASTNode {
    private Id name;
    private Type type;

    public Id getName() {
        return name;
    }

    public void setName(Id name) {
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    Formal(Token token, Id name, Type type) {
        super(token);
        this.name = name;
        this.type = type;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
