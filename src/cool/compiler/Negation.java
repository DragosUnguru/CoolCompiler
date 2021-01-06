package cool.compiler;

import org.antlr.v4.runtime.Token;

public class Negation extends Expression {
    private Expression expr;

    public Expression getExpr() {
        return expr;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }

    Negation(Token token, Expression e) {
        super(token);
        this.expr = e;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
