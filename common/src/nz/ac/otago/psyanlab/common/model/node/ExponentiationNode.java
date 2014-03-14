
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;
import nz.ac.otago.psyanlab.common.model.Operand;

import android.text.TextUtils;

public class ExponentiationNode extends BinaryOperator {
    private static final String SYMBOL = "^";

    static public boolean kindOf(Token token) {
        return TextUtils.equals(SYMBOL, token.getString());
    }

    public ExponentiationNode() {
        super(SYMBOL, 1);
    }

    @Override
    protected Object evaluateImplementation(Object left, Object right) {
        if (left != null && right != null) {
            double result = Math.pow((Double)left, (Double)right);
            if ((mType & Operand.TYPE_INTEGER) != 0) {
                return (int)result;
            }
            if ((mType & Operand.TYPE_FLOAT) != 0) {
                return (float)result;
            }
        }
        return null;
    }

    @Override
    public int getAssociativity() {
        return ASSOCIATIVE_RIGHT;
    }
}
