
package nz.ac.otago.psyanlab.common.expression.parselets;

import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.Precedence;
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.TokenType;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.SubstringExpression;

/**
 * Parselet for the substring ternary operator.
 */
public class SubstringParselet implements InfixParselet {
    @Override
    public Expression parse(Parser parser, Expression left, Token token) {
        Expression low = parser.parseExpression();
        parser.consume(TokenType.COMMA);
        Expression high = parser.parseExpression();
        parser.consume(TokenType.RIGHT_BRACKET);

        return new SubstringExpression(left, low, high);
    }

    @Override
    public int getPrecedence() {
        return Precedence.SUBSTRING;
    }
}
