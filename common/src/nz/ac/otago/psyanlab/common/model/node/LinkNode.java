
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.model.ExpressionNode;

public class LinkNode extends ExpressionNode {
    private static final String SYMBOL = "";

    private ExpressionNode mLinked;

    public LinkNode(ExpressionNode linked) {
        super(SYMBOL, linked.getPrecedence());
        mLinked = linked;
        mLinked.markLinked();
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
        return mLinked.evaluate();
    }

    @Override
    public String printGraph() {
        return mLinked.printGraph();
    }

    @Override
    public String toString() {
        return "";
    }
}
