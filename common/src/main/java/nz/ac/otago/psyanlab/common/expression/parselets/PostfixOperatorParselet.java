
package nz.ac.otago.psyanlab.common.expression.parselets;

import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.PostfixExpression;

/**
 * Generic infix parselet for an unary arithmetic operator. Parses postfix unary
 * "?" expressions.
 */
public class PostfixOperatorParselet implements InfixParselet {
    public PostfixOperatorParselet(int precedence) {
        mPrecedence = precedence;
    }

    @Override
    public Expression parse(Parser parser, Expression left, Token token) {
        return new PostfixExpression(left, token.getType(), mPrecedence);
    }

    @Override
    public int getPrecedence() {
        return mPrecedence;
    }

    private final int mPrecedence;
}
