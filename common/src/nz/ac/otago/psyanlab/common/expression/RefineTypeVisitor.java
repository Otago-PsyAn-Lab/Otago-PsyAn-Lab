
package nz.ac.otago.psyanlab.common.expression;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.util.OperandCallbacks;
import nz.ac.otago.psyanlab.common.expression.expressions.BooleanExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.ConditionalExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.ExpressionVisitor;
import nz.ac.otago.psyanlab.common.expression.expressions.FloatExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.InfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.IntegerExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.LinkExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.NameExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.OperatorExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PostfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PrefixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.StringExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.SubstringExpression;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.StubOperand;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Process an expression tree and resolve, as best as possible, the types of the
 * expressions inside. Also generates operands for name expressions and asserts
 * their types as best as possible. As more operands are assigned values, with
 * repeated processing by this visitor, the final type of the root expression is
 * further refined to a single potential type.
 */
public class RefineTypeVisitor implements ExpressionVisitor {
    private OperandCallbacks mCallbacks;

    private Context mContext;

    private TypeError mError;

    private HashMap<String, Long> mOperandMap;

    private int mTypeMask;

    public RefineTypeVisitor(Context context, OperandCallbacks callbacks,
            HashMap<String, Long> operandMap, int rootType) {
        mContext = context;
        mCallbacks = callbacks;
        mOperandMap = operandMap;
        mTypeMask = rootType;
    }

    public TypeError getError() {
        return mError;
    }

    public HashMap<String, Long> getOperandMap() {
        return mOperandMap;
    }

    public int getType() {
        return mTypeMask;
    }

    public boolean hasError() {
        return mError == null;
    }

    @Override
    public String toString() {
        return TextUtils.join(",  ", typeToStringArray(mTypeMask));
    }

    @Override
    public void visit(BooleanExpression expression) {
        mTypeMask = doIntersectionOrError(expression, mTypeMask, Operand.TYPE_BOOLEAN);
    }

    @Override
    public void visit(ConditionalExpression expression) {
        final int parentTypeMask = mTypeMask;

        mTypeMask = Operand.TYPE_BOOLEAN;
        expression.getCondition().accept(this);

        mTypeMask = parentTypeMask;
        expression.getThenArm().accept(this);
        mTypeMask = parentTypeMask;
        expression.getElseArm().accept(this);
        mTypeMask = doIntersectionOrError(expression, parentTypeMask, mTypeMask);
    }

    @Override
    public void visit(FloatExpression expression) {
        mTypeMask = doIntersectionOrError(expression, mTypeMask, Operand.TYPE_FLOAT);
    }

    @Override
    public void visit(InfixExpression expression) {
        boolean sawOnlyFloat = false;
        final int parentTypeMask = mTypeMask;

        mTypeMask = expression.getOperatorChildType();
        expression.getLeft().accept(this);
        if (allowMixedNumbers(expression) && (mTypeMask & Operand.TYPE_NUMBER) != 0) {
            if ((mTypeMask & Operand.TYPE_NUMBER) == Operand.TYPE_FLOAT) {
                sawOnlyFloat = true;
            }
            mTypeMask |= Operand.TYPE_NUMBER;
        }
        expression.getRight().accept(this);
        if (allowMixedNumbers(expression) && (mTypeMask & Operand.TYPE_NUMBER) != 0 && sawOnlyFloat) {
            mTypeMask = Operand.TYPE_FLOAT;
        }

        mTypeMask = expression.getOperatorResultType(mTypeMask);
        mTypeMask = doIntersectionOrError(expression, parentTypeMask, mTypeMask);
    }

    @Override
    public void visit(IntegerExpression expression) {
        mTypeMask = doIntersectionOrError(expression, mTypeMask, Operand.TYPE_NUMBER);
    }

    @Override
    public void visit(LinkExpression expression) {
        expression.getChild().accept(this);
    }

    @Override
    public void visit(NameExpression expression) {
        String name = expression.getName();
        Long operandId = mOperandMap.get(name);
        Operand op;
        if (operandId == null) {
            // No matched operand, so create one.
            op = new StubOperand(name);
            operandId = mCallbacks.createOperand(op);
            mOperandMap.put(name, operandId);
        } else {
            op = mCallbacks.getOperand(operandId);
        }

        // Try to assert the requested type.
        op.attemptRestrictType(mTypeMask);
        mTypeMask = doIntersectionOrError(expression, mTypeMask, op.getType());
    }

