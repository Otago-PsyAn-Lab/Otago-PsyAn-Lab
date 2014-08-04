
package nz.ac.otago.psyanlab.common.expression.parselets;

import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.TokenType;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;

/**
 * Parses parentheses used to group an expression, like "a * (b + c)".
 */
public class GroupParselet implements PrefixParselet {
    @Override
    public Expression parse(Parser parser, Token token) {
        Expression expression = parser.parseExpression();
        parser.consume(TokenType.RIGHT_PAREN);
        return expression;
    }
}
