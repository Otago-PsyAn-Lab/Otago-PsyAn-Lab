
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.model.ExpressionNode;

public class ParenthesisedBlockNode extends ExpressionNode {
    private static final String SYMBOL = "";

    private ExpressionNode mInner;

    public ParenthesisedBlockNode(ExpressionNode inner) {
        super(SYMBOL, 0);
        mInner = inner;
        mInner.markLinked();
    }

    @Override
    public ExpressionNode addNodeFromLeft(ExpressionNode node) throws ParseException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ExpressionNode addNodeFromRight(ExpressionNode node) throws ParseException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Object evaluateImplementation() {
        return mInner.evaluate();
    }

    @Override
    public String printGraph() {
        return mInner.printGraph();
    }

    @Override
    public String toString() {
        return "";
    }
    
    @Override
    public boolean isOperator() {
        return false;
    }
}
