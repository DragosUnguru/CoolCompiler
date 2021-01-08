package cool.structures;

import cool.compiler.CoolClass;
import cool.compiler.Program;

import java.util.List;

import static cool.structures.SymbolTable.*;

public class ClassPassVisitor extends BasePassVisitor {
    private static final List<String> ILLEGAL_PARENTS = List.of("String", "Bool", "Int", "SELF_TYPE");

    @Override
    public TypeSymbol visit(Program program) {
        for (var coolClass : program.getClasses()) {
            if (coolClass != null)
                coolClass.accept(this);
        }

        return null;
    }

    @Override
    public TypeSymbol visit(CoolClass coolClass) {
//        // Classes are crossed in the same order they are set as children. Set context accordingly
//        if (!(globalContext.getChild(CLASS_IDX) instanceof TerminalNode)) {
//            context = (ParserRuleContext) globalContext.getChild(CLASS_IDX++);
//        }

        // Get classes and superclasses name, init class symbol
        String className = coolClass.getClassName().getToken().getText();
        ClassSymbol classSymbol = new ClassSymbol(className, null);

        // If class is redefined
        if (!globals.add(classSymbol)) {
            String errorMsg = ErrorMessages.ClassDefinitions.redefined(className);
            error(coolClass.getClassName().getToken(), errorMsg);

            return null;
        }

        // If class inherits a superclass
        if (coolClass.getSuperClass() != null) {
            String superClassName = coolClass.getSuperClass().getToken().getText();

            // If class inherits an illegal class
            if (ILLEGAL_PARENTS.contains(superClassName)) {
                String errorMsg = ErrorMessages.ClassDefinitions.illegalParent(className, superClassName);
                error(coolClass.getSuperClass().getToken(), errorMsg);

                return null;
            }
        }

        // If class has illegal name 'SELF_TYPE'
        if (className.equals(SELF_TYPE)) {
            String errorMsg = ErrorMessages.ClassDefinitions.illegalNameSelfType();
            error(coolClass.getClassName().getToken(), errorMsg);

            return null;
        }

        return null;
    }
}
