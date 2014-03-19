
package nz.ac.otago.psyanlab.common.expression.expressions;

import nz.ac.otago.psyanlab.common.expression.TokenType;

/**
 * A binary arithmetic expression like "a + b" or "c ^ d".
 */
public class InfixExpression extends OperatorExpression implements Expression {
    private final Expression mLeft;

    private final Expression mRight;

    public InfixExpression(Expression left, TokenType operator, Expression right, int precedence,
            int associativity) {
        super(operator, precedence, associativity);
        mLeft = left;
        mRight = right;
    }

    public InfixExpression(Expression left, TokenType operator, Expression right, int precedence,
            int associativity, boolean isVirtual) {
        super(operator, precedence, associativity, isVirtual);
        mLeft = left;
        mRight = right;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public Expression getLeft() {
        return mLeft;
    }

    public Expression getRight() {
        return mRight;
    }
}