    @Override
    public void visit(PostfixExpression expression) {
        final int parentTypeMask = mTypeMask;

        mTypeMask = expression.getOperatorChildType();
        expression.getLeft().accept(this);

        mTypeMask = expression.getOperatorResultType(mTypeMask);
        mTypeMask = doIntersectionOrError(expression, parentTypeMask, mTypeMask);
    }

    @Override
    public void visit(PrefixExpression expression) {
        final int parentTypeMask = mTypeMask;

        mTypeMask = expression.getOperatorChildType();
        expression.getRight().accept(this);

        mTypeMask = expression.getOperatorResultType(mTypeMask);
        mTypeMask = doIntersectionOrError(expression, parentTypeMask, mTypeMask);
    }

    @Override
    public void visit(StringExpression expression) {
        mTypeMask = doIntersectionOrError(expression, mTypeMask, Operand.TYPE_STRING);
    }

    @Override
    public void visit(SubstringExpression expression) {
        final int parentTypeMask = mTypeMask;

        mTypeMask = Operand.TYPE_STRING;
        expression.getString().accept(this);

        mTypeMask = Operand.TYPE_INTEGER;
        expression.getLow().accept(this);

        mTypeMask = Operand.TYPE_INTEGER;
        expression.getHigh().accept(this);

        mTypeMask = doIntersectionOrError(expression, parentTypeMask, Operand.TYPE_STRING);
    }

    private boolean allowMixedNumbers(OperatorExpression expression) {
        switch (expression.getOperator()) {
            case CARET:
            case ASTERISK:
            case SLASH:
            case PLUS:
            case MINUS:
            case LESS_THAN:
            case LESS_THAN_OR_EQUAL_TO:
            case MORE_THAN:
            case MORE_THAN_OR_EQUAL_TO:
                return true;
            default:
                return false;
        }
    }

    private int doIntersectionOrError(Expression expression, int expected, int got) {
        int intersection = got & expected;
        if (intersection == 0) {
            // No intersection, so store error and break out of type refining.
            String errorMessage = formatTypeError(typeToStringArray(expected),
                    typeToStringArray(got));
            mError = new TypeError(expression, errorMessage);
            throw new TypeException(errorMessage);
        }
        return intersection;
    }

    protected String formatTypeError(List<String> expected, List<String> got) {
        Resources res = mContext.getResources();
        return res.getQuantityString(R.plurals.expected_type, expected.size(),
                TextUtils.join(", ", expected))
                + ", but "
                + res.getQuantityString(R.plurals.got_type, got.size(), TextUtils.join(", ", got))
                + ".";
    }

    protected List<String> typeToStringArray(int type) {
        ArrayList<String> types = new ArrayList<String>();
        if ((type & Operand.TYPE_BOOLEAN) != 0) {
            types.add((String)Operand.getTypeString(mContext, Operand.TYPE_BOOLEAN));
        }
        if ((type & Operand.TYPE_FLOAT) != 0) {
            types.add((String)Operand.getTypeString(mContext, Operand.TYPE_FLOAT));
        }
        if ((type & Operand.TYPE_IMAGE) != 0) {
            types.add((String)Operand.getTypeString(mContext, Operand.TYPE_IMAGE));
        }
        if ((type & Operand.TYPE_INTEGER) != 0) {
            types.add((String)Operand.getTypeString(mContext, Operand.TYPE_INTEGER));
        }
        if ((type & Operand.TYPE_SOUND) != 0) {
            types.add((String)Operand.getTypeString(mContext, Operand.TYPE_SOUND));
        }
        if ((type & Operand.TYPE_STRING) != 0) {
            types.add((String)Operand.getTypeString(mContext, Operand.TYPE_STRING));
        }
        if ((type & Operand.TYPE_VIDEO) != 0) {
            types.add((String)Operand.getTypeString(mContext, Operand.TYPE_VIDEO));
        }

        return types;
    }

    public class TypeError {
        private String mErrorMessage;

        private Expression mExpression;

        public TypeError(Expression expression, String errorMessage) {
            mExpression = expression;
            mErrorMessage = errorMessage;
        }

        public String getErrorMessage() {
            return mErrorMessage;
        }

        public Expression getExpression() {
            return mExpression;
        }
    }

    @SuppressWarnings("serial")
    public class TypeException extends RuntimeException {
        public TypeException(String message) {
            super(message);
        }
    }
}
