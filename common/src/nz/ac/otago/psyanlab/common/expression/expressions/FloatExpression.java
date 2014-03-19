
package nz.ac.otago.psyanlab.common.expression.expressions;

import nz.ac.otago.psyanlab.common.expression.Precedence;

/**
 * A simple variable name expression like "abc".
 */
public class FloatExpression implements Expression {
    private final String mName;

    public FloatExpression(String name) {
        mName = name;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public String getValueString() {
        return mName;
    }

    @Override
    public int getPrecedence() {
        return Precedence.IDENTITY;
    }
}
