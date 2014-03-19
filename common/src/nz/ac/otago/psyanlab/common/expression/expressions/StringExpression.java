
package nz.ac.otago.psyanlab.common.expression.expressions;

import nz.ac.otago.psyanlab.common.expression.Precedence;

/**
 * A simple variable name expression like "abc".
 */
public class StringExpression implements Expression {
    private final String mName;

    public StringExpression(String name) {
        mName = name;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public String getString() {
        return "\"" + mName + "\"";
    }

    @Override
    public int getPrecedence() {
        return Precedence.IDENTITY;
    }
}
