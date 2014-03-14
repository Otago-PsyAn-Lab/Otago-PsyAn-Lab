
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;
import nz.ac.otago.psyanlab.common.model.ExpressionNode;

public class LiteralNode extends ExpressionNode {
    static public boolean kindOf(Token token) {
        return (token.getType() & Token.TOKEN_LITERAL) != 0;
    }

    public LiteralNode() {
        super("", 0);
    }

    public LiteralNode(Token token) {
        super(token.toString(), 0, token);
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
        return "\"" + getToken().toString() + "\"";
    }

    @Override
    protected Object evaluateImplementation() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isOperator() {
        return false;
    }
}
