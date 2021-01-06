package cool.compiler;

import org.antlr.v4.runtime.Token;

import java.util.List;

public class MethodDef extends Feature {
    private List<Formal> args;
    private Id name;
    private Type returnType;
    private List<Expression> body;

    public List<Formal> getArgs() {
        return args;
    }

    public void setArgs(List<Formal> args) {
        this.args = args;
    }

    public Id getName() {
        return name;
    }

    public void setName(Id name) {
        this.name = name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    public List<Expression> getBody() {
        return body;
    }

    public void setBody(List<Expression> body) {
        this.body = body;
    }

    public int getNoOfArgs() {
        return args.size();
    }

    MethodDef(Token token, List<Formal> args, Id name, Type returnType, List<Expression> body) {
        super(token);
        this.args = args;
        this.name = name;
        this.returnType = returnType;
        this.body = body;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
