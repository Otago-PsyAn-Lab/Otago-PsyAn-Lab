
package nz.ac.otago.psyanlab.common.expression;

import nz.ac.otago.psyanlab.common.expression.expressions.ConditionalExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.ExpressionVisitor;
import nz.ac.otago.psyanlab.common.expression.expressions.FloatExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.IntegerExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.NameExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.InfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PostfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.PrefixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.StringExpression;

public class PrintHierarchyVisitor implements ExpressionVisitor {
    StringBuilder mBuilder;

    public PrintHierarchyVisitor() {
        mBuilder = new StringBuilder();
    }

    @Override
    public String toString() {
        return mBuilder.toString();
    }

    @Override
    public void visit(ConditionalExpression expression) {
        mBuilder.append("(");

        expression.getCondition().accept(this);
        mBuilder.append(" ? ");
        expression.getThenArm().accept(this);
        mBuilder.append(" : ");
        expression.getElseArm().accept(this);

        mBuilder.append(")");
    }

    @Override
    public void visit(FloatExpression expression) {
        mBuilder.append(expression.getValueString());
    }

    @Override
    public void visit(InfixExpression expression) {
        mBuilder.append("(");

        expression.getLeft().accept(this);
        mBuilder.append(" " + expression.getOperator() + " ");
        expression.getRight().accept(this);

        mBuilder.append(")");
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
        mBuilder.append("(");

        expression.getLeft().accept(this);
        mBuilder.append(expression.getOperator());

        mBuilder.append(")");
    }

    @Override
    public void visit(PrefixExpression expression) {
        mBuilder.append("(");

        mBuilder.append(expression.getOperator());
        expression.getRight().accept(this);

        mBuilder.append(")");
    }

    @Override
    public void visit(StringExpression expression) {
        mBuilder.append(expression.getString());
    }
}
