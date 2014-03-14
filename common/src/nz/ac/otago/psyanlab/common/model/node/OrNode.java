
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;

import android.text.TextUtils;

public class OrNode extends BinaryOperator {
    private static final String SYMBOL = "or";

    static public boolean kindOf(Token token) {
        return TextUtils.equals(SYMBOL, token.getString());
    }

    public OrNode() {
        super(SYMBOL, 8);
    }

    @Override
    protected Object evaluateImplementation(Object left, Object right) {
        if (left != null && right != null) {
            return (Boolean)left || (Boolean)right;
        }
        return null;
    }
}
