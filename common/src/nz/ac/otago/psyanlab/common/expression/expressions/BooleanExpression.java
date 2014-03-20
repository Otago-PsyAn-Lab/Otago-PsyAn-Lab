
package nz.ac.otago.psyanlab.common.expression.expressions;

import nz.ac.otago.psyanlab.common.expression.Precedence;

/**
 * A simple variable name expression like "abc".
 */
public class BooleanExpression implements Expression {
    private final String mName;

    public BooleanExpression(String name) {
        mName = name;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int getPrecedence() {
        return Precedence.IDENTITY;
    }

    public String getValueString() {
        return mName;
    }
}
