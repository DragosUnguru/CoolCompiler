package cool.compiler;

import org.antlr.v4.runtime.Token;

public abstract class ASTNode {
    // Descriptive token of the expression
    private Token token;

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }

    ASTNode(Token token) { this.token = token; }

    public <T> T accept(ASTVisitor<T> visitor) {
        return null;
    }
}

