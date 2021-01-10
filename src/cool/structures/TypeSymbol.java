package cool.structures;

import static cool.structures.BasePassVisitor.SELF_TYPE;

public class TypeSymbol extends Symbol {
    private boolean selfTypeFlag;

    public TypeSymbol(String name) {
        super(name);
        this.selfTypeFlag = false;
    }

    public TypeSymbol(String name, boolean selfTypeFlag) {
        super(name);
        this.selfTypeFlag = selfTypeFlag;
    }

    public boolean isSelfTypeEvaluated() {
        return selfTypeFlag;
    }

    public void setSelfTypeFlag(boolean selfTypeFlag) {
        this.selfTypeFlag = selfTypeFlag;
    }

    @Override
    public String toString() {
        return selfTypeFlag ? SELF_TYPE : name;
    }
}
