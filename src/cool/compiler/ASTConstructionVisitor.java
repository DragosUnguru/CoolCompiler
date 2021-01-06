package cool.compiler;

import cool.parser.CoolParser;
import cool.parser.CoolParserBaseVisitor;
import org.antlr.v4.runtime.tree.ParseTree;

import java.util.ArrayList;
import java.util.List;

public class ASTConstructionVisitor extends CoolParserBaseVisitor<ASTNode> {

    @Override
    public ASTNode visitProgram(CoolParser.ProgramContext ctx) {
        List<CoolClass> visitedNodes = new ArrayList<>();

        for (ParseTree node : ctx.children) {
            visitedNodes.add((CoolClass) visit(node));
        }

        return new Program(visitedNodes, ctx.getStart());
    }

    @Override
    public ASTNode visitCool_class(CoolParser.Cool_classContext ctx) {
        Type superclassType = null;
        List<Feature> visitedFeatures = new ArrayList<>();

        // Superclass is optional
        if (ctx.superclass != null) {
            superclassType = new Type(ctx.superclass);
        }

        // Visit features
        for (CoolParser.FeatureContext feature : ctx.features) {
            visitedFeatures.add((Feature) (visit(feature)));
        }

        return new CoolClass(ctx.getStart(), new Type(ctx.type), superclassType, visitedFeatures);
    }

    @Override
    public ASTNode visitMethodDef(CoolParser.MethodDefContext ctx) {
        List<Formal> visitedArgs = new ArrayList<>();
        List<Expression> visitedExpr = new ArrayList<>();

        // Visit method arguments
        for (CoolParser.FormalContext formal : ctx.formals) {
            visitedArgs.add((Formal) visit(formal));
        }

        // Visit method's body's expressions
        for (CoolParser.ExprContext expression : ctx.body) {
            visitedExpr.add((Expression) visit(expression));
        }

        return new MethodDef(ctx.getStart(), visitedArgs, new Id(ctx.name), new Type(ctx.returnType), visitedExpr);
    }

    @Override
    public ASTNode visitVarDef(CoolParser.VarDefContext ctx) {
        // Initialization expression is optional
        if (ctx.init != null) {
            return new VarDef(ctx.getStart(), new Id(ctx.name), new Type(ctx.type), (Expression) visit(ctx.init));
        }

        return new VarDef(ctx.getStart(), new Id(ctx.name), new Type(ctx.type), null);
    }

    @Override
    public ASTNode visitFormal(CoolParser.FormalContext ctx) {
        return new Formal(ctx.getStart(), new Id(ctx.name), new Type(ctx.type));
    }

    @Override
    public ASTNode visitInited_formal(CoolParser.Inited_formalContext ctx) {

        if (ctx.initExpr != null) {
            return new InitedFormal(ctx.getStart(), (Formal) visit(ctx.notInitFormal), (Expression) visit(ctx.initExpr));
        }

        return new InitedFormal(ctx.getStart(), (Formal) visit(ctx.notInitFormal), null);
    }

    @Override
    public ASTNode visitMethodCall(CoolParser.MethodCallContext ctx) {
        Type imposedType = null;
        List<Expression> visitedArgs = new ArrayList<>();

        // @<TYPE> is optional
        if (ctx.imposedType != null) {
            imposedType = new Type(ctx.imposedType);
        }

        // Visit arguments
        for (CoolParser.ExprContext expression : ctx.args) {
            visitedArgs.add((Expression) visit(expression));
        }

        return new MethodCall(ctx.getStart(),
                (Expression) visit(ctx.caller),
                imposedType,
                new Id(ctx.methodName),
                visitedArgs);
    }

    @Override
    public ASTNode visitStaticMethodCall(CoolParser.StaticMethodCallContext ctx) {
        List<Expression> visitedArgs = new ArrayList<>();

        // Visit arguments
        for (CoolParser.ExprContext expression : ctx.args) {
            visitedArgs.add((Expression) visit(expression));
        }

        return new StaticMethodCall(ctx.getStart(), new Id(ctx.name), visitedArgs);
    }

