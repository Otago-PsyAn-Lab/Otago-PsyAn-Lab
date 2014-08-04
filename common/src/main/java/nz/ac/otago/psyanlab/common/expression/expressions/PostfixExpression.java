
package nz.ac.otago.psyanlab.common.expression.expressions;

import nz.ac.otago.psyanlab.common.expression.Precedence;
import nz.ac.otago.psyanlab.common.expression.TokenType;

/**
 * A postfix unary arithmetic expression like "a!".
 */
public class PostfixExpression extends OperatorExpression implements Expression {
    private final Expression mLeft;

    public PostfixExpression(Expression left, TokenType operator, int precedence) {
        super(operator, precedence, ASSOCIATIVE_LEFT);
        mLeft = left;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public Expression getLeft() {
        return mLeft;
    }

    @Override
    public int getPrecedence() {
        return Precedence.POSTFIX;
    }
}
