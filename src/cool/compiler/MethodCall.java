package cool.compiler;

import org.antlr.v4.runtime.Token;

import java.util.List;

public class MethodCall extends Expression {
    private List<Expression> args;
    private Expression caller;
    private Type imposedType;
    private Id methodName;

    public List<Expression> getArgs() {
        return args;
    }

    public void setArgs(List<Expression> args) {
        this.args = args;
    }

    public Expression getCaller() {
        return caller;
    }

    public void setCaller(Expression caller) {
        this.caller = caller;
    }

    public Type getImposedType() {
        return imposedType;
    }

    public void setImposedType(Type imposedType) {
        this.imposedType = imposedType;
    }

    public Id getMethodName() {
        return methodName;
    }

    public void setMethodName(Id methodName) {
        this.methodName = methodName;
    }

    public int getNoOfArgs() {
        return args.size();
    }

    MethodCall(Token token, Expression caller, Type imposedType, Id methodName, List<Expression> args) {
        super(token);
        this.caller = caller;
        this.imposedType = imposedType;
        this.methodName = methodName;
        this.args = args;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
