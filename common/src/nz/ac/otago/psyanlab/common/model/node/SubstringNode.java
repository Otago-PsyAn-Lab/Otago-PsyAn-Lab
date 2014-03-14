
package nz.ac.otago.psyanlab.common.model.node;

import nz.ac.otago.psyanlab.common.designer.util.Token;
import nz.ac.otago.psyanlab.common.model.ExpressionNode;

import android.text.TextUtils;

public class SubstringNode extends ExpressionNode {
    private static final String SYMBOL = "-";

    private static final int STATE_FIRST_PARAMETER = 0;

    private static final int STATE_SECOND_PARAMETER = 1;

    private static final int STATE_COMPLETE = 2;

    static public boolean kindOf(Token token) {
        return TextUtils.equals(SYMBOL, token.getString());
    }

    private ExpressionNode mLeftChild;

    private int mState;

    private ExpressionNode mIndex;

    private ExpressionNode mLength;

    public SubstringNode() {
        super(SYMBOL, 3);
    }

    @Override
    public ExpressionNode addNodeFromLeft(ExpressionNode node) throws ParseException {
        if (!isHigherPrecedence(node)) {
            return node.addNodeFromRight(this);
        }

        if (mLeftChild != null) {
            mLeftChild = mLeftChild.addNodeFromLeft(node);
        } else {
            mLeftChild = node;
        }
        return this;
    }

    @Override
    public ExpressionNode addNodeFromRight(ExpressionNode node) throws ParseException {
        if (mState == STATE_COMPLETE) {
            return null;
        }

        if (node instanceof SeparatorInstruction) {
            if (mIndex == null) {
                throw new ParseException("Expected integer expression, instead got separator.");
            }
            mState = STATE_SECOND_PARAMETER;
        } else if (node instanceof EndSubstringInstruction) {
            if (mState == STATE_SECOND_PARAMETER) {
                if (mLength == null) {
                    throw new ParseException("Expected integer expression, instead got ].");
                }

            }
            if (mIndex == null) {
                throw new ParseException("Expected integer expression, instead got ].");
            }
            mState = STATE_COMPLETE;
        } else if (mState == STATE_FIRST_PARAMETER) {
            if (mIndex != null) {
                mIndex = mIndex.addNodeFromRight(node);
            } else {
                mIndex = node;
            }
        } else {
            if (mLength != null) {
                mLength = mLength.addNodeFromRight(node);
            } else {
                mLength = node;
            }
        }
        return this;
    }

    @Override
    public String printGraph() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Object evaluateImplementation() {
        boolean error = false;
        String s = "";
        int index = 0;
        int length = 0;

        if (mLeftChild != null) {
            s = (String)mLeftChild.evaluate();
        } else {
            error = true;
        }
        if (mIndex == null) {
            error = true;
        } else {
            index = (Integer)mIndex.evaluate();
        }
        if (mLength != null) {
            length = (Integer)mLength.evaluate();
        }

        if (error) {
            return null;
        }

        if (mLength == null) {
            return s.substring(index);
        }

        return s.substring(index, index + length);
    }
}
