
package nz.ac.otago.psyanlab.single.test;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.OperandDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.ProgramComponentAdapter;
import nz.ac.otago.psyanlab.common.designer.util.OperandCallbacks;
import nz.ac.otago.psyanlab.common.expression.Lexer;
import nz.ac.otago.psyanlab.common.expression.OpalExpressionParser;
import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.PrintHierarchyVisitor;
import nz.ac.otago.psyanlab.common.expression.PrintVisitor;
import nz.ac.otago.psyanlab.common.expression.RefineTypeVisitor;
import nz.ac.otago.psyanlab.common.expression.RefineTypeVisitor.TypeError;
import nz.ac.otago.psyanlab.common.expression.RefineTypeVisitor.TypeException;
import nz.ac.otago.psyanlab.common.expression.expressions.ConditionalExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.FloatExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.InfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.IntegerExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.NameExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PostfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PrefixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.StringExpression;
import nz.ac.otago.psyanlab.common.model.Operand;

import android.content.Context;
import android.support.v4.util.LongSparseArray;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
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
        test("! a", "!a", "(!a)", "!a", Operand.TYPE_BOOLEAN);
        test("- + a", "-+a", "(-(+a))", "-+a", Operand.TYPE_NUMBER);

        Log.d("TEST SECTION", "Unary and binary precedence");
        test("-a*b", "-a * b", "((-a) * b)", "-a * b", Operand.TYPE_NUMBER);
        test("a*-b", "a * -b", "(a * (-b))", "a * -b", Operand.TYPE_NUMBER);
        test("!a and b", "!a and b", "((!a) and b)", "!a and b", Operand.TYPE_BOOLEAN);
        test("!a or b and c", "!a or b and c", "((!a) or (b and c))", "!a or b and c",
                Operand.TYPE_BOOLEAN);
        test("a and !b", "a and !b", "(a and (!b))", "a and !b", Operand.TYPE_BOOLEAN);
        test("a and !b or c", "a and !b or c", "((a and (!b)) or c)", "a and !b or c",
                Operand.TYPE_BOOLEAN);

        Log.d("TEST SECTION", "Binary precedence");
        test("b+c*d^e-f/g", "b + c * d ^ e - f / g", "((b + (c * (d ^ e))) - (f / g))",
                "b + c * d ^ e - f / g", Operand.TYPE_NUMBER);

        Log.d("TEST SECTION", "Binary associativity");
        test("a+b-c", "a + b - c", "((a + b) - c)", "a + b - c", Operand.TYPE_NUMBER);
        test("a*b/c", "a * b / c", "((a * b) / c)", "a * b / c", Operand.TYPE_NUMBER);
        test("a^b^c", "a ^ b ^ c", "(a ^ (b ^ c))", "a ^ b ^ c", Operand.TYPE_NUMBER);

        Log.d("TEST SECTION", "Conditional operator");
        test("a?b:c?d:e", "a ? b : c ? d : e", "(a ? b : (c ? d : e))", "a ? b : c ? d : e",
                Operand.TYPE_BOOLEAN);
        test("a ? b ? c : d : e", "a ? b ? c : d : e", "(a ? (b ? c : d) : e)",
                "a ? b ? c : d : e", Operand.TYPE_BOOLEAN);
        test("a and b ? c * d : e / f", "a and b ? c * d : e / f",
                "((a and b) ? (c * d) : (e / f))", "a and b ? c * d : e / f", Operand.TYPE_NUMBER);

        Log.d("TEST SECTION", "Grouping");
        test("a + (b + c) + d", "a + (b + c) + d", "((a + (b + c)) + d)", "a + (b + c) + d",
                Operand.TYPE_NUMBER);
        test("a ^ (b + c)", "a ^ (b + c)", "(a ^ (b + c))", "a ^ (b + c)", Operand.TYPE_NUMBER);
        test("a ^ (d ? b + c: a + b)", "a ^ (d ? b + c : a + b)", "(a ^ (d ? (b + c) : (a + b)))",
                "a ^ (d ? b + c : a + b)", Operand.TYPE_NUMBER);

        Log.d("TEST SECTION", "Literal detection and coercion");
        test("1", "1", "1", "1", Operand.TYPE_NUMBER);
        test("1", "1", "1", "1", Operand.TYPE_INTEGER);
        test("1", "1", "1", "1", Operand.TYPE_FLOAT);
        test("1.1", "1.1", "1.1", "1.1", Operand.TYPE_NUMBER);
        test("1.1", "1.1", "1.1", "1.1", Operand.TYPE_FLOAT);
        test("\"a string\"", "\"a string\"", "\"a string\"", "\"a string\"", Operand.TYPE_STRING);

        Log.d("TEST SECTION", "Substring");
        test("\"my \" + \"a string\"[2,8]", "\"my \" + \"a string\"[2, 8]",
                "(\"my \" + (\"a string\"[2, 8]))", "\"my \" + \"a string\"[2, 8]",
                Operand.TYPE_STRING);

        Log.d("TEST SECTION", "Type assertion");
        test("1 + a", "1 + a", "(1 + a)", "1 + a", Operand.TYPE_NUMBER);
        test("1 + a", "1 + a", "(1 + a)", "1 + a", Operand.TYPE_INTEGER);
        test("1 + a", "1 + a", "(1 + a)", "1 + a", Operand.TYPE_FLOAT);

        Log.d("TEST SECTION", "Concatenation operator");
        test("\"a string\" + \"another string\"", "\"a string\" + \"another string\"",
                "(\"a string\" + \"another string\")", "\"a string\" + \"another string\"",
                Operand.TYPE_STRING);

        Log.d("TEST SECTION", "Chained inequalities (virtual boolean operators)");
        test("a=b=c", "a = b = c", "((a = b) and (b = c))", "a = b = c", Operand.TYPE_BOOLEAN);
        test("1 <= a >= c", "1 <= a >= c", "((1 <= a) and (a >= c))", "1 <= a >= c",
                Operand.TYPE_BOOLEAN);
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
                mOperandCallbacks, new HashMap<String, Long>(), Operand.TYPE_ANY);
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

        @Override
        protected List<String> typeToStringArray(int type) {
            ArrayList<String> types = new ArrayList<String>();
            if ((type & Operand.TYPE_BOOLEAN) != 0) {
                types.add("boolean");
            }
            if ((type & Operand.TYPE_FLOAT) != 0) {
                types.add("float");
            }
            if ((type & Operand.TYPE_IMAGE) != 0) {
                types.add("image");
            }
            if ((type & Operand.TYPE_INTEGER) != 0) {
                types.add("integer");
            }
            if ((type & Operand.TYPE_SOUND) != 0) {
                types.add("sound");
            }
            if ((type & Operand.TYPE_STRING) != 0) {
                types.add("string");
            }
            if ((type & Operand.TYPE_VIDEO) != 0) {
                types.add("video");
            }

            return types;
        }
    }

    public class PrintErrorVisitor extends PrintVisitor {
        private TypeError mError;

        public PrintErrorVisitor(TypeError error) {
            mError = error;
        }

        public String getErrorMessage() {
            if (mError == null) {
                return "";
            }
            return mError.getErrorMessage();
        }

        @Override
        public String toString() {
            return super.toString();
        }

        @Override
        public void visit(ConditionalExpression expression) {
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("«");
            }
            super.visit(expression);
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("»");
            }
        }

        @Override
        public void visit(FloatExpression expression) {
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("«");
            }
            super.visit(expression);
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("»");
            }
        }

        @Override
        public void visit(InfixExpression expression) {
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("«");
            }
            super.visit(expression);
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("»");
            }
        }

        @Override
        public void visit(IntegerExpression expression) {
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("«");
            }
            super.visit(expression);
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("»");
            }
        }

        @Override
        public void visit(NameExpression expression) {
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("«");
            }
            super.visit(expression);
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("»");
            }
        }

        @Override
        public void visit(PostfixExpression expression) {
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("«");
            }
            super.visit(expression);
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("»");
            }
        }

        @Override
        public void visit(PrefixExpression expression) {
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("«");
            }
            super.visit(expression);
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("»");
            }
        }

        @Override
        public void visit(StringExpression expression) {
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("«");
            }
            super.visit(expression);
            if (mError != null && expression == mError.getExpression()) {
                mBuilder.append("»");
            }
        }

    }

    private final class OperandCallbacksImplementation implements OperandCallbacks {
        @Override
        public void addOperandDataChangeListener(OperandDataChangeListener listener) {
        }

        @Override
        public long createOperand(Operand operand) {
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
        public Operand getOperand(long id) {
            return mOperands.get(id);
        }

        @Override
        public ProgramComponentAdapter<Operand> getOperandAdapter(long scopeId, int scope) {
            return null;
        }

        @Override
        public void removeOperandDataChangeListener(OperandDataChangeListener listener) {
        }

        @Override
        public void updateOperand(long id, Operand operand) {
            mOperands.put(id, operand);
        }
    }
}
