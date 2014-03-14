
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.model.ExpressionNode;

public abstract class UnaryOperator extends ExpressionNode {
    private ExpressionNode mChild;

    public UnaryOperator(String symbol, int precedence) {
        super(symbol, precedence);
    }

    @Override
    public ExpressionNode addNodeFromLeft(ExpressionNode node) throws ParseException {
        if (isLowerPrecedence(node)) {
            return node.addNodeFromRight(this);
        }

        if (mChild != null) {
            mChild = mChild.addNodeFromLeft(node);
        } else {
            mChild = node;
        }
        return this;
    }

    @Override
    public ExpressionNode addNodeFromRight(ExpressionNode node) throws ParseException {
        if (isLowerPrecedence(node)) {
            return node.addNodeFromLeft(this);
        }

        if (mChild != null) {
            mChild = mChild.addNodeFromRight(node);
        } else {
            mChild = node;
        }
        return this;
    }

    @Override
    public Object evaluateImplementation() {
        if (mChild != null) {
            return evaluateImplementation(mChild.evaluate());
        }
        return null;
    }

    @Override
    public String printGraph() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (getAssociativity() == ASSOCIATIVE_RIGHT) {
            sb.append(getSymbol());
        }
        if (mChild != null) {
            sb.append(mChild.printGraph());
        }
        if (getAssociativity() == ASSOCIATIVE_LEFT) {
            sb.append(getSymbol());
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (getAssociativity() == ASSOCIATIVE_RIGHT) {
            sb.append(getSymbol());
        }
        if (mChild != null) {
            sb.append(mChild.toString());
        }
        if (getAssociativity() == ASSOCIATIVE_LEFT) {
            sb.append(getSymbol());
        }
        return sb.toString();
    }

    protected abstract Object evaluateImplementation(Object childValue);

}
