
package nz.ac.otago.psyanlab.common.expression.parselets;

import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.Precedence;
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.TokenType;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.InfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.LinkExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.OperatorExpression;

/**
 * Parselet for the condition or "ternary" operator, like "a ? b : c".
 */
public class ComparisonParselet implements InfixParselet {
    @Override
    public Expression parse(Parser parser, Expression left, Token token) {
        Expression right = parser.parseExpression(Precedence.COMPARISON);

        if (left.getPrecedence() == Precedence.COMPARISON) {
            // 'and' the two comparisons and link the right side into our left
            // side.
            Expression link = new LinkExpression(((InfixExpression)left).getRight());
            Expression comparison = new InfixExpression(link, token.getType(), right,
                    Precedence.COMPARISON, OperatorExpression.ASSOCIATIVE_LEFT);
            return new InfixExpression(left, TokenType.AND, comparison, Precedence.AND,
                    OperatorExpression.ASSOCIATIVE_LEFT, true);
        }

        return new InfixExpression(left, token.getType(), right, Precedence.COMPARISON,
                OperatorExpression.ASSOCIATIVE_LEFT);
    }

    @Override
    public int getPrecedence() {
        return Precedence.COMPARISON;
    }
}
