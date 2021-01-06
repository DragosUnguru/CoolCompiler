package cool.compiler;

import org.antlr.v4.runtime.Token;

public class VarDef extends Feature {
    private Id name;
    private Type type;
    private Expression expr;

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

    public Expression getExpr() {
        return expr;
    }

    public void setExpr(Expression expr) {
        this.expr = expr;
    }

    VarDef(Token token, Id name, Type type, Expression expr) {
        super(token);
        this.name = name;
        this.type = type;
        this.expr = expr;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
