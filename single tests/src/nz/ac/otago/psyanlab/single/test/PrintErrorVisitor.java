package nz.ac.otago.psyanlab.single.test;

import nz.ac.otago.psyanlab.common.expression.PrintVisitor;
import nz.ac.otago.psyanlab.common.expression.RefineTypeVisitor.TypeError;
import nz.ac.otago.psyanlab.common.expression.expressions.ConditionalExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.FloatExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.InfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.IntegerExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.NameExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PostfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PrefixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.StringExpression;

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