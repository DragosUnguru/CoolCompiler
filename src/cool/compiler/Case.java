package cool.compiler;

import org.antlr.v4.runtime.Token;

import java.util.List;

public class Case extends Expression {
    private Expression cond;
    private List<Formal> formals;
    private List<Expression> then;

    public Expression getCond() {
        return cond;
    }

    public void setCond(Expression cond) {
        this.cond = cond;
    }

    public List<Formal> getFormals() {
        return formals;
    }

    public void setFormals(List<Formal> formals) {
        this.formals = formals;
    }

    public List<Expression> getThen() {
        return then;
    }

    public void setThen(List<Expression> then) {
        this.then = then;
    }

    Case(Token token, Expression cond, List<Formal> formals, List<Expression> then) {
        super(token);
        this.cond = cond;
        this.formals = formals;
        this.then = then;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
