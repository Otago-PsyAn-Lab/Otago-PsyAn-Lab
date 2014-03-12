
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.util.ExpressionCompiler.OperandCallbacks;
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
    private static final int MODE_INNER_BLOCK = 0x01;

    private static final int MODE_NORMAL = 0x00;

    protected static final int PARSER_BEGIN_BLOCK = 0X01;

    protected static final int PARSER_END_BLOCK = 0X04;

    protected static final int PARSER_ERROR = 0x100;

    protected static final int PARSER_GENERATE_OPERAND = 0x10;

    private OperandCallbacks mCallbacks;

    private ExpressionParser mInnerBlock;

    private Token mLastToken;

    private int mMode = MODE_NORMAL;

    private HashMap<String, Long> mOperandIds;

    private Node mRoot;

    private Stack<Node> stack;

    public ExpressionParser(OperandCallbacks callbacks, HashMap<String, Long> operandIds) {
        mCallbacks = callbacks;
        mOperandIds = operandIds;
    }

    public void addToken(Token token) {
        Node node = Node.newNode(token, mLastToken);
        mLastToken = token;
        int instruction = node.getInstruction();
        if ((instruction & PARSER_BEGIN_BLOCK) != 0) {
            // Open a new inner block.
            mInnerBlock = new ExpressionParser(mCallbacks, mOperandIds);
            mMode = MODE_INNER_BLOCK;
        } else if ((instruction & PARSER_END_BLOCK) != 0) {
            if (mMode != MODE_INNER_BLOCK) {
                token.markError("Unexpected end of grouping.");
            }
            // Close inner block and use its tree as our node.
            mMode = MODE_NORMAL;
            node = mInnerBlock.getRoot();
            mInnerBlock = null;
        } else if ((instruction & PARSER_GENERATE_OPERAND) != 0) {
            // Create and store operand.
            Operand operand = new StubOperand(node.toString());
            long operandId = mCallbacks.createOperand(operand);
            node.setOperand(operandId, operand);
            mOperandIds.put(node.toString(), operandId);
        }

        if (mMode == MODE_INNER_BLOCK) {
            mInnerBlock.addNode(node);
        } else {
            addNode(node);
        }
    }

    public HashMap<String, Long> getOperandIds() {
        return mOperandIds;
    }

    public Node getRoot() {
        return mRoot;
    }

    private void addNode(Node node) {
        if (mRoot == null) {
            mRoot = node;
            return;
        }
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
        public int getInstruction() {
            return PARSER_BEGIN_BLOCK;
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
            if (isLowerPrecedence(node)) {
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
            if (isLowerPrecedence(node)) {
                return NODE_NOT_ADDED;
            }

            if (mRight != null) {
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

            if (node.assertType(getBaseType())) {
                mRight = node;
                return NODE_ADDED;
            }
            node.markError(getExpectedTypeErrorString());
            return NODE_ADDED;
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
        public String toString() {
            return SYMBOL;
        }
    }

    static class EqualsNode extends BinaryOperator {
        private static final String SYMBOL = "=";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public EqualsNode(Token token) {
            super(token, 5);
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

    static class LessThanNode extends BinaryOperator {
        private static final String SYMBOL = "<";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public LessThanNode(Token token) {
            super(token, 5);
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
            }
        }

        @Override
        public int getType() {
            if ((mToken.getType() & ExpressionCompiler.TOKEN_NUMBER) != 0) {
                return mType;
            }
            return Operand.TYPE_STRING;
        }

        @Override
        public String toString() {
            return mValue;
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

    static class MoreThanNode extends BinaryOperator {
        private static final String SYMBOL = ">";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public MoreThanNode(Token token) {
            super(token, 5);
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

    static class NegativeNode extends Node {
        private static final String SYMBOL = "-";

        public static boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        private Node mChild;

        public NegativeNode(Token token) {
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
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getAssociativity() {
            return ASSOCIATIVITY_RIGHT;
        }
    }

    /**
     * Nodes are not necessarily complete in meaning.
     */
    static abstract class Node {
        protected static final int ASSOCIATIVITY_LEFT = 1;

        protected static final int ASSOCIATIVITY_RIGHT = 2;

        protected static final int NODE_ADDED = 1;

        protected static final int NODE_ERROR = 2;

        protected static final int NODE_NOT_ADDED = 0;

        public static Node newNode(Token token, Token lastToken) {
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
            } else if (EqualsNode.kindOf(token)) {
                node = new EqualsNode(token);
            } else if (NotEqualsNode.kindOf(token)) {
                node = new NotEqualsNode(token);
            } else if ((lastToken == null || PositiveNode.kindOf(lastToken))
                    && PositiveNode.kindOf(token)) {
                node = new PositiveNode(token);
            } else if ((lastToken == null || PositiveNode.kindOf(lastToken))
                    && PositiveNode.kindOf(token)) {
                node = new PositiveNode(token);
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

        protected Operand mOperand;

        protected long mOperandId;

        protected int mPrecedence;

        protected Token mToken;

        public Node(Token token, int precedence) {
            mToken = token;
            mPrecedence = precedence;
        }

        public int addNodeLeft(Node node) {
            return NODE_NOT_ADDED;
        }

        public int addNodeRight(Node node) {
            return NODE_NOT_ADDED;
        }

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
            mToken.markError(string);
        }

        public void refresh() {

        }

        public void setOperand(long operandId, Operand operand) {
            mOperandId = operandId;
            mOperand = operand;
        }

        abstract public String toString();

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
    }

    static class NotEqualsNode extends BinaryOperator {
        private static final String SYMBOL = "<>";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        public NotEqualsNode(Token token) {
            super(token, 5);
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

    static class NotNode extends Node {
        private static final String SYMBOL = "!";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        private Node mChild;

        public NotNode(Token token) {
            super(token, 6);
        }

        @Override
        public int addNodeLeft(Node node) {
            if (isLowerPrecedence(node)) {
                return NODE_NOT_ADDED;
            }

            if (mChild != null) {
                return NODE_NOT_ADDED;
            }

            if (node.assertType(Operand.TYPE_BOOLEAN)) {
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
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getAssociativity() {
            return ASSOCIATIVITY_RIGHT;
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

    static class PlusNode extends Node {
        private static final int OPERATOR_ADDITION = 1;

        private static final int OPERATOR_CONCATENATION = 2;

        private static final int OPERATOR_UNDEFINED = 0;

        private static final String SYMBOL = "+";

        static public boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        private Node mLeft;

        private int mOperator;

        private Node mRight;

        public PlusNode(Token token) {
            super(token, 4);
        }

        @Override
        public int addNodeLeft(Node node) {
            if (isLowerPrecedence(node)) {
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
            if (isLowerPrecedence(node)) {
                return NODE_NOT_ADDED;
            }

            if (mRight != null) {
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

        /**
         * Or the left and right operands types together. Handle the cases where
         * some may be null.
         * 
         * @return Or'ed types.
         */
        private int orOperandTypes() {
            if (mRight == null) {
                if (mLeft == null) {
                    return Operand.TYPE_NUMBER | Operand.TYPE_STRING;
                }
                return mRight.getType();
            }
            if (mLeft == null) {
                return mLeft.getType();
            }
            return mLeft.getType() | mRight.getType();
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
    }

    static class PositiveNode extends Node {
        private static final String SYMBOL = "+";

        public static boolean kindOf(Token token) {
            return TextUtils.equals(SYMBOL, token.getString());
        }

        private Node mChild;

        public PositiveNode(Token token) {
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
        public String toString() {
            return SYMBOL;
        }

        @Override
        protected int getAssociativity() {
            return ASSOCIATIVITY_RIGHT;
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
            super(token, 2);
        }

        @Override
        public int addNodeLeft(Node node) {
            if (isLowerPrecedence(node)) {
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
                } else if (mState == STATE_WAITING_FOR_SECOND) {
                    mLength = node;
                    mState = STATE_WAITING_FOR_END;
                    return NODE_ADDED;
                }
                node.markError("Unexpected additional integer term.");
                return NODE_ERROR;
            }

            node.markError("Expected integer.");
            return NODE_ERROR;
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
        public String toString() {
            return mName;
        }

        @Override
        protected int getAssociativity() {
            return 0;
        }
    }
}
