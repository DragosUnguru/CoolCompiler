package cool.compiler;

import org.antlr.v4.runtime.Token;

import java.util.List;

public class Program extends ASTNode {
    private List<CoolClass> classes;

    public List<CoolClass> getClasses() {
        return classes;
    }

    public void setClasses(List<CoolClass> classes) {
        this.classes = classes;
    }

    Program(List<CoolClass> classes, Token token) {
        super(token);
        this.classes = classes;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
