
package nz.ac.otago.psyanlab.common.expression;

import nz.ac.otago.psyanlab.common.expression.RefineTypeVisitor.TypeError;
import nz.ac.otago.psyanlab.common.expression.expressions.BooleanExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.ConditionalExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.FloatExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.InfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.IntegerExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.NameExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PostfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PrefixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.StringExpression;

public class PrintTypeErrorVisitor extends PrintVisitor {
    private TypeError mError;

    private int mErrorEnd;

    private int mErrorStart;

    public PrintTypeErrorVisitor(TypeError error) {
        mError = error;
    }

    public int getErrorEnd() {
        return mErrorEnd;
    }

    public String getErrorMessage() {
        if (mError == null) {
            return "";
        }
        return mError.getErrorMessage();
    }

    public int getErrorStart() {
        return mErrorStart;
    }

    @Override
    public String toString() {
        return super.toString();
    }

    @Override
    public void visit(BooleanExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(ConditionalExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(FloatExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(InfixExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(IntegerExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(NameExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(PostfixExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(PrefixExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }

    @Override
    public void visit(StringExpression expression) {
        if (mError != null && expression == mError.getExpression()) {
            mErrorStart = mBuilder.length();
        }
        super.visit(expression);
        if (mError != null && expression == mError.getExpression()) {
            mErrorEnd = mBuilder.length();
        }
    }
}
