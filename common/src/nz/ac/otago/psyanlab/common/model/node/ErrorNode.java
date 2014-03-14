
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;
import nz.ac.otago.psyanlab.common.model.ExpressionNode;

public class ErrorNode extends ExpressionNode {
    public ErrorNode() {
        super(null, 0);
    }

    public ErrorNode(Token token) {
        super(null, 0, token);
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
        return "{error: " + toString() + "}";
    }

    @Override
    public String toString() {
        Token token = getToken();
        if (token == null) {
            return "";
        }
        return token.toString();
    }

    @Override
    protected Object evaluateImplementation() {
        return null;
    }
}
