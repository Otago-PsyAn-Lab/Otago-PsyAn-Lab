
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;
import nz.ac.otago.psyanlab.common.model.ExpressionNode;
import nz.ac.otago.psyanlab.common.model.Operand;

import android.text.TextUtils;

public class NegativeNode extends UnaryOperator {
    private static final String SYMBOL = "-";

    public NegativeNode() {
        super(SYMBOL, 1);
    }

    @Override
    protected Object evaluateImplementation(Object childValue) {
        if (childValue != null) {
            if ((mType & Operand.TYPE_INTEGER) != 0) {
                return -(Integer)childValue;
            }
            if ((mType & Operand.TYPE_FLOAT) != 0) {
                return -(Float)childValue;
            }
        }
        return null;
    }

    @Override
    public int getAssociativity() {
        return ASSOCIATIVE_RIGHT;
    }

    public static boolean matches(Token token, ExpressionNode lastGeneratedNode) {
        if (lastGeneratedNode == null) {
            // No last node so we must be unary right associative.
            return TextUtils.equals(SYMBOL, token.getString());
        } else if (!lastGeneratedNode.isOperator()) {
            return false;
        }
        // Last node was an operator so we must be unary.
        return TextUtils.equals(SYMBOL, token.getString());
    }
}
