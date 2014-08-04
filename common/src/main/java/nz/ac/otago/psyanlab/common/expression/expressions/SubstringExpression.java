
package nz.ac.otago.psyanlab.common.expression.expressions;

import nz.ac.otago.psyanlab.common.expression.Precedence;

/**
 * A ternary conditional expression like "a ? b : c".
 */
public class SubstringExpression implements Expression {
    private final Expression mHigh;

    private final Expression mLow;

    private final Expression mString;

    public SubstringExpression(Expression condition, Expression low, Expression high) {
        mString = condition;
        mLow = low;
        mHigh = high;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public Expression getHigh() {
        return mHigh;
    }

    public Expression getLow() {
        return mLow;
    }

    @Override
    public int getPrecedence() {
        return Precedence.SUBSTRING;
    }

    public Expression getString() {
        return mString;
    }
}
