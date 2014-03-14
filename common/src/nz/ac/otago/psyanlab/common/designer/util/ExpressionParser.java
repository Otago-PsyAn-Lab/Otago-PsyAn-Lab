
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.model.ExpressionNode;
import nz.ac.otago.psyanlab.common.model.ExpressionNode.ParseException;
import nz.ac.otago.psyanlab.common.model.node.ParenthesisedBlockNode;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Stack;

/**
 * Parse takes tokens and builds a tree of the expression.
 */
class ExpressionParser {
    private static final int MODE_INNER_BLOCK = 1;

    private static final int MODE_INNER_BLOCK_IMMINENT = 2;

    private static final int MODE_NORMAL = 0;

    protected static final int PARSER_BEGIN_BLOCK = 0X01;

    protected static final int PARSER_END_BLOCK = 0X04;

    protected static final int PARSER_ERROR = 0x100;

    protected static final int PARSER_GENERATE_OPERAND = 0x10;

    private OperandCallbacks mCallbacks;

    private ExpressionParser mInnerBlock;

    private ExpressionNode mLastGeneratedNode;

    private int mMode = MODE_NORMAL;

    private HashMap<String, Long> mOperandIds;

    private ExpressionNode mRoot;

    private Stack<ExpressionNode> mHeldStack;

    public ExpressionParser(OperandCallbacks callbacks, HashMap<String, Long> operandIds) {
        mCallbacks = callbacks;
        mOperandIds = operandIds;
        mHeldStack = new Stack<ExpressionNode>();
    }

    public void addToken(Token token) throws ParseException {
        int instruction = getInstruction(token);

        ExpressionNode node = null;
        if ((instruction & PARSER_BEGIN_BLOCK) != 0) {
            // Open a new inner block.
            mInnerBlock = new ExpressionParser(mCallbacks, mOperandIds);
            mMode = MODE_INNER_BLOCK_IMMINENT;
        } else if ((instruction & PARSER_END_BLOCK) != 0) {
            if (mMode != MODE_INNER_BLOCK) {
                throw new ParseException("Unexpected end of block.");
            }
            // Close inner block and use its tree as our node.
            mMode = MODE_NORMAL;
            mInnerBlock.completeTree();
            node = new ParenthesisedBlockNode(mInnerBlock.getRoot());
            mInnerBlock = null;
        } else if ((instruction & PARSER_GENERATE_OPERAND) != 0) {
            // // Create and store operand.
            // Long operandId = mOperandIds.get(node.toString());
            // Operand operand;
            // if (operandId == null) {
            // operand = new StubOperand(node.toString());
            // operandId = mCallbacks.createOperand(operand);
            // } else {
            // operand = mCallbacks.getOperand(operandId);
            // }
            // node.setOperand(operandId, operand);
            // mOperandIds.put(node.toString(), operandId);
        } else {
            node = ExpressionNode.newNode(token, mLastGeneratedNode);
        }

        if (mMode == MODE_INNER_BLOCK_IMMINENT) {
            mMode = MODE_INNER_BLOCK;
        } else if (mMode == MODE_INNER_BLOCK) {
            mInnerBlock.addNode(node);
        } else {
            addNode(node);
        }
        mLastGeneratedNode = node;
    }

    public void completeTree() throws ParseException {
        while (!mHeldStack.empty()) {
            addNodeToTree(mHeldStack.pop());
        }
    }

    public String formatExpression() {
        return mRoot.toString();
    }

    public HashMap<String, Long> getOperandIds() {
        return mOperandIds;
    }

    public ExpressionNode getRoot() {
        return mRoot;
    }

    public String printState() {
        String s = "";
        if (mRoot != null) {
            s += mRoot.printGraph();
        } else {
            s += "null";
        }

        s += "      stack";
        for (int i = 0; i < mHeldStack.size(); i++) {
            s += " :: ";
            s += mHeldStack.get(i).printGraph();
        }

        if (mInnerBlock != null) {
            s += "     inner(";
            s += mInnerBlock.printState();
            s += ")";
        }

        return s;
    }

    private void addNode(ExpressionNode node) throws ParseException {
        // Log.d("DEBUG ADD NODE", node.printGraph());
        // Log.d("DEBUG PARSER_STATE", printState());
        if (mRoot == null) {
            mRoot = node;
            return;
        }

        if (!mHeldStack.empty()) {
            // Add node to first held node.
            ExpressionNode attempt = mHeldStack.peek().addNodeFromRight(node);
            if (attempt != null) {
                mHeldStack.pop();
                mHeldStack.push(attempt);
            } else {
                if (node.getAssociativity() == ExpressionNode.ASSOCIATIVE_RIGHT) {
                    mHeldStack.push(node);
                } else {
                    ExpressionNode heldNode = mHeldStack.pop();
                    addNodeToTree(heldNode);
                    addNodeToTree(node);
                }
            }
        } else if (node.getAssociativity() == ExpressionNode.ASSOCIATIVE_RIGHT) {
            // This node is right associative so we hold it till it is
            // completed.
            mHeldStack.push(node);
        } else {
            addNodeToTree(node);
        }
    }

    private void addNodeToTree(ExpressionNode node) throws ParseException {
        ExpressionNode attempt = mRoot.addNodeFromRight(node);
        if (attempt == null) {
            throw new ParseException("Failed to add node to tree: " + mRoot.printGraph() + " :: "
                    + node.printGraph());
        }
        mRoot = attempt;
    }

    public int getInstruction(Token token) {
        if (TextUtils.equals("(", token.toString())) {
            return PARSER_BEGIN_BLOCK;
        } else if (TextUtils.equals(")", token.toString())) {
            return PARSER_END_BLOCK;

        }
        return 0;
    }

}
