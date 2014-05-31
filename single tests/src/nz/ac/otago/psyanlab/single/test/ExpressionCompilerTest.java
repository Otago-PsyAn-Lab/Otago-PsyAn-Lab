
package nz.ac.otago.psyanlab.single.test;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.OperandDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.util.OperandCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.expression.Lexer;
import nz.ac.otago.psyanlab.common.expression.OpalExpressionParser;
import nz.ac.otago.psyanlab.common.expression.ParseException;
import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.PrintHierarchyVisitor;
import nz.ac.otago.psyanlab.common.expression.PrintVisitor;
import nz.ac.otago.psyanlab.common.expression.RefineTypeVisitor;
import nz.ac.otago.psyanlab.common.expression.RefineTypeVisitor.TypeException;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.ExpressionVisitor;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.util.Type;

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.util.Log;
import android.widget.SpinnerAdapter;

import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

public class ExpressionCompilerTest extends TestCase {
    public static Expression parse(String source) {
        Lexer lexer = new Lexer(source);
        Parser parser = new OpalExpressionParser(lexer);
        return parser.parseExpression();
    }

    private OperandCallbacks mOperandCallbacks;

    protected LongSparseArray<Operand> mOperands;

    public final void testExpression() {
        mOperands = new LongSparseArray<Operand>();
        Log.d("TEST SECTION", "Unary precedence");
        test("! a", "!a", "(!a)", "!a", Type.TYPE_BOOLEAN);
        test("- + a", "-+a", "(-(+a))", "-+a", Type.TYPE_NUMBER);

        Log.d("TEST SECTION", "Unary and binary precedence");
        test("-a*b", "-a * b", "((-a) * b)", "-a * b", Type.TYPE_NUMBER);
        test("a*-b", "a * -b", "(a * (-b))", "a * -b", Type.TYPE_NUMBER);
        test("!a and b", "!a and b", "((!a) and b)", "!a and b", Type.TYPE_BOOLEAN);
        test("!a or b and c", "!a or b and c", "((!a) or (b and c))", "!a or b and c",
                Type.TYPE_BOOLEAN);
        test("a and !b", "a and !b", "(a and (!b))", "a and !b", Type.TYPE_BOOLEAN);
        test("a and !b or c", "a and !b or c", "((a and (!b)) or c)", "a and !b or c",
                Type.TYPE_BOOLEAN);

        Log.d("TEST SECTION", "Binary precedence");
        test("b+c*d^e-f/g", "b + c * d ^ e - f / g", "((b + (c * (d ^ e))) - (f / g))",
                "b + c * d ^ e - f / g", Type.TYPE_NUMBER);

        Log.d("TEST SECTION", "Binary associativity");
        test("a+b-c", "a + b - c", "((a + b) - c)", "a + b - c", Type.TYPE_NUMBER);
        test("a*b/c", "a * b / c", "((a * b) / c)", "a * b / c", Type.TYPE_NUMBER);
        test("a^b^c", "a ^ b ^ c", "(a ^ (b ^ c))", "a ^ b ^ c", Type.TYPE_NUMBER);

        Log.d("TEST SECTION", "Conditional operator");
        test("a?b:c?d:e", "a ? b : c ? d : e", "(a ? b : (c ? d : e))", "a ? b : c ? d : e",
                Type.TYPE_BOOLEAN);
        test("a ? b ? c : d : e", "a ? b ? c : d : e", "(a ? (b ? c : d) : e)",
                "a ? b ? c : d : e", Type.TYPE_BOOLEAN);
        test("a and b ? c * d : e / f", "a and b ? c * d : e / f",
                "((a and b) ? (c * d) : (e / f))", "a and b ? c * d : e / f", Type.TYPE_NUMBER);

        Log.d("TEST SECTION", "Grouping");
        test("a + (b + c) + d", "a + (b + c) + d", "((a + (b + c)) + d)", "a + (b + c) + d",
                Type.TYPE_NUMBER);
        test("a ^ (b + c)", "a ^ (b + c)", "(a ^ (b + c))", "a ^ (b + c)", Type.TYPE_NUMBER);
        test("a ^ (d ? b + c: a + b)", "a ^ (d ? b + c : a + b)", "(a ^ (d ? (b + c) : (a + b)))",
                "a ^ (d ? b + c : a + b)", Type.TYPE_NUMBER);

        Log.d("TEST SECTION", "Literal detection and coercion");
        test("1", "1", "1", "1", Type.TYPE_NUMBER);
        test("1", "1", "1", "1", Type.TYPE_INTEGER);
        test("1", "1", "1", "1", Type.TYPE_FLOAT);
        test("1.1", "1.1", "1.1", "1.1", Type.TYPE_NUMBER);
        test("1.1", "1.1", "1.1", "1.1", Type.TYPE_FLOAT);
        test("\"a string\"", "\"a string\"", "\"a string\"", "\"a string\"", Type.TYPE_STRING);

        Log.d("TEST SECTION", "Substring");
        test("\"my \" + \"a string\"[2,8]", "\"my \" + \"a string\"[2, 8]",
                "(\"my \" + (\"a string\"[2, 8]))", "\"my \" + \"a string\"[2, 8]",
                Type.TYPE_STRING);

        Log.d("TEST SECTION", "Type assertion");
        test("1 + a", "1 + a", "(1 + a)", "1 + a", Type.TYPE_NUMBER);
        test("1 + a", "1 + a", "(1 + a)", "1 + a", Type.TYPE_INTEGER);
        test("1 + a", "1 + a", "(1 + a)", "1 + a", Type.TYPE_FLOAT);

        Log.d("TEST SECTION", "Concatenation operator");
        test("\"a string\" + \"another string\"", "\"a string\" + \"another string\"",
                "(\"a string\" + \"another string\")", "\"a string\" + \"another string\"",
                Type.TYPE_STRING);

        Log.d("TEST SECTION", "Chained inequalities (virtual boolean operators)");
        test("a=b=c", "a = b = c", "((a = b) and (b = c))", "a = b = c", Type.TYPE_BOOLEAN);
        test("1 <= a >= c", "1 <= a >= c", "((1 <= a) and (a >= c))", "1 <= a >= c",
                Type.TYPE_BOOLEAN);
    }

