package cool.compiler;

import org.antlr.v4.runtime.Token;

/**
 * Any type of arithmetic or relational operation
 */
public class Operation extends ASTNode {
    Operation(Token token) {
        super(token);
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
