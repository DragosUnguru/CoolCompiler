package cool.compiler;

import org.antlr.v4.runtime.Token;

import java.util.List;

public class CoolClass extends ASTNode {
    private Type className;
    private Type superClass;
    private List<Feature> features;

    public Type getClassName() {
        return className;
    }

    public void setClassName(Type className) {
        this.className = className;
    }

    public Type getSuperClass() {
        return superClass;
    }

    public void setSuperClass(Type superClass) {
        this.superClass = superClass;
    }

    public List<Feature> getFeatures() {
        return features;
    }

    public void setFeatures(List<Feature> features) {
        this.features = features;
    }

    CoolClass(Token token, Type className, Type superClass, List<Feature> features) {
        super(token);
        this.className = className;
        this.superClass = superClass;
        this.features = features;
    }

    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visit(this);
    }
}
