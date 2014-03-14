
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;

import android.text.TextUtils;

public class NotNode extends UnaryOperator {
    private static final String SYMBOL = "!";

    static public boolean kindOf(Token token) {
        return TextUtils.equals(SYMBOL, token.getString());
    }

    public NotNode() {
        super(SYMBOL, 5);
    }

    @Override
    protected Object evaluateImplementation(Object childValue) {
        if (childValue != null) {
            return !((Boolean)childValue);
        }
        return null;
    }

    @Override
    public int getAssociativity() {
        return ASSOCIATIVE_RIGHT;
    }
}
