
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;
import nz.ac.otago.psyanlab.common.model.ExpressionNode;

import android.text.TextUtils;

public class AndNode extends BinaryOperator {
    private static final String SYMBOL = "and";

    static public boolean kindOf(Token token) {
        return TextUtils.equals(SYMBOL, token.getString());
    }

    public AndNode() {
        super(SYMBOL, 6);
    }

    public AndNode(ExpressionNode andLeft, ExpressionNode andRight) {
        super(SYMBOL, 6, andLeft, andRight);
    }

    @Override
    protected Object evaluateImplementation(Object left, Object right) {
        if (left != null && right != null) {
            return (Boolean)left && (Boolean)right;
        }
        return null;
    }
}
