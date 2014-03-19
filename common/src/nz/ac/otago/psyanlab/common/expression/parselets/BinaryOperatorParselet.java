
package nz.ac.otago.psyanlab.common.expression.parselets;

import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.InfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.OperatorExpression;

/**
 * Generic infix parselet for a binary arithmetic operator. The only difference
 * when parsing, "+", "-", "*", "/", and "^" is precedence and associativity, so
 * we can use a single parselet class for all of those.
 */
public class BinaryOperatorParselet implements InfixParselet {
    private final boolean mIsRight;

    private final int mPrecedence;

    public BinaryOperatorParselet(int precedence, boolean isRight) {
        mPrecedence = precedence;
        mIsRight = isRight;
    }

    @Override
    public int getPrecedence() {
        return mPrecedence;
    }

    @Override
    public Expression parse(Parser parser, Expression left, Token token) {
        // To handle right-associative operators like "^", we allow a slightly
        // lower precedence when parsing the right-hand side. This will let a
        // parselet with the same precedence appear on the right, which will
        // then
        // take *this* parselet's result as its left-hand argument.
        Expression right = parser.parseExpression(mPrecedence - (mIsRight ? 1 : 0));

        return new InfixExpression(left, token.getType(), right, mPrecedence,
                mIsRight ? OperatorExpression.ASSOCIATIVE_RIGHT
                        : OperatorExpression.ASSOCIATIVE_LEFT);
    }
}
