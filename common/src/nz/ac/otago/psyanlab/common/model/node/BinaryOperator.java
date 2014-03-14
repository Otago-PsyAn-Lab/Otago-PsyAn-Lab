
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.model.ExpressionNode;

public abstract class BinaryOperator extends ExpressionNode {
    private ExpressionNode mLeftChild;

    private ExpressionNode mRightChild;

    public BinaryOperator(String symbol, int precedence) {
        super(symbol, precedence);
    }

    protected BinaryOperator(String symbol, int precedence, ExpressionNode andLeft,
            ExpressionNode andRight) {
        super(symbol, precedence);
        mLeftChild = andLeft;
        mRightChild = andRight;
    }

    @Override
    public ExpressionNode addNodeFromLeft(ExpressionNode node) throws ParseException {
        if (isLowerPrecedence(node)) {
            return node.addNodeFromRight(this);
        }

        if (mLeftChild != null) {
            throw new ParseException("Already have left child for node " + toString()
                    + ", incoming node was " + node.toString());
        } else {
            mLeftChild = node;
        }
        return this;
    }

    @Override
    public ExpressionNode addNodeFromRight(ExpressionNode node) throws ParseException {
        if (getAssociativity() == ASSOCIATIVE_RIGHT && isLowerPrecedence(node)) {
            // This rule means -a and +a (sign) must have same precedence as ^.
            return null;
        } else if (!isHigherPrecedence(node)) {
            return node.addNodeFromLeft(this);
        }

        if (mRightChild != null) {
            mRightChild = mRightChild.addNodeFromRight(node);
        } else {
            mRightChild = node;
        }
        return this;
    }

    @Override
    public Object evaluateImplementation() {
        Object leftValue = null;
        Object rightValue = null;

        if (mLeftChild != null) {
            leftValue = mLeftChild.evaluate();
        }
        if (mRightChild != null) {
            rightValue = mRightChild.evaluate();
        }

        return evaluateImplementation(leftValue, rightValue);
    }

    public ExpressionNode getLeftChild() {
        return mLeftChild;
    }

    public ExpressionNode getRightChild() {
        return mRightChild;
    }

    @Override
    public String printGraph() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (mLeftChild != null) {
            sb.append(mLeftChild.printGraph() + " ");
        }
        sb.append(getSymbol());
        if (mRightChild != null) {
            sb.append(" " + mRightChild.printGraph());
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String left = "";
        if (mLeftChild != null) {
            left = mLeftChild.toString();
        }
        if (left.length() != 0) {
            sb.append(left + " ");
        }

        if (!isVirtual()) {
            sb.append(getSymbol() + " ");
        }

        String right = "";
        if (mRightChild != null) {
            right = mRightChild.toString();
        }
        if (right.length() != 0) {
            sb.append(right);
        }
        return sb.toString();
    }

    protected abstract Object evaluateImplementation(Object left, Object right);

}
