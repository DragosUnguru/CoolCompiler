package cool.compiler;

import org.antlr.v4.runtime.Token;

/**
 * Any expression
 */
public abstract class Expression extends ASTNode {
    Expression(Token token) {
        super(token);
    }
}
