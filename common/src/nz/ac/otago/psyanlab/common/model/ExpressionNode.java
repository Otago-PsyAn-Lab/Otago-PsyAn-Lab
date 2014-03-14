
package nz.ac.otago.psyanlab.common.model;

import nz.ac.otago.psyanlab.common.designer.util.Token;
import nz.ac.otago.psyanlab.common.model.node.AndNode;
import nz.ac.otago.psyanlab.common.model.node.DivisionNode;
import nz.ac.otago.psyanlab.common.model.node.EndSubstringInstruction;
import nz.ac.otago.psyanlab.common.model.node.EqualityNode;
import nz.ac.otago.psyanlab.common.model.node.ErrorNode;
import nz.ac.otago.psyanlab.common.model.node.FalseNode;
import nz.ac.otago.psyanlab.common.model.node.InEqualityNode;
import nz.ac.otago.psyanlab.common.model.node.LessThanNode;
import nz.ac.otago.psyanlab.common.model.node.LessThanOrEqualToNode;
import nz.ac.otago.psyanlab.common.model.node.LiteralNode;
import nz.ac.otago.psyanlab.common.model.node.MoreThanNode;
import nz.ac.otago.psyanlab.common.model.node.MoreThanOrEqualToNode;
import nz.ac.otago.psyanlab.common.model.node.MultiplicationNode;
import nz.ac.otago.psyanlab.common.model.node.NegativeNode;
import nz.ac.otago.psyanlab.common.model.node.NotNode;
import nz.ac.otago.psyanlab.common.model.node.OrNode;
import nz.ac.otago.psyanlab.common.model.node.PlusNode;
import nz.ac.otago.psyanlab.common.model.node.PositiveNode;
import nz.ac.otago.psyanlab.common.model.node.SeparatorInstruction;
import nz.ac.otago.psyanlab.common.model.node.SubstringNode;
import nz.ac.otago.psyanlab.common.model.node.SubtractionNode;
import nz.ac.otago.psyanlab.common.model.node.TrueNode;
import nz.ac.otago.psyanlab.common.model.node.VariableNode;
import nz.ac.otago.psyanlab.common.model.node.XorNode;

/**
 * A node in an expression tree.
 */
public abstract class ExpressionNode {

    public static final int ASSOCIATIVE_LEFT = 1;

    public static final int ASSOCIATIVE_RIGHT = 0;

    public static ExpressionNode newNode(Token token, ExpressionNode lastGeneratedNode) {
        ExpressionNode node;
        if (AndNode.kindOf(token)) {
            node = new AndNode();
        } else if (XorNode.kindOf(token)) {
            node = new XorNode();
        } else if (OrNode.kindOf(token)) {
            node = new OrNode();
        } else if (NotNode.kindOf(token)) {
            node = new NotNode();
        } else if (TrueNode.kindOf(token)) {
            node = new TrueNode();
        } else if (FalseNode.kindOf(token)) {
            node = new FalseNode();
        } else if (LiteralNode.kindOf(token)) {
            node = new LiteralNode(token);
        } else if (LessThanNode.kindOf(token)) {
            node = new LessThanNode();
        } else if (MoreThanNode.kindOf(token)) {
            node = new MoreThanNode();
        } else if (LessThanOrEqualToNode.kindOf(token)) {
            node = new LessThanOrEqualToNode();
        } else if (MoreThanOrEqualToNode.kindOf(token)) {
            node = new MoreThanOrEqualToNode();
        } else if (EqualityNode.kindOf(token)) {
            node = new EqualityNode();
        } else if (InEqualityNode.kindOf(token)) {
            node = new InEqualityNode();
        } else if (NegativeNode.matches(token, lastGeneratedNode)) {
            node = new NegativeNode();
        } else if (PositiveNode.matches(token, lastGeneratedNode)) {
            // We know when we are a sign operator when we haven't already
            // seen a token or when that token was not a literal, identity,
            // or block.
            node = new PositiveNode();
        } else if (PlusNode.kindOf(token)) {
            node = new PlusNode();
        } else if (SubtractionNode.kindOf(token)) {
            node = new SubtractionNode();
        } else if (MultiplicationNode.kindOf(token)) {
            node = new MultiplicationNode();
        } else if (DivisionNode.kindOf(token)) {
            node = new DivisionNode();
        } else if (SubstringNode.kindOf(token)) {
            node = new SubstringNode();
        } else if (SeparatorInstruction.kindOf(token)) {
            node = new SeparatorInstruction();
        } else if (EndSubstringInstruction.kindOf(token)) {
            node = new EndSubstringInstruction();
        } else if (VariableNode.kindOf(token)) {
            // Do variables last to ensure keywords are parsed.
            node = new VariableNode(token);
        } else {
            node = new ErrorNode(token);
        }
        return node;
    }

    private Object mEvalResult;

    private int mPrecedence;

    private String mSymbol;

    /**
     * Track the number of times evaluate has been called so we can cache the
     * value for all links, but no more.
     */
    private int mTimesEvalCalled;

    /**
     * The number of times this node has been linked (plus 1).
     */
    private int mTimesLinked = 1;

    private Token mToken;

    private boolean mVirtual = false;

    protected int mType;

    public ExpressionNode(String symbol, int precedence) {
        mSymbol = symbol;
        mPrecedence = precedence;
    }

    public ExpressionNode(String symbol, int precendence, Token token) {
        this(symbol, precendence);
        mToken = token;
    }

    /**
     * Add a node from the left side of this.
     * 
     * @param node Node to be added.
     * @return New node of this tree.
     */
    public abstract ExpressionNode addNodeFromLeft(ExpressionNode node) throws ParseException;

    /**
     * Add a node from the right side of this.
     * 
     * @param node Node to be added.
     * @return New node of this tree.
     */
    public abstract ExpressionNode addNodeFromRight(ExpressionNode node) throws ParseException;

    final public Object evaluate() {
        if (mTimesEvalCalled == 0) {
            mEvalResult = evaluateImplementation();
        }
        mTimesEvalCalled = (mTimesEvalCalled + 1) % mTimesLinked;

        return mEvalResult;
    }

    public int getAssociativity() {
        return ASSOCIATIVE_LEFT;
    }

    public int getPrecedence() {
        return mPrecedence;
    }

    public Token getToken() {
        return mToken;
    }

    public boolean isOperator() {
        return true;
    }

    public void markLinked() {
        mTimesLinked += 1;
    }

    public abstract String printGraph();

    public void setVirtual(boolean virtual) {
        mVirtual = virtual;
    }

    public abstract String toString();

    protected abstract Object evaluateImplementation();

    protected String getSymbol() {
        return mSymbol;
    }

    protected boolean isHigherPrecedence(ExpressionNode node) {
        // Lower values have a 'higher' precedence, so we have to reverse
        // the inequality comparison. Which is why we use a helper method to
        // hide it all.
        return node.getPrecedence() < mPrecedence;
    }

    protected boolean isLowerPrecedence(ExpressionNode node) {
        // Lower values have a 'higher' precedence, so we have to reverse
        // the inequality comparison. Which is why we use a helper method to
        // hide it all.
        return node.getPrecedence() > mPrecedence;
    }

    protected boolean isVirtual() {
        return mVirtual;
    }

    @SuppressWarnings("serial")
    public static class ParseException extends Exception {
        public ParseException(String message) {
            super(message);
        }
    }
}
