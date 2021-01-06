package cool.compiler;

import org.antlr.v4.runtime.Token;

import java.util.List;

public class InstructionBlock extends Expression {
    private List<Expression> body;

    public List<Expression> getBody() {
        return body;
    }

    public void setBody(List<Expression> body) {
        this.body = body;
    }

    InstructionBlock(Token token, List<Expression> body) {
        super(token);
        this.body = body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
