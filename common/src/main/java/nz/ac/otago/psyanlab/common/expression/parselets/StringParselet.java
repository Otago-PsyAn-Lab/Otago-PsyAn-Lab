
package nz.ac.otago.psyanlab.common.expression.parselets;

import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.StringExpression;

/**
 * Simple parselet for a string literal.
 */
public class StringParselet implements PrefixParselet {
    @Override
    public Expression parse(Parser parser, Token token) {
        return new StringExpression(token.getText());
    }
}
