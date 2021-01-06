package cool.compiler;

import org.antlr.v4.runtime.Token;

public class While extends Expression {
    private Expression cond;
    private Expression body;

    public Expression getCond() {
        return cond;
    }

    public void setCond(Expression cond) {
        this.cond = cond;
    }

    public Expression getBody() {
        return body;
    }

    public void setBody(Expression body) {
        this.body = body;
    }

    While(Token token, Expression cond, Expression body) {
        super(token);
        this.cond = cond;
        this.body = body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
