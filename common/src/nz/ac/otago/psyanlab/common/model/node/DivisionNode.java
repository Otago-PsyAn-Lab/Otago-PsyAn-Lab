
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;
import nz.ac.otago.psyanlab.common.model.Operand;

import android.text.TextUtils;

public class DivisionNode extends BinaryOperator {
    private static final String SYMBOL = "/";

    static public boolean kindOf(Token token) {
        return TextUtils.equals(SYMBOL, token.getString());
    }

    public DivisionNode() {
        super(SYMBOL, 2);
    }

    @Override
    protected Object evaluateImplementation(Object left, Object right) {
        if (left != null && right != null) {
            if ((mType & Operand.TYPE_INTEGER) != 0) {
                return (Integer)left / (Integer)right;
            }
            if ((mType & Operand.TYPE_FLOAT) != 0) {
                return (Float)left / (Float)right;
            }
        }
        return null;
    }
}
