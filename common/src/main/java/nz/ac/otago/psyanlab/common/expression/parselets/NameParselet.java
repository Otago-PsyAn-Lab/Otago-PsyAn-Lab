
package nz.ac.otago.psyanlab.common.expression.parselets;

import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.expressions.BooleanExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.NameExpression;

import android.text.TextUtils;

/**
 * Simple parselet for a named variable like "abc". Also detects boolean
 * literals.
 */
public class NameParselet implements PrefixParselet {
    @Override
    public Expression parse(Parser parser, Token token) {
        String text = token.getText().toLowerCase();
        if (TextUtils.equals(text, "true") || TextUtils.equals(text, "false")) {
            return new BooleanExpression(text);
        }
        return new NameExpression(token.getText());
    }
}
