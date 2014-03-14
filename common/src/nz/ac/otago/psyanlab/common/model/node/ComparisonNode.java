
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.model.ExpressionNode;

public abstract class ComparisonNode extends BinaryOperator {
    public ComparisonNode(String symbol) {
        super(symbol, 4);
    }

    @Override
    public ExpressionNode addNodeFromLeft(ExpressionNode node) throws ParseException {
        if (node instanceof ComparisonNode) {
            ComparisonNode andLeft = (ComparisonNode)node.addNodeFromRight(new LinkNode(
                    getLeftChild()));
            ComparisonNode andRight = this;
            AndNode andNode = new AndNode(andLeft, andRight);
            andNode.setVirtual(true);
            return andNode;
        }
        return super.addNodeFromLeft(node);
    }

    @Override
    public ExpressionNode addNodeFromRight(ExpressionNode node) throws ParseException {
        if (node instanceof ComparisonNode) {
            ComparisonNode andLeft = this;
            ComparisonNode andRight = (ComparisonNode)node.addNodeFromLeft(new LinkNode(
                    getRightChild()));
            AndNode andNode = new AndNode(andLeft, andRight);
            andNode.setVirtual(true);
            return andNode;
        }
        return super.addNodeFromRight(node);
    }
}
