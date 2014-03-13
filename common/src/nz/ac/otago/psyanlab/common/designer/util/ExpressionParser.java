
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler.Token;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.StubOperand;

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

    private Node mLastGeneratedNode;

    private int mMode = MODE_NORMAL;

    private HashMap<String, Long> mOperandIds;

    private Node mRoot;

    private Stack<Node> mHeldStack;

    private boolean mWasError;

    public ExpressionParser(OperandCallbacks callbacks, HashMap<String, Long> operandIds) {
        mCallbacks = callbacks;
        mOperandIds = operandIds;
        mWasError = false;
        mHeldStack = new Stack<Node>();
    }

    public void addToken(Token token) {
        if (mWasError) {
            return;
        }

        Node node = Node.newNode(token, mLastGeneratedNode);
        mLastGeneratedNode = node;
        int instruction = node.getInstruction();
        if ((instruction & PARSER_BEGIN_BLOCK) != 0) {
            // Open a new inner block.
            mInnerBlock = new ExpressionParser(mCallbacks, mOperandIds);
            mMode = MODE_INNER_BLOCK_IMMINENT;
        } else if ((instruction & PARSER_END_BLOCK) != 0) {
            if (mMode != MODE_INNER_BLOCK) {
                token.markError("Unexpected end of grouping.");
                mWasError = true;
                return;
            }
            // Close inner block and use its tree as our node.
            mMode = MODE_NORMAL;
            mInnerBlock.completeTree();
            node = mInnerBlock.getRoot();
            node.setParenthesised(true);
            mInnerBlock = null;
        } else if ((instruction & PARSER_GENERATE_OPERAND) != 0) {
            // Create and store operand.
            Operand operand = new StubOperand(node.toString());
            long operandId = mCallbacks.createOperand(operand);
            node.setOperand(operandId, operand);
            mOperandIds.put(node.toString(), operandId);
        }

        if (mMode == MODE_INNER_BLOCK_IMMINENT) {
            mMode = MODE_INNER_BLOCK;
        } else if (mMode == MODE_INNER_BLOCK) {
            mWasError = mInnerBlock.addNode(node);
        } else {
            mWasError = addNode(node);
        }
    }

    public void completeTree() {
        while (!mHeldStack.empty()) {
            if (addNodeToTree(mHeldStack.pop()) == Node.NODE_ERROR) {
                break;
            }
        }

        mRoot.assertComplete();
    }

    public String formatExpression() {
        return mRoot.prettyPrint();
    }

    public HashMap<String, Long> getOperandIds() {
        return mOperandIds;
    }

    public Node getRoot() {
        return mRoot;
    }

    public String printState() {
        String s = "";
        if (mRoot != null) {
            s += mRoot.printTree();
        } else {
            s += "null";
        }

        s += "      stack";
        for (int i = 0; i < mHeldStack.size(); i++) {
            s += " :: ";
            s += mHeldStack.get(i).printTree();
        }

        if (mInnerBlock != null) {
            s += "     inner(";
            s += mInnerBlock.printState();
            s += ")";
        }

        return s;
    }

    /**
     * Add a node to the tree.
     * 
     * @param node
     * @return
     */
    private boolean addNode(Node node) {
        if (mRoot == null) {
            mRoot = node;
            return false;
        }

        if (!mHeldStack.empty()) {
            // Add node to first held node.
            int addResult = mHeldStack.peek().addNodeRight(node);
            if (addResult == Node.NODE_ERROR) {
                return true;
            } else if (addResult == Node.NODE_NOT_ADDED) {
                // Node not consumed in add, this tells us the currently held
                // node on top of the stack is actually complete. We just
                // continue to hold it because this node may go on the stack
                // first.
                if (node.getAssociativity() == Node.ASSOCIATIVITY_RIGHT) {
                    // This node is right associative so we hold it till it is
                    // completed.
                    mHeldStack.push(node);
                } else {
                    // Node is left associative, but we need to add the top node
                    // on the stack to the tree first.
                    Node heldNode = mHeldStack.pop();
                    if (addNodeToTree(heldNode) == Node.NODE_ERROR) {
                        return true;
                    }

                    // Now that the held node has been added to the tree, we can
                    // add the node we started with.
                    if (addNodeToTree(node) == Node.NODE_ERROR) {
                        return true;
                    }
                }
            }
        } else {
            if (node.getAssociativity() == Node.ASSOCIATIVITY_RIGHT) {
                // This node is right associative so we hold it till it is
                // completed.
                mHeldStack.push(node);
            } else {
                int result = addNodeToTree(node);
                if (result == Node.NODE_INSERT_AND) {
                    Node and = new AndNode(null);
                    and.addNodeLeft(mRoot);
                    and.addNodeRight(node);
                    mRoot = and;
                }
                if (result == Node.NODE_ERROR) {
                    return true;
                }
            }
        }

        return false;
    }

    private int addNodeToTree(Node node) {
        int addResult = mRoot.addNodeRight(node);
        if (addResult == Node.NODE_ERROR) {
            return addResult;
        }

        if (addResult == Node.NODE_NOT_ADDED) {
            int rootAddResult = node.addNodeLeft(mRoot);
            if (rootAddResult == Node.NODE_ERROR) {
                return rootAddResult;
            }

            if (rootAddResult == Node.NODE_NOT_ADDED) {
                // Something went wrong.
                throw new RuntimeException(
                        "Something went wrong building the expression graph. Expected root to become child of node but got result 'node_not_added'.");
            }

            if (rootAddResult == Node.NODE_INSERT_AND) {
                Node and = new AndNode(null);
                and.addNodeLeft(mRoot);
                Node virt = mRoot.getRightNodeLink();
                node.addNodeLeft(virt);
                and.addNodeRight(node);
                node = and;
            }

            mRoot = node;
        } else if (addResult == Node.NODE_INSERT_AND) {
            Node and = new AndNode(null);
            and.addNodeLeft(mRoot);
            Node virt = mRoot.getRightNodeLink();
            node.addNodeLeft(virt);
            and.addNodeRight(node);
            mRoot = and;
        }

        return Node.NODE_ADDED;
    }

    static class AndNode extends BinaryOperator {
        private static final String SYMBOL = "and";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public AndNode(Token token) {
            super(token, 7);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_BOOLEAN;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected boolean type.";
        }
    }

    static class AsterixNode extends BinaryOperator {
        private static final String SYMBOL = "*";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public AsterixNode(Token token) {
            super(token, 3);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_NUMBER;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected number type.";
        }
    }

    static class BeginSubBlockInstruction extends Node {
        private static final String SYMBOL = "(";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public BeginSubBlockInstruction(Token token) {
            super(token, 0);
        }

        @Override
        public void assertComplete() {
        }

        @Override
        public int getInstruction() {
            return PARSER_BEGIN_BLOCK;
        }

        @Override
        public String printTree() {
            return toString();
        }

        @Override
        public String toString() {
            return SYMBOL;
        }
    }

    static class BeginSubstringInstruction extends Node {
        private static final String SYMBOL = "[";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public BeginSubstringInstruction(Token token) {
            super(token, 0);
        }

        @Override
        public void assertComplete() {
        }

        @Override
        public String toString() {
            return SYMBOL;
        }
    }

    static abstract class BinaryOperator extends Node {
        protected Node mLeft;

        protected Node mRight;

        public BinaryOperator(Token token, int precedence) {
            super(token, precedence);
        }

        @Override
        public int addNodeLeft(Node node) {
            if (getAssociativity() == ASSOCIATIVITY_RIGHT && isLowerPrecedence(node)) {
                return NODE_NOT_ADDED;
            } else if (!isHigherPrecedence(node)) {
                return NODE_NOT_ADDED;
            }

            if (mLeft != null) {
                // Left is full so try the right side.
                return addNodeRight(node);
            }

            if (node.assertType(getBaseType())) {
                mLeft = node;
                return NODE_ADDED;
            }
            node.markError(getExpectedTypeErrorString());
            return NODE_ERROR;
        }

        @Override
        public int addNodeRight(Node node) {
            if (getAssociativity() == ASSOCIATIVITY_RIGHT && isLowerPrecedence(node)) {
                return NODE_NOT_ADDED;
            } else if (!isHigherPrecedence(node)) {
                return NODE_NOT_ADDED;
            }

            if (mRight != null) {

                int rightAddResult = mRight.addNodeRight(node);
                if (rightAddResult == NODE_NOT_ADDED) {
                    // Must be rewriting the tree.
                    int result = node.addNodeLeft(mRight);
                    if (result == NODE_ADDED) {
                        if (node.assertType(getBaseType())) {
                            mRight = node;
                            return result;
                        }
                        node.markError(getExpectedTypeErrorString());
                        return NODE_ERROR;
                    }
                    return result;
                }
                return rightAddResult;
            }

            if (node.assertType(getBaseType())) {
                mRight = node;
                return NODE_ADDED;
            }
            node.markError(getExpectedTypeErrorString());
            return NODE_ADDED;
        }

        @Override
        public void assertComplete() {
            if (mLeft == null) {
                markError("Missing left term.");
            } else {
                mLeft.assertComplete();
            }
            if (mRight == null) {
                markError("Missing right term.");
            } else {
                mRight.assertComplete();
            }
        }

        @Override
        public boolean assertType(int type) {
            boolean r = (type & getBaseType()) != 0;

            if (mLeft != null) {
                r = r && mLeft.assertType(type);
            }

            if (mRight != null) {
                r = r && mRight.assertType(type);
            }

            return r;
        }

        @Override
        public int getType() {
            return orOperandTypes();
        }

        @Override
        public String printTree() {
            return "(" + ((mLeft == null) ? "" : mLeft.printTree()) + " " + toString() + " "
                    + ((mRight == null) ? "" : mRight.printTree()) + ")";
        }

        protected abstract int getBaseType();

        protected abstract String getExpectedTypeErrorString();

        /**
         * Or the left and right operands types together. Handle the cases where
         * some may be null.
         * 
         * @return Or'ed types.
         */
        protected int orOperandTypes() {
            if (mRight == null) {
                if (mLeft == null) {
                    return getBaseType();
                }
                return mRight.getType();
            }
            if (mLeft == null) {
                return mLeft.getType();
            }
            return mLeft.getType() | mRight.getType();
        }

        @Override
        protected String pretty() {
            return ((mLeft == null) ? "" : mLeft.prettyPrint())
                    + ((mVirtual) ? "" : " " + toString() + " ")
                    + ((mRight == null) ? "" : mRight.prettyPrint());
        }
    }

    static abstract class ComparisonOperator extends BinaryOperator {
        public ComparisonOperator(Token token) {
            super(token, 5);
        }

        @Override
        public int addNodeLeft(Node node) {
            if (node instanceof ComparisonOperator) {
                return NODE_INSERT_AND;
            }
            return super.addNodeLeft(node);
        }

        @Override
        public int addNodeRight(Node node) {
            if (node instanceof ComparisonOperator) {
                return NODE_INSERT_AND;
            }
            return super.addNodeRight(node);
        }

        @Override
        public Node getRightNodeLink() {
            return new LinkNode(mRight);
        }

        @Override
        public boolean assertType(int type) {
            return (type & Operand.TYPE_BOOLEAN) != 0;
        }
    }

    static class LinkNode extends Node {

        private Node mLinked;

        public LinkNode(Node linked) {
            super(null, 0);
            mLinked = linked;
        }

        @Override
        public int getPrecedence() {
            return mLinked.getPrecedence();
        }

        @Override
        public Node getRightNodeLink() {
            return mLinked.getRightNodeLink();
        }

        @Override
        public int addNodeLeft(Node node) {
            return mLinked.addNodeLeft(node);
        }

        @Override
        public int addNodeRight(Node node) {
            return mLinked.addNodeRight(node);
        }

        @Override
        public void assertComplete() {
        }

        @Override
        public boolean assertType(int type) {
            return mLinked.assertType(type);
        }

        @Override
        public int getInstruction() {
            return mLinked.getInstruction();
        }

        @Override
        public int getType() {
            return mLinked.getType();
        }

        @Override
        public void markError(String string) {
            mLinked.markError(string);
        }

        @Override
        public String prettyPrint() {
            return "";
        }

        @Override
        public String printTree() {
            return "{" + mLinked.printTree() + "}";
        }

        @Override
        public void refresh() {
        }

        @Override
        public void setOperand(long operandId, Operand operand) {
            mLinked.setOperand(operandId, operand);
        }

        @Override
        public void setParenthesised(boolean parenthesised) {
            mLinked.setParenthesised(parenthesised);
        }

        @Override
        public String toString() {
            return null;
        }

        @Override
        protected int getAssociativity() {
            return mLinked.getAssociativity();
        }

        @Override
        protected boolean isHigherPrecedence(Node node) {
            return mLinked.isHigherPrecedence(node);
        }

        @Override
        protected boolean isLowerPrecedence(Node node) {
            return mLinked.isLowerPrecedence(node);
        }

        @Override
        protected String pretty() {
            return "";
        }

    }

    static class EndSubBlockInstruction extends Node {
        private static final String SYMBOL = ")";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public EndSubBlockInstruction(Token token) {
            super(token, 0);
        }

        @Override
        public void assertComplete() {
        }

        @Override
        public int getInstruction() {
            return PARSER_END_BLOCK;
        }

        @Override
        public String toString() {
            return SYMBOL;
        }
    }

    static class EndSubstringInstruction extends Node {
        private static final String SYMBOL = "]";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public EndSubstringInstruction(Token token) {
            super(token, 0);
        }

        @Override
        public void assertComplete() {
        }

        @Override
        public String toString() {
            return SYMBOL;
        }
    }

    static class EqualsNode extends ComparisonOperator {
        private static final String SYMBOL = "=";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public EqualsNode(Token token) {
            super(token);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_NON_ASSETS;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected non asset type.";
        }
    }

    static class ErrorNode extends VariableNode {

        public ErrorNode(Token token) {
            super(token);
        }

        @Override
        public int getInstruction() {
            return PARSER_ERROR;
        }
    }

    static class ExponentNode extends BinaryOperator {
        private static final String SYMBOL = "^";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public ExponentNode(Token token) {
            super(token, 1);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getAssociativity() {
            return ASSOCIATIVITY_RIGHT;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_NUMBER;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected number type.";
        }
    }

    static class LessThanNode extends ComparisonOperator {
        private static final String SYMBOL = "<";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public LessThanNode(Token token) {
            super(token);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_NON_ASSETS;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected non asset type.";
        }
    }

    static class LessThanOrEqualsNode extends ComparisonOperator {
        private static final String SYMBOL = "<=";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public LessThanOrEqualsNode(Token token) {
            super(token);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_NON_ASSETS;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected non asset type.";
        }
    }

    static class LiteralNode extends Node {
        static public boolean kindOf(Token token) {
            return (token.getType() & ExpressionCompiler.TOKEN_LITERAL) != 0;
        }

        private int mType;

        private String mValue;

        public LiteralNode(Token token) {
            super(token, 0);
            mValue = token.getString();
            if ((mToken.getType() & ExpressionCompiler.TOKEN_NUMBER) != 0) {
                try {
                    Integer.parseInt(mValue);
                    mType = Operand.TYPE_INTEGER;
                } catch (NumberFormatException e) {
                    mType = Operand.TYPE_FLOAT;
                }
            } else {
                mType = Operand.TYPE_STRING;
            }
        }

        @Override
        public void assertComplete() {
        }

        @Override
        public int getType() {
            if ((mToken.getType() & ExpressionCompiler.TOKEN_NUMBER) != 0) {
                return mType;
            }
            return Operand.TYPE_STRING;
        }

        @Override
        public String printTree() {
            return prettyPrint();
        }

        @Override
        public String toString() {
            return mValue;
        }

        @Override
        protected String pretty() {
            if (mType == Operand.TYPE_STRING) {
                return "\"" + toString() + "\"";
            }
            return toString();
        }
    }

    static class MiddleSubstringInstruction extends Node {
        private static final String SYMBOL = ",";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public MiddleSubstringInstruction(Token token) {
            super(token, 0);
        }

        @Override
        public void assertComplete() {
        }

        @Override
        public String toString() {
            return SYMBOL;
        }
    }

    static class MinusNode extends BinaryOperator {
        private static final String SYMBOL = "-";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public MinusNode(Token token) {
            super(token, 3);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_NUMBER;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected number type.";
        }
    }

    static class ModuloNode extends BinaryOperator {
        private static final String SYMBOL = "%";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public ModuloNode(Token token) {
            super(token, 3);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_INTEGER;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected integer type.";
        }
    }

    static class MoreThanNode extends ComparisonOperator {
        private static final String SYMBOL = ">";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public MoreThanNode(Token token) {
            super(token);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_NON_ASSETS;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected non asset type.";
        }
    }

    static class MoreThanOrEqualsNode extends ComparisonOperator {
        private static final String SYMBOL = ">=";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public MoreThanOrEqualsNode(Token token) {
            super(token);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_NON_ASSETS;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected non asset type.";
        }
    }

    static class NegativeSignNode extends Node {
        private static final String SYMBOL = "-";

        public static boolean matches(Token token, Node lastGeneratedNode) {
            if (lastGeneratedNode == null) {
                // No last node so we must be unary right associative.
                return TextUtils.equals(SYMBOL, token.getString());
            } else if (lastGeneratedNode instanceof VariableNode
                    || lastGeneratedNode instanceof LiteralNode
                    || lastGeneratedNode instanceof EndSubBlockInstruction) {
                // a + ?, 1 + ?, ) + ?.
                return false;
            }
            // Last node was an operator so we must be unary right associative.
            return TextUtils.equals(SYMBOL, token.getString());
        }

        private Node mChild;

        public NegativeSignNode(Token token) {
            super(token, 2);
        }

        @Override
        public int addNodeLeft(Node node) {
            if (isLowerPrecedence(node)) {
                return NODE_NOT_ADDED;
            }

            if (mChild != null) {
                return NODE_NOT_ADDED;
            }

            if (node.assertType(Operand.TYPE_NUMBER)) {
                mChild = node;
                return NODE_ADDED;
            }
            node.markError("Expected a number.");
            return NODE_ERROR;
        }

        @Override
        public int addNodeRight(Node node) {
            return addNodeLeft(node);
        }

        @Override
        public void assertComplete() {
            if (mChild == null) {
                markError("Missing term.");
            } else {
                mChild.assertComplete();
            }
        }

        @Override
        public int getType() {
            if (mChild != null) {
                return mChild.getType();
            }
            return Operand.TYPE_NUMBER;
        }

        @Override
        public String printTree() {
            return "(" + toString() + ((mChild == null) ? "" : mChild.printTree()) + ")";
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getAssociativity() {
            return ASSOCIATIVITY_RIGHT;
        }

        @Override
        protected String pretty() {
            return toString() + ((mChild == null) ? "" : mChild.prettyPrint());
        }
    }

    /**
     * Construction blocks used to build an expression graph, though not
     * strictly so. Nodes are not necessarily complete in meaning and may be
     * used as a state message passing mechanism.
     */
    static abstract class Node {
        protected static final int ASSOCIATIVITY_LEFT = 1;

        protected static final int ASSOCIATIVITY_RIGHT = 2;

        protected static final int NODE_ADDED = 1;

        protected static final int NODE_ERROR = 2;

        protected static final int NODE_INSERT_AND = 3;

        protected static final int NODE_NOT_ADDED = 0;

        public static Node newNode(Token token, Node lastGeneratedNode) {
            Node node;
            if (AndNode.kindOf(token)) {
                node = new AndNode(token);
            } else if (OrNode.kindOf(token)) {
                node = new OrNode(token);
            } else if (NotNode.kindOf(token)) {
                node = new NotNode(token);
            } else if (LiteralNode.kindOf(token)) {
                node = new LiteralNode(token);
            } else if (LessThanNode.kindOf(token)) {
                node = new LessThanNode(token);
            } else if (MoreThanNode.kindOf(token)) {
                node = new MoreThanNode(token);
            } else if (LessThanOrEqualsNode.kindOf(token)) {
                node = new LessThanOrEqualsNode(token);
            } else if (MoreThanOrEqualsNode.kindOf(token)) {
                node = new MoreThanOrEqualsNode(token);
            } else if (EqualsNode.kindOf(token)) {
                node = new EqualsNode(token);
            } else if (NotEqualsNode.kindOf(token)) {
                node = new NotEqualsNode(token);
            } else if (NegativeSignNode.matches(token, lastGeneratedNode)) {
                node = new NegativeSignNode(token);
            } else if (PositiveSignNode.matches(token, lastGeneratedNode)) {
                // We know when we are a sign operator when we haven't already
                // seen a token or when that token was not a literal, identity,
                // or block.
                node = new PositiveSignNode(token);
            } else if (PlusNode.kindOf(token)) {
                node = new PlusNode(token);
            } else if (MinusNode.kindOf(token)) {
                node = new MinusNode(token);
            } else if (AsterixNode.kindOf(token)) {
                node = new AsterixNode(token);
            } else if (SlashNode.kindOf(token)) {
                node = new SlashNode(token);
            } else if (BeginSubBlockInstruction.kindOf(token)) {
                node = new BeginSubBlockInstruction(token);
            } else if (EndSubBlockInstruction.kindOf(token)) {
                node = new EndSubBlockInstruction(token);
            } else if (BeginSubstringInstruction.kindOf(token)) {
                node = new BeginSubstringInstruction(token);
            } else if (MiddleSubstringInstruction.kindOf(token)) {
                node = new MiddleSubstringInstruction(token);
            } else if (EndSubstringInstruction.kindOf(token)) {
                node = new EndSubstringInstruction(token);
            } else if (VariableNode.kindOf(token)) {
                // Do variables last to ensure keywords are parsed.
                node = new VariableNode(token);
            } else {
                node = new ErrorNode(token);
            }
            return node;
        }

        public Node getRightNodeLink() {
            return null;
        }

        private boolean mParenthesised;

        protected Operand mOperand;

        protected long mOperandId;

        protected int mPrecedence;

        protected Token mToken;

        protected boolean mVirtual;

        public Node(Token token, int precedence) {
            mToken = token;
            if (mToken == null) {
                mVirtual = true;
            }
            mPrecedence = precedence;
        }

        public int addNodeLeft(Node node) {
            return NODE_NOT_ADDED;
        }

        public int addNodeRight(Node node) {
            return NODE_NOT_ADDED;
        }

        public abstract void assertComplete();

        public boolean assertType(int type) {
            return (type & getType()) != 0;
        }

        public int getInstruction() {
            return 0;
        }

        public int getPrecedence() {
            return mPrecedence;
        }

        public int getType() {
            return Operand.TYPE_ANY;
        }

        public void markError(String string) {
            if (!mVirtual) {
                mToken.markError(string);
            }
        }

        public String prettyPrint() {
            if (mParenthesised) {
                return "(" + pretty() + ")";
            }
            return pretty();
        }

        public String printTree() {
            return toString();
        }

        public void refresh() {

        }

        public void setOperand(long operandId, Operand operand) {
            mOperandId = operandId;
            mOperand = operand;
        }

        public void setParenthesised(boolean parenthesised) {
            mParenthesised = parenthesised;
        }

        public abstract String toString();

        protected int getAssociativity() {
            return ASSOCIATIVITY_LEFT;
        }

        protected boolean isHigherPrecedence(Node node) {
            // Lower values have a 'higher' precedence, so we have to reverse
            // the inequality comparison. Which is why we use a helper method to
            // hide it all.
            return node.getPrecedence() < mPrecedence;
        }

        protected boolean isLowerPrecedence(Node node) {
            // Lower values have a 'higher' precedence, so we have to reverse
            // the inequality comparison. Which is why we use a helper method to
            // hide it all.
            return node.getPrecedence() > mPrecedence;
        }

        protected String pretty() {
            if (mVirtual) {
                return "";
            }
            return toString();
        }
    }

    static class NotEqualsNode extends ComparisonOperator {
        private static final String SYMBOL = "<>";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public NotEqualsNode(Token token) {
            super(token);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_NON_ASSETS;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected non asset type.";
        }
    }

    static class NotNode extends Node {
        private static final String SYMBOL = "!";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        private Node mChild;

        public NotNode(Token token) {
            super(token, 6);
        }

        public int addNode(Node node) {
            if (isLowerPrecedence(node)) {
                return NODE_NOT_ADDED;
            }

            if (mChild != null) {
                return NODE_NOT_ADDED;
            }

            if (node.assertType(Operand.TYPE_BOOLEAN)) {
                if (mChild == null) {
                    mChild = node;
                    return NODE_ADDED;
                }
                int result = mChild.addNodeRight(node);
                if (result == NODE_NOT_ADDED) {
                    // Must be rewriting the tree.
                    int rewriteResult = node.addNodeRight(mChild);
                    if (rewriteResult == NODE_ADDED) {
                        if (node.assertType(Operand.TYPE_BOOLEAN)) {
                            mChild = node;
                            return rewriteResult;
                        }
                        node.markError("Expected boolean.");
                        return NODE_ERROR;
                    }
                    return rewriteResult;
                }
                return result;
            }
            node.markError("Expected boolean.");
            return NODE_ERROR;
        }

        @Override
        public int addNodeLeft(Node node) {
            return addNode(node);
        }

        @Override
        public int addNodeRight(Node node) {
            return addNodeLeft(node);
        }

        @Override
        public void assertComplete() {
            if (mChild == null) {
                markError("Missing term.");
            } else {
                mChild.assertComplete();
            }
        }

        @Override
        public boolean assertType(int type) {
            if (mChild != null) {
                return mChild.assertType(type);
            }
            return (type & Operand.TYPE_BOOLEAN) != 0;
        }

        @Override
        public int getType() {
            if (mChild != null) {
                return mChild.getType();
            }
            return Operand.TYPE_BOOLEAN;
        }

        @Override
        public String printTree() {
            return "(" + toString() + ((mChild == null) ? "" : mChild.printTree()) + ")";
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getAssociativity() {
            return ASSOCIATIVITY_RIGHT;
        }

        @Override
        protected String pretty() {
            return toString() + ((mChild == null) ? "" : mChild.prettyPrint());
        }
    }

    static class OrNode extends BinaryOperator {
        private static final String SYMBOL = "or";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public OrNode(Token token) {
            super(token, 8);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_BOOLEAN;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected boolean type.";
        }
    }

    /**
     * A node which encapsulates the properties of the concatenation operator
     * and the addition operator.
     */
    static class PlusNode extends BinaryOperator {
        private static final int OPERATOR_ADDITION = 1;

        private static final int OPERATOR_CONCATENATION = 2;

        private static final int OPERATOR_UNDEFINED = 0;

        private static final String SYMBOL = "+";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        private int mOperator;

        public PlusNode(Token token) {
            super(token, 4);
        }

        @Override
        public int addNodeLeft(Node node) {
            if (!isHigherPrecedence(node)) {
                return NODE_NOT_ADDED;
            }

            if (mLeft != null) {
                // Left is full so try the right side.
                return addNodeRight(node);
            }

            if (node.assertType(Operand.TYPE_NUMBER | Operand.TYPE_STRING)) {
                updateOperatorKind(node);
                mLeft = node;
                return NODE_ADDED;
            }
            node.markError("Expected a number or string.");
            return NODE_ERROR;
        }

        @Override
        public int addNodeRight(Node node) {
            if (!isHigherPrecedence(node)) {
                return NODE_NOT_ADDED;
            }

            if (mRight != null) {
                int rightAddResult = mRight.addNodeRight(node);
                if (rightAddResult == NODE_NOT_ADDED) {
                    // Must be rewriting the tree.
                    int result = node.addNodeLeft(mRight);
                    if (result == NODE_ADDED) {
                        if (makeTypeAssertion(node)) {
                            updateOperatorKind(node);
                            mRight = node;
                            return result;
                        }
                        node.markError("Expected number type.");
                        return NODE_ERROR;
                    }
                    return result;
                }
                return rightAddResult;
            }

            if (node.assertType(Operand.TYPE_NUMBER)) {
                updateOperatorKind(node);
                mRight = node;
                return NODE_ADDED;
            }
            node.markError("Expected number type.");
            return NODE_ADDED;

        }

        @Override
        public boolean assertType(int type) {
            boolean r;

            if (mOperator == OPERATOR_ADDITION) {
                r = (Operand.TYPE_NUMBER & type) != 0;
            } else if (mOperator == OPERATOR_CONCATENATION) {
                r = (Operand.TYPE_STRING & type) != 0;
            } else {
                r = (type & (Operand.TYPE_NUMBER | Operand.TYPE_STRING)) != 0;
                if (mLeft != null) {
                    r = r && mLeft.assertType(type);
                    updateOperatorKind(mLeft);
                }

                if (mRight != null) {
                    r = r && mRight.assertType(type);
                    updateOperatorKind(mRight);
                }
            }

            return r;
        }

        @Override
        public int getType() {
            int operandTypes = orOperandTypes();
            if (operandTypes == Operand.TYPE_STRING) {
                return Operand.TYPE_STRING;
            }

            if ((operandTypes & Operand.TYPE_FLOAT) != 0) {
                // One of the operands is of a float type, so we will produce a
                // float.
                return Operand.TYPE_FLOAT;
            } else {
                // Both operands should be of an integer type. The result of
                // this operator should be integer addition.
                return operandTypes;
            }
        }

        @Override
        public void refresh() {
            mLeft.refresh();
            mRight.refresh();
            int left = mLeft.getType();
            int right = mRight.getType();
            if (left == Operand.TYPE_STRING) {
                mOperator = OPERATOR_CONCATENATION;
                mRight.assertType(Operand.TYPE_STRING);
                return;
            } else if (right == Operand.TYPE_STRING) {
                mOperator = OPERATOR_CONCATENATION;
                mLeft.assertType(Operand.TYPE_STRING);
                return;
            } else if ((left ^ Operand.TYPE_NUMBER) == 0 && (left & Operand.TYPE_NUMBER) != 0) {
                // Make sure the operand is not any other potential type in
                // addition to number.
                mOperator = OPERATOR_ADDITION;
                mRight.assertType(Operand.TYPE_NUMBER);
                return;
            } else if ((right ^ Operand.TYPE_NUMBER) == 0 && (right & Operand.TYPE_NUMBER) != 0) {
                // Make sure the operand is not any other potential type in
                // addition to number.
                mOperator = OPERATOR_ADDITION;
                mLeft.assertType(Operand.TYPE_NUMBER);
                return;
            }
            mOperator = OPERATOR_UNDEFINED;
            mLeft.assertType(Operand.TYPE_STRING | Operand.TYPE_NUMBER);
            updateOperatorKind(mLeft);
            mRight.assertType(Operand.TYPE_STRING | Operand.TYPE_NUMBER);
            updateOperatorKind(mRight);
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        /**
         * Just a helper for making the type assertion when we might have more
         * information about our operator kind.
         * 
         * @param node Node to assert type of.
         * @return True if assertion is successful.
         */
        private boolean makeTypeAssertion(Node node) {
            if (mOperator == OPERATOR_ADDITION) {
                return node.assertType(Operand.TYPE_NUMBER);
            } else if (mOperator == OPERATOR_CONCATENATION) {
                return node.assertType(Operand.TYPE_STRING);
            }
            boolean r = node.assertType(Operand.TYPE_NUMBER | Operand.TYPE_STRING);
            updateOperatorKind(node);
            return r;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_NUMBER | Operand.TYPE_STRING;
        }

        private void updateOperatorKind(Node node) {
            if ((node.getType() & Operand.TYPE_NUMBER) == 0) {
                mOperator = OPERATOR_CONCATENATION;
            } else if ((node.getType() & Operand.TYPE_STRING) == 0) {
                mOperator = OPERATOR_ADDITION;
            } else {
                mOperator = OPERATOR_UNDEFINED;
            }
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected string or number.";
        }
    }

    static class PositiveSignNode extends Node {
        private static final String SYMBOL = "+";

        public static boolean matches(Token token, Node lastGeneratedNode) {
            if (lastGeneratedNode == null) {
                // No last node so we must be unary right associative.
                return TextUtils.equals(SYMBOL, token.getString());
            } else if (lastGeneratedNode instanceof VariableNode
                    || lastGeneratedNode instanceof LiteralNode
                    || lastGeneratedNode instanceof EndSubBlockInstruction) {
                // a + ?, 1 + ?, ) + ?.
                return false;
            }
            // Last node was an operator so we must be unary right associative.
            return TextUtils.equals(SYMBOL, token.getString());
        }

        private Node mChild;

        public PositiveSignNode(Token token) {
            super(token, 2);
        }

        @Override
        public int addNodeLeft(Node node) {
            if (isLowerPrecedence(node)) {
                return NODE_NOT_ADDED;
            }

            if (mChild != null) {
                return NODE_NOT_ADDED;
            }

            if (node.assertType(Operand.TYPE_NUMBER)) {
                mChild = node;
                return NODE_ADDED;
            }
            node.markError("Expected a number.");
            return NODE_ERROR;
        }

        @Override
        public int addNodeRight(Node node) {
            return addNodeLeft(node);
        }

        @Override
        public void assertComplete() {
            if (mChild == null) {
                markError("Missing term.");
            } else {
                mChild.assertComplete();
            }
        }

        @Override
        public boolean assertType(int type) {
            if (mChild != null) {
                return mChild.assertType(type);
            }
            return (type & Operand.TYPE_NUMBER) != 0;
        }

        @Override
        public int getType() {
            if (mChild != null) {
                return mChild.getType();
            }
            return Operand.TYPE_NUMBER;
        }

        @Override
        public String printTree() {
            return "(" + toString() + ((mChild == null) ? "" : mChild.printTree()) + ")";
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getAssociativity() {
            return ASSOCIATIVITY_RIGHT;
        }

        @Override
        protected String pretty() {
            return toString() + ((mChild == null) ? "" : mChild.prettyPrint());
        }
    }

    static class SlashNode extends BinaryOperator {
        private static final String SYMBOL = "/";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        private Node mLeft;

        private Node mRight;

        public SlashNode(Token token) {
            super(token, 3);
        }

        @Override
        public boolean assertType(int type) {
            boolean r = (type & Operand.TYPE_NUMBER) != 0;

            // Add integers to bits because they always satisfy float
            // requirements.
            type |= Operand.TYPE_INTEGER;

            if (mLeft != null) {
                r = r && mLeft.assertType(type);
            }

            if (mRight != null) {
                r = r && mRight.assertType(type);
            }

            return r;
        }

        @Override
        public int getType() {
            if ((orOperandTypes() & Operand.TYPE_FLOAT) != 0) {
                // One of the operands is of a float type, so we will produce a
                // float.
                return Operand.TYPE_FLOAT;
            } else {
                // Both operands should be of an integer type. The result of
                // this operator should be integer division.
                return orOperandTypes();
            }
        }

        @Override
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getBaseType() {
            return Operand.TYPE_NUMBER;
        }

        @Override
        protected String getExpectedTypeErrorString() {
            return "Expected number type.";
        }
    }

    static class SubstringNode extends Node {
        private static final int STATE_COMPLETE = 5;

        private static final int STATE_WAITING_FOR_END = 4;

        private static final int STATE_WAITING_FOR_FIRST = 1;

        private static final int STATE_WAITING_FOR_SECOND = 3;

        private static final int STATE_WAITING_FOR_SEPARATOR = 2;

        private static final String SYMBOL = "[";

        private Node mLeft;

        private Node mLength;

        private Node mStartIndex;

        private int mState;

        public SubstringNode(Token token) {
            super(token, 5);
        }

        @Override
        public int addNodeLeft(Node node) {
            if (!isHigherPrecedence(node)) {
                return NODE_NOT_ADDED;
            }

            if (mLeft != null) {
                // Already have left operand.
                return NODE_NOT_ADDED;
            }

            if (node.assertType(Operand.TYPE_STRING)) {
                mLeft = node;
                return NODE_ADDED;
            }
            node.markError("Expected string operand.");
            return NODE_ERROR;
        }

        @Override
        public int addNodeRight(Node node) {
            if (node instanceof MiddleSubstringInstruction) {
                if (mState != STATE_WAITING_FOR_SEPARATOR) {
                    node.markError("Syntax error. Expected integer type, not '" + node + "'.");
                    return NODE_ERROR;
                }
                mState = STATE_WAITING_FOR_SECOND;
                // Actually, throw away node because it is not functional.
                return NODE_ADDED;
            }

            if (node instanceof EndSubstringInstruction) {
                if (mState != STATE_WAITING_FOR_END && mState != STATE_WAITING_FOR_SEPARATOR) {
                    node.markError("Syntax error. Expected integer type, not '" + node + "'.");
                    return NODE_ERROR;
                }
                mState = STATE_COMPLETE;
                return NODE_ADDED;
            }

            if (node.assertType(Operand.TYPE_INTEGER)) {
                if (mState == STATE_WAITING_FOR_FIRST) {
                    mStartIndex = node;
                    mState = STATE_WAITING_FOR_SEPARATOR;
                    return NODE_ADDED;
                } else if (mState == STATE_WAITING_FOR_SEPARATOR) {
                    int result = mStartIndex.addNodeRight(node);
                    if (result == NODE_NOT_ADDED) {
                        // Must be rewriting the tree.
                        int addStartResult = node.addNodeLeft(mStartIndex);
                        if (addStartResult == NODE_ADDED) {
                            if (node.assertType(Operand.TYPE_INTEGER)) {
                                mStartIndex = node;
                                return addStartResult;
                            }
                            node.markError("Expected integer.");
                            return NODE_ERROR;
                        }
                        return addStartResult;
                    }
                } else if (mState == STATE_WAITING_FOR_SECOND) {
                    mLength = node;
                    mState = STATE_WAITING_FOR_END;
                    return NODE_ADDED;
                } else if (mState == STATE_WAITING_FOR_END) {
                    int result = mLength.addNodeRight(node);
                    if (result == NODE_NOT_ADDED) {
                        // Must be rewriting the tree.
                        int addLengthResult = node.addNodeRight(mLength);
                        if (addLengthResult == NODE_ADDED) {
                            if (node.assertType(Operand.TYPE_INTEGER)) {
                                mLength = node;
                                return addLengthResult;
                            }
                            node.markError("Expected integer.");
                            return NODE_ERROR;
                        }
                        return addLengthResult;
                    }
                    node.markError("Unexpected token.");
                    return NODE_ERROR;
                }
            }

            node.markError("Expected integer.");
            return NODE_ERROR;
        }

        @Override
        public void assertComplete() {
            if (mLeft == null) {
                markError("Missing left term.");
            } else {
                mLeft.assertComplete();
            }
            if (mStartIndex == null) {
                markError("Missing index term.");
            } else {
                mStartIndex.assertComplete();
            }
            if (mLength != null) {
                mLength.assertComplete();
            }
        }

        @Override
        public boolean assertType(int type) {
            return (type & Operand.TYPE_STRING) != 0;
        }

        @Override
        public int getType() {
            return Operand.TYPE_STRING;
        }

        @Override
        public String printTree() {
            return "(" + ((mLeft == null) ? "" : mLeft.printTree()) + " ["
                    + ((mStartIndex == null) ? "" : mStartIndex.printTree())
                    + ((mLength == null) ? "]" : ", " + mLength.printTree() + "]") + ")";
        }

        @Override
        public String toString() {
            return SYMBOL
                    + mStartIndex
                    + ((mLength != null) ? MiddleSubstringInstruction.SYMBOL + mLength
                            + EndSubstringInstruction.SYMBOL : EndSubstringInstruction.SYMBOL);
        }

        @Override
        protected int getAssociativity() {
            return ASSOCIATIVITY_RIGHT;
        }

        @Override
        protected String pretty() {
            return ((mLeft == null) ? "" : mLeft.prettyPrint()) + " ["
                    + ((mStartIndex == null) ? "" : mStartIndex.prettyPrint())
                    + ((mLength == null) ? "]" : ", " + mLength.prettyPrint() + "]");
        }
    }

    static class VariableNode extends Node {
        static public boolean kindOf(Token token) {
            return (token.getType() & ExpressionCompiler.TOKEN_IDENTITY) != 0;
        }

        private String mName;

        public VariableNode(Token token) {
            super(token, 0);
            mName = token.getString();
        }

        @Override
        public void assertComplete() {
        }

        @Override
        public boolean assertType(int type) {
            mOperand.attemptRestrictType(type);
            return (mOperand.getType() & type) != 0;
        }

        @Override
        public int getInstruction() {
            int instruction = 0;
            if (mOperand == null) {
                instruction = PARSER_GENERATE_OPERAND;
            }
            return instruction;
        }

        @Override
        public int getType() {
            return mOperand.getType();
        }

        @Override
        public String printTree() {
            return toString();
        }

        @Override
        public String toString() {
            return mName;
        }

        @Override
        protected int getAssociativity() {
            return 0;
        }
    }
}