    public final void testIncompleteParseDetection() {
        String text = "a + b c asd  aadsf adfad";
        Lexer lexer = new Lexer(text);
        Parser parser = new OpalExpressionParser(lexer);
        Expression e = parser.parseExpression();
        PrintVisitor print = new PrintVisitor();
        e.accept(print);
        if (parser.areUnparsedTokens()) {
            Log.d("TEST", "EXPRESSION: " + text + "  INCOMPLETE DETECTION: " + print.toString()
                    + " «" + parser.getLastUnparsed() + "» " + lexer.getTextRemainder());
            Log.d("TEST", "ERROR MESSAGE: Expected operator.");
        }
        assertEquals(true, parser.areUnparsedTokens());

        text = "a + b * 1.4";
        lexer = new Lexer(text);
        parser = new OpalExpressionParser(lexer);
        e = parser.parseExpression();
        print = new PrintVisitor();
        e.accept(print);
        Log.d("TEST", "EXPRESSION: " + text + "  INCOMPLETE DETECTION: " + print.toString());
        assertEquals(false, parser.areUnparsedTokens());
    }

    public final void testInputScenario() {
        testInput("(");
        testInput("(1");
        testInput("(1)");
        testInput("(1)+");
        testInput("(1)+2");
        testInput("(1)+2)");
        testInput("((1)+2)");
    }

    private void test(String expression, String print, String hierarchy, String type,
            int expressionType) {
        Log.d("TEST", "EXPRESSION: " + expression);
        testHierarchy(expression, hierarchy);
        testPrint(expression, print);
        testTypeDetection(expression, expressionType);
        testType(expression, type, expressionType);
    }

    private void testHierarchy(String expression, String printOutput) {
        PrintHierarchyVisitor printHierarchyVisitor = new PrintHierarchyVisitor();

        Expression e = parse(expression);
        e.accept(printHierarchyVisitor);
        Log.d("TEST",
                "EXPRESSION: " + expression + "  HIERARCHY: " + printHierarchyVisitor.toString());
        assertEquals(printOutput, printHierarchyVisitor.toString());
    }

    private void testInput(String text) {
        Lexer lexer = new Lexer(text);
        Parser parser = new OpalExpressionParser(lexer);
        ExpressionVisitor v = new PrintVisitor();
        Log.d("TEST", "EXPRESSION :" + text);
        try {
            Expression e = parser.parseExpression();
            e.accept(v);
            Log.d("TEST", "OUTPUT: " + v.toString());
        } catch (ParseException e) {
            Log.d("TEST", "ERROR: " + e.getMessage());
        }
    }

