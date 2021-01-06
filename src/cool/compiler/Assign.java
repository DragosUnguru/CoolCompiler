package cool.compiler;

import org.antlr.v4.runtime.Token;

public class Assign extends Expression {
    private Id name;
    private Expression expr;

    public Id getName() {
        return name;
    }

    public void setName(Id name) {
        this.name = name;
    }

    public Expression getExpr() {
        return expr;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }

    Assign(Token token, Id name, Expression expr) {
        super(token);
        this.name = name;
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
