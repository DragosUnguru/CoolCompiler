package cool.compiler;

import org.antlr.v4.runtime.Token;

public class InitedFormal extends ASTNode {
    private Formal formal;
    private Expression initExpr;

    public Formal getFormal() {
        return formal;
    }

    public void setFormal(Formal formal) {
        this.formal = formal;
    }

    public Expression getInitExpr() {
        return initExpr;
    }

    public void setInitExpr(Expression initExpr) {
        this.initExpr = initExpr;
    }

    InitedFormal(Token token, Formal formal, Expression initExpr) {
        super(token);
        this.formal = formal;
        this.initExpr = initExpr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
