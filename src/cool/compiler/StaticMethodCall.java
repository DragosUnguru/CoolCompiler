package cool.compiler;

import org.antlr.v4.runtime.Token;

import java.util.List;

public class StaticMethodCall extends Expression {
    private Id methodName;
    private List<Expression> args;

    public Id getMethodName() {
        return methodName;
    }

    public void setMethodName(Id methodName) {
        this.methodName = methodName;
    }

    public List<Expression> getArgs() {
        return args;
    }

    public void setArgs(List<Expression> args) {
        this.args = args;
    }

    StaticMethodCall(Token token, Id methodName, List<Expression> args) {
        super(token);
        this.methodName = methodName;
        this.args = args;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
