package cool.compiler;

import org.antlr.v4.runtime.Token;

/**
 * What a class can contain
 * Variable and method definitions
 */
public abstract class Feature extends ASTNode {
    Feature(Token token) {
        super(token);
    }
}
