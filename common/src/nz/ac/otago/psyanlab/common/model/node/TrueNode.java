
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;
import nz.ac.otago.psyanlab.common.model.ExpressionNode;

import android.text.TextUtils;

public class TrueNode extends ExpressionNode {
    private static final String SYMBOL = "true";

    static public boolean kindOf(Token token) {
        return TextUtils.equals(SYMBOL, token.getString());
    }

    public TrueNode() {
        super(SYMBOL, 0);
    }

    public TrueNode(Token token) {
        super(SYMBOL, 0, token);
    }

    @Override
    public ExpressionNode addNodeFromLeft(ExpressionNode node) throws ParseException {
        return node.addNodeFromRight(this);
    }

    @Override
    public ExpressionNode addNodeFromRight(ExpressionNode node) throws ParseException {
        return node.addNodeFromLeft(this);
    }

    @Override
    public String printGraph() {
        return "{Literal:" + getPrecedence() + ":" + getToken().toString() + "}";
    }

    @Override
    public String toString() {
        return getToken().toString();
    }

    @Override
    protected Object evaluateImplementation() {
        return true;
    }

    @Override
    public boolean isOperator() {
        return false;
    }
}
