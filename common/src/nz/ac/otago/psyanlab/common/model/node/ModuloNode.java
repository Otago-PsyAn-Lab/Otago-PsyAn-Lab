
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;

import android.text.TextUtils;

public class ModuloNode extends BinaryOperator {
    private static final String SYMBOL = "%";

    static public boolean kindOf(Token token) {
        return TextUtils.equals(SYMBOL, token.getString());
    }

    public ModuloNode() {
        super(SYMBOL, 2);
    }

    @Override
    protected Object evaluateImplementation(Object left, Object right) {
        if (left != null && right != null) {
            return (Integer)left % (Integer)right;
        }
        return null;
    }
}