    private void testPrint(String expression, String printOutput) {
        PrintVisitor printVisitor = new PrintVisitor();

        Expression e = parse(expression);
        e.accept(printVisitor);
        Log.d("TEST", "EXPRESSION: " + expression + "  PRINT: " + printVisitor.toString());
        assertEquals(printOutput, printVisitor.toString());
    }

    private void testType(String expression, String printOutput, int expressionType) {
        ContextlessRefineTypeVisitor refineTypeVisitor = new ContextlessRefineTypeVisitor(null,
                mOperandCallbacks, new HashMap<String, Long>(), expressionType);
        Expression e = parse(expression);
        boolean wasError = false;
        try {
            e.accept(refineTypeVisitor);
        } catch (TypeException error) {
            wasError = true;
        }
        PrintErrorVisitor printErrorVisitor = new PrintErrorVisitor(refineTypeVisitor.getError());
        e.accept(printErrorVisitor);
        Log.d("TEST", "EXPRESSION: " + expression + "  TYPE: " + refineTypeVisitor.toString());
        if (wasError) {
            Log.d("TEST", "ERROR MESSAGE: " + printErrorVisitor.getErrorMessage());
        }
        assertEquals(printOutput, printErrorVisitor.toString());
        assertEquals(true, (refineTypeVisitor.getType() & expressionType) != 0);
    }

    private void testTypeDetection(String expression, int expectedType) {
        ContextlessRefineTypeVisitor refineTypeVisitor = new ContextlessRefineTypeVisitor(null,
                mOperandCallbacks, new HashMap<String, Long>(), Type.TYPE_ANY);
        Expression e = parse(expression);
        try {
            e.accept(refineTypeVisitor);
        } catch (TypeException error) {
        }
        PrintErrorVisitor printErrorVisitor = new PrintErrorVisitor(refineTypeVisitor.getError());
        e.accept(printErrorVisitor);
        Log.d("TEST",
                "EXPRESSION: " + expression + "  TYPE FROM ANY: " + refineTypeVisitor.toString());
        assertEquals(true, (refineTypeVisitor.getType() & expectedType) != 0);
    }

    protected Long findUnusedKey(LongSparseArray<?> map) {
        Long currKey = 0l;
        while (true) {
            if (map.indexOfKey(currKey) < 0) {
                break;
            }
            currKey++;
        }
        return currKey;
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mOperandCallbacks = new OperandCallbacksImplementation();
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public class ContextlessRefineTypeVisitor extends RefineTypeVisitor {
        public ContextlessRefineTypeVisitor(Context context, OperandCallbacks callbacks,
                HashMap<String, Long> operandMap, int rootType) {
            super(context, callbacks, operandMap, rootType);
        }

        @Override
        protected String formatTypeError(List<String> expected, List<String> got) {
            return "Expected type [" + TextUtils.join(", ", expected) + "], but got type ["
                    + TextUtils.join(", ", got) + "].";
        }
    }

    private final class OperandCallbacksImplementation implements OperandCallbacks {
        @Override
        public void addOperandDataChangeListener(OperandDataChangeListener listener) {
        }

        @Override
        public long addOperand(Operand operand) {
            long id = findUnusedKey(mOperands);
            mOperands.put(id, operand);
            return id;
        }

        @Override
        public void deleteOperand(long id) {
            mOperands.remove(id);
        }

        @Override
        public void discardOperandAdapter(ProgramComponentAdapter<Operand> adapter) {
        }

        @Override
        public SpinnerAdapter getMethodsAdapter(Class<?> clazz, int returnTypes) {
            return null;
        }

        @Override
        public Operand getOperand(long id) {
            return mOperands.get(id);
        }

        @Override
        public ProgramComponentAdapter<Operand> getOperandAdapter(long scopeId) {
            return null;
        }

        @Override
        public HashMap<Long, Operand> getOperands() {
            return null;
        }

        @Override
        public void removeOperandDataChangeListener(OperandDataChangeListener listener) {
        }

        @Override
        public void putOperand(long id, Operand operand) {
            mOperands.put(id, operand);
        }
    }
}
