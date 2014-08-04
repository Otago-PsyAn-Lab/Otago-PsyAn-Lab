
package nz.ac.otago.psyanlab.common.expression.parselets;

import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.IntegerExpression;

/**
 * Simple parselet for an integer.
 */
public class IntegerParselet implements PrefixParselet {
    @Override
    public Expression parse(Parser parser, Token token) {
        return new IntegerExpression(token.getText());
    }
}
