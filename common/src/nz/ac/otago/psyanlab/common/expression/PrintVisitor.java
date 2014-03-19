
package nz.ac.otago.psyanlab.common.expression;

import nz.ac.otago.psyanlab.common.expression.expressions.ConditionalExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.ExpressionVisitor;
import nz.ac.otago.psyanlab.common.expression.expressions.FloatExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.InfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.IntegerExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.NameExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.OperatorExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PostfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PrefixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.StringExpression;

public class PrintVisitor implements ExpressionVisitor {
    private int mPrecedence;

    protected StringBuilder mBuilder;

    public PrintVisitor() {
        mBuilder = new StringBuilder();
    }

    @Override
    public String toString() {
        return mBuilder.toString();
    }

    @Override
    public void visit(ConditionalExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            mBuilder.append("(");
        }

        mPrecedence = precedence;
        expression.getCondition().accept(this);

        mBuilder.append(" ? ");

        mPrecedence = precedence;
        expression.getThenArm().accept(this);

        mBuilder.append(" : ");

        mPrecedence = precedence;
        expression.getElseArm().accept(this);

        if (groupedExpression) {
            mBuilder.append(")");
        }
    }

    @Override
    public void visit(FloatExpression expression) {
        mBuilder.append(expression.getValueString());
    }

    @Override
    public void visit(InfixExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            mBuilder.append("(");
        }

        mPrecedence = precedence;
        expression.getLeft().accept(this);

        mBuilder.append(" " + expression.getOperator() + " ");

        mPrecedence = precedence;
        if (expression.getAssociativity() == OperatorExpression.ASSOCIATIVE_LEFT) {
            mPrecedence++;
        }
        expression.getRight().accept(this);

        if (groupedExpression) {
            mBuilder.append(")");
        }
    }

    @Override
    public void visit(IntegerExpression expression) {
        mBuilder.append(expression.getValueString());
    }

    @Override
    public void visit(NameExpression expression) {
        mBuilder.append(expression.getName());
    }

    @Override
    public void visit(PostfixExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            mBuilder.append("(");
        }

        mPrecedence = precedence;
        expression.getLeft().accept(this);
        mBuilder.append(expression.getOperator());

        if (groupedExpression) {
            mBuilder.append(")");
        }
    }

    @Override
    public void visit(PrefixExpression expression) {
        final int parentPrecedence = mPrecedence;
        final int precedence = expression.getPrecedence();
        final boolean groupedExpression = parentPrecedence > precedence;
        if (groupedExpression) {
            mBuilder.append("(");
        }

        mBuilder.append(expression.getOperator());
        mPrecedence = precedence;
        expression.getRight().accept(this);

        if (groupedExpression) {
            mBuilder.append(")");
        }
    }

    @Override
    public void visit(StringExpression expression) {
        mBuilder.append(expression.getString());
    }
}
