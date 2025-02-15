package cool.compiler;

import cool.structures.LetInSymbol;
import org.antlr.v4.runtime.Token;

import java.util.List;

public class LetIn extends Expression {
    private List<InitedFormal> formals;
    private Expression body;
    private LetInSymbol symbol;

    public LetInSymbol getSymbol() {
        return symbol;
    }

    public void setSymbol(LetInSymbol symbol) {
        this.symbol = symbol;
    }

    public List<InitedFormal> getFormals() {
        return formals;
    }

    public void setFormals(List<InitedFormal> formals) {
        this.formals = formals;
    }

    public Expression getBody() {
        return body;
    }

    public void setBody(Expression body) {
        this.body = body;
    }

    LetIn(Token token, List<InitedFormal> formals, Expression body) {
        super(token);
        this.formals = formals;
        this.body = body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
