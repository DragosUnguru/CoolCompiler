package cool.compiler;

import cool.structures.Scope;
import org.antlr.v4.runtime.Token;

/**
 * Any object type
 */
public class Type extends ASTNode {
    private Scope scope;

    public Scope getScope() {
        return scope;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    Type(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
