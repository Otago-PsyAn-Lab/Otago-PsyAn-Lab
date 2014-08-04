
package nz.ac.otago.psyanlab.common.expression.parselets;

import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.FloatExpression;

/**
 * Simple parselet for a floating point number.
 */
public class FloatParselet implements PrefixParselet {
    @Override
    public Expression parse(Parser parser, Token token) {
        return new FloatExpression(token.getText());
    }
}
