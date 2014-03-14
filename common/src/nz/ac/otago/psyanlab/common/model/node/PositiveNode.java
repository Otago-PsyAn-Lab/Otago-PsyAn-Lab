
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;
import nz.ac.otago.psyanlab.common.model.ExpressionNode;

import android.text.TextUtils;

public class PositiveNode extends UnaryOperator {
    private static final String SYMBOL = "+";

    public PositiveNode() {
        super(SYMBOL, 1);
    }

    @Override
    protected Object evaluateImplementation(Object childValue) {
        return childValue;
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
