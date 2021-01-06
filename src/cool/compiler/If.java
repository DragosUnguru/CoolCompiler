package cool.compiler;

import org.antlr.v4.runtime.Token;

public class If extends Expression {
    private Expression cond;
    private Expression then;
    private Expression elseOutcome;

    public Expression getCond() {
        return cond;
    }

    public void setCond(Expression cond) {
        this.cond = cond;
    }

    public Expression getThen() {
        return then;
    }

    public void setThen(Expression then) {
        this.then = then;
    }

    public Expression getElseOutcome() {
        return elseOutcome;
    }

    public void setElseOutcome(Expression elseOutcome) {
        this.elseOutcome = elseOutcome;
    }

    If(Token token, Expression cond, Expression then, Expression elseOutcome) {
        super(token);
        this.cond = cond;
        this.then = then;
        this.elseOutcome = elseOutcome;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
