package cool.compiler;

import org.antlr.v4.runtime.Token;

/**
 * Any object type
 */
public class Type extends ASTNode {

    Type(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
