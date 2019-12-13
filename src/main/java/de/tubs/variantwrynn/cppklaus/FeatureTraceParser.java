package de.tubs.variantwrynn.cppklaus;

import antlr.cpp.CPPParser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.prop4j.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import de.tubs.variantwrynn.util.fide.NodeUtils;

public class FeatureTraceParser extends antlr.cpp.CPPParserBaseVisitor<Node> {
    private final static String AND = "&&";
    private final static String OR = "||";

    private FeatureTrace currentRoot, current;
    private int ArtefactPos = 0;
    private Map<String, Function<List<Node>, Literal>> functionMacros;

    // We need this to compute the presence condition in else
    private List<Node> currentNegatedConditions;

    private final boolean ignoreEmptyCode = true;

    public FeatureTraceParser() {
        initialize();
        reset(null);
    }

    public void reset(FeatureTrace hierarchy) {
        currentRoot = hierarchy;
        current = currentRoot;
        currentNegatedConditions.clear();
    }

    public void initialize() {
        currentNegatedConditions = new ArrayList<>();
        functionMacros = new HashMap<>();
        functionMacros.put("defined", (nodes) -> {
            assert(nodes.size() == 1);
            return NodeUtils.reference(nodes.get(0));
        });
    }

    private FeatureTrace push() {
        FeatureTrace newTrace = new FeatureTrace();
        current.addChild(newTrace);
        current = newTrace;
        return newTrace;
    }

    private void pop() {
        current = current.getParent();
    }

    @Override
    protected Node aggregateResult(Node aggregate, Node nextResult) {
        if (nextResult != null) {
            if (aggregate != null) {
                return super.aggregateResult(aggregate, nextResult);
                //throw new RuntimeException("Do we need an And here?\naggregate = [" + aggregate + "],\nnextResult = [" + nextResult + "]");
            }

            return nextResult;
        }

        return aggregate;
    }

    private Node handleCondition(ParserRuleContext ctx, Node condition, Node presenceCondition, int childOffset) {
        FeatureTrace myTrace = push();
        myTrace.setFormula(presenceCondition);

        currentNegatedConditions.add(NodeUtils.negate(condition));

        for (int i = childOffset; i < ctx.getChildCount(); ++i) {
            visit(ctx.getChild(i));
        }

        if (current == myTrace) {
            pop();
        }

        return presenceCondition;
    }

    @Override
    public Node visitCondition(CPPParser.ConditionContext ctx) {
        List<Node> outerScopeNegatedConditions = currentNegatedConditions;
        currentNegatedConditions = new ArrayList<>();

        final Node condition = visit(ctx.getChild(0));
        Node result = handleCondition(ctx, condition, condition, 1);

        currentNegatedConditions = outerScopeNegatedConditions;

        return result;
    }

    @Override
    public Node visitElseIfCondition(CPPParser.ElseIfConditionContext ctx) {
        // Pop the trace of the previous condition.
        pop();

        final Node condition = visit(ctx.getChild(0));
        List<Node> presenceCondition = new ArrayList<>(currentNegatedConditions.size() + 1);
        presenceCondition.addAll(currentNegatedConditions);
        presenceCondition.add(condition);
        return handleCondition(ctx, condition, new And(presenceCondition.toArray()), 1);
    }

    @Override
    public Node visitElseCondition(CPPParser.ElseConditionContext ctx) {
        // Pop the trace of the previous condition and push our own.
        pop();
        FeatureTrace myTrace = push();
        myTrace.setFormula(new And(currentNegatedConditions.toArray()));

        // to obtain the text
        for (int i = 0; i < ctx.getChildCount(); ++i) {
            visit(ctx.getChild(i));
        }

        pop();

        return myTrace.getFormula(); // null represents true
    }

    @Override
    public Node visitIfdefMacro(CPPParser.IfdefMacroContext ctx) {
        return NodeUtils.reference(ctx.IDENTIFIER().getText());
    }

    @Override
    public Node visitIfndefMacro(CPPParser.IfndefMacroContext ctx) {
        return NodeUtils.negate(NodeUtils.reference(ctx.IDENTIFIER().getText()));
    }

    @Override
    public Node visitExpression(CPPParser.ExpressionContext ctx) {
        String t = ctx.getText();

        if (ctx.braces() != null) {
            return visitBraces(ctx.braces());
        }

        if (ctx.not() != null) {
            return visitNot(ctx.not());
        }

        if (ctx.left != null) {
            Node left = visitExpression(ctx.left);
            Node right = visitExpression(ctx.right);

            String op = ctx.binaryOperator().getText();
            if (AND.equals(op)) {
                return new And(left, right);
            } else if(OR.equals(op)) {
                return new Or(left, right);
            } else {
                throw new UnsupportedOperationException("Unknown operator \"" + op + "\"");
            }
        }

        return visitLiteral(ctx.literal());
    }

    @Override
    public Node visitNot(CPPParser.NotContext ctx) {
        if (ctx.literal() != null) {
            return NodeUtils.negate(visitLiteral(ctx.literal()));
        }

        if (ctx.braces() != null) {
            return new Not(visitBraces(ctx.braces()));
        }

        return null;
    }

    @Override
    public Literal visitLiteral(CPPParser.LiteralContext ctx) {
        if (ctx.functionCall() != null) {
            return visitFunctionCall(ctx.functionCall());
        }

        return NodeUtils.reference(ctx.IDENTIFIER().getText());
    }

    @Override
    public Literal visitFunctionCall(CPPParser.FunctionCallContext ctx) {
        String fun = ctx.IDENTIFIER().getText();

        if (functionMacros.containsKey(fun)) {
            int numExpressions = ctx.getChildCount() - 3 /* fun ( ) */;
            numExpressions -= (numExpressions - 1) / 2; /* remove commas*/
            List<Node> args = new ArrayList<>(numExpressions);
            for (int i = 0; i < numExpressions; ++i) {
                args.add(visitExpression(ctx.expression(i)));
            }
            return functionMacros.get(fun).apply(args);
        } else {
            throw new UnsupportedOperationException("Unknown function macro \"" + fun + "\"");
        }
    }

    @Override
    public Node visitCodeline(@NotNull CPPParser.CodelineContext ctx) {
        String code = ctx.getText();
        if (!ignoreEmptyCode || !code.replaceAll("[\\r\\n]+\\s*", "").trim().isEmpty()) {
            current.addArtefact(new CPPSPLCodeFragment(ctx.getText(), ArtefactPos));
            ++ArtefactPos;
        }
        return super.visitCodeline(ctx);
    }
}