    @Override
    public ASTNode visitInstructionBlock(CoolParser.InstructionBlockContext ctx) {
        List<Expression> visitedInstructions = new ArrayList<>();

        // Visit every instruction in the block
        for (CoolParser.ExprContext instruction : ctx.body) {
            visitedInstructions.add((Expression) visit(instruction));
        }

        return new InstructionBlock(ctx.getStart(), visitedInstructions);
    }

    @Override
    public ASTNode visitLetIn(CoolParser.LetInContext ctx) {
        List<InitedFormal> visitedFormals = new ArrayList<>();

        // Visit formals
        for (CoolParser.Inited_formalContext formal : ctx.formals) {
            visitedFormals.add((InitedFormal) visit(formal));
        }

        return new LetIn(ctx.getStart(), visitedFormals, (Expression) visit(ctx.body));
    }

    @Override
    public ASTNode visitIf(CoolParser.IfContext ctx) {
        return new If(ctx.getStart(),
                (Expression) visit(ctx.cond),
                (Expression) visit(ctx.then),
                (Expression) visit(ctx.elseOutcome));
    }

    @Override
    public ASTNode visitCase(CoolParser.CaseContext ctx) {
        List<Formal> visitedFormals = new ArrayList<>();
        List<Expression> visitedThenExprs = new ArrayList<>();

        // Visit formals
        for (CoolParser.FormalContext formal : ctx.formals) {
            visitedFormals.add((Formal) visit(formal));
        }

        // Visit condition outcomes
        for (CoolParser.ExprContext expression : ctx.then) {
            visitedThenExprs.add((Expression) visit(expression));
        }

        return new Case(ctx.getStart(), (Expression) visit(ctx.cond), visitedFormals, visitedThenExprs);
    }

    @Override
    public ASTNode visitWhile(CoolParser.WhileContext ctx) {
        return new While(ctx.getStart(), (Expression) visit(ctx.cond), (Expression) visit(ctx.body));
    }

    @Override
    public ASTNode visitNew(CoolParser.NewContext ctx) {
        return new New(ctx.getStart(), new Type(ctx.type));
    }

    @Override
    public ASTNode visitId(CoolParser.IdContext ctx) {
        return new Id(ctx.getStart());
    }

    @Override
    public ASTNode visitInt(CoolParser.IntContext ctx) {
        return new Int(ctx.getStart());
    }

    @Override
    public ASTNode visitCoolString(CoolParser.CoolStringContext ctx) {
        return new CoolString(ctx.getStart());
    }

    @Override
    public ASTNode visitBool(CoolParser.BoolContext ctx) {
        return new CoolString(ctx.getStart());
    }

    @Override
    public ASTNode visitNegation(CoolParser.NegationContext ctx) {
        return new Negation(ctx.getStart(), (Expression) visit(ctx.e));
    }

    @Override
    public ASTNode visitIsVoid(CoolParser.IsVoidContext ctx) {
        return new IsVoid(ctx.getStart(), (Expression) visit(ctx.e));
    }

    @Override
    public ASTNode visitNot(CoolParser.NotContext ctx) {
        return new Not(ctx.getStart(), (Expression) visit(ctx.e));
    }

    @Override
    public ASTNode visitParen(CoolParser.ParenContext ctx) {
        return new Paren(ctx.getStart(), (Expression) visit(ctx.e));
    }

    @Override
    public ASTNode visitMultDiv(CoolParser.MultDivContext ctx) {
        return new Arithmetic((Expression) visit(ctx.left), (Expression) visit(ctx.right), new Operation(ctx.op), ctx.op);
    }

    @Override
    public ASTNode visitPlusMinus(CoolParser.PlusMinusContext ctx) {
        return new Arithmetic((Expression) visit(ctx.left), (Expression) visit(ctx.right), new Operation(ctx.op), ctx.op);
    }

    @Override
    public ASTNode visitRelational(CoolParser.RelationalContext ctx) {
        return new Relational((Expression) visit(ctx.left), (Expression) visit(ctx.right), new Operation(ctx.op), ctx.op);
    }

    @Override
    public ASTNode visitAssign(CoolParser.AssignContext ctx) {
        return new Assign(ctx.getStart(), new Id(ctx.name), (Expression) visit(ctx.e));
    }
}
