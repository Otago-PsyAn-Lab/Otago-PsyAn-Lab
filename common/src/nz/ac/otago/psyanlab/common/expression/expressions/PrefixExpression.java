
package nz.ac.otago.psyanlab.common.expression.expressions;

import nz.ac.otago.psyanlab.common.expression.Precedence;
import nz.ac.otago.psyanlab.common.expression.TokenType;

/**
 * A prefix unary arithmetic expression like "!a" or "-b".
 */
public class PrefixExpression extends OperatorExpression implements Expression {
    private final Expression mRight;

    public PrefixExpression(TokenType operator, Expression right, int precedence) {
        super(operator, precedence, ASSOCIATIVE_RIGHT);
        mRight = right;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public int getPrecedence() {
        return Precedence.PREFIX;
    }

    public Expression getRight() {
        return mRight;
    }
}
