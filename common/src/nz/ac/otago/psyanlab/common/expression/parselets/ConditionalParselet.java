
package nz.ac.otago.psyanlab.common.expression.parselets;

import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.Precedence;
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.TokenType;
import nz.ac.otago.psyanlab.common.expression.expressions.ConditionalExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;

/**
 * Parselet for the condition or "ternary" operator, like "a ? b : c".
 */
public class ConditionalParselet implements InfixParselet {
    @Override
    public Expression parse(Parser parser, Expression left, Token token) {
        Expression thenArm = parser.parseExpression();
        parser.consume(TokenType.COLON);
        Expression elseArm = parser.parseExpression(Precedence.CONDITIONAL - 1);

        return new ConditionalExpression(left, thenArm, elseArm);
    }

    @Override
    public int getPrecedence() {
        return Precedence.CONDITIONAL;
    }
}
