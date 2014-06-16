
package nz.ac.otago.psyanlab.common.expression.expressions;

import nz.ac.otago.psyanlab.common.expression.TokenType;
import nz.ac.otago.psyanlab.common.model.util.Type;

public class OperatorExpression {
    public static final int ASSOCIATIVE_LEFT = 2;

    public static final int ASSOCIATIVE_RIGHT = 1;

    private final boolean mIsVirtual;

    protected final int mAssociativity;

    protected final TokenType mOperator;

    protected final int mPrecedence;

    public OperatorExpression(TokenType operator, int precedence, int associativity) {
        mOperator = operator;
        mPrecedence = precedence;
        mAssociativity = associativity;
        mIsVirtual = false;
    }

    public OperatorExpression(TokenType operator, int precedence, int associativity, boolean virtual) {
        mOperator = operator;
        mPrecedence = precedence;
        mAssociativity = associativity;
        mIsVirtual = virtual;
    }

    public int getAssociativity() {
        return mAssociativity;
    }

    public TokenType getOperator() {
        return mOperator;
    }

    public int getOperatorChildType(int typeMask) {
        switch (getOperator()) {
            case AND:
            case OR:
            case XOR:
            case BANG:
                return Type.TYPE_BOOLEAN;
            case PLUS:
                int r = (Type.TYPE_NUMBER | Type.TYPE_STRING) & typeMask;
                if (r == 0) {
                    return Type.TYPE_NUMBER | Type.TYPE_STRING;
                }
                return r;
            case ASTERISK:
            case CARET:
            case MINUS:
            case PERCENT:
            case SLASH:

            case LESS_THAN:
            case LESS_THAN_OR_EQUAL_TO:
            case MORE_THAN:
            case MORE_THAN_OR_EQUAL_TO:
                return Type.TYPE_NUMBER;
            case NOT_EQUALS:
            case EQUALS:
                return Type.TYPE_NON_ASSETS;

            default:
                return 0;
        }
    }

    // public boolean reprocess(int leftMask, int rightMask) {
    // switch (getOperator()) {
    // case AND:
    // case OR:
    // case XOR:
    // case BANG:
    // case PLUS:
    // case ASTERISK:
    // case CARET:
    // case MINUS:
    // case PERCENT:
    // case SLASH:
    // case LESS_THAN:
    // case LESS_THAN_OR_EQUAL_TO:
    // case MORE_THAN:
    // case MORE_THAN_OR_EQUAL_TO:
    // case EQUALS:
    // // Not same, but have intersection.
    // return leftMask != rightMask && (leftMask & rightMask) != 0;
    //
    // default:
    // return false;
    // }
    // }

    public int getOperatorResultType(int typeMask) {
        switch (getOperator()) {
            case AND:
            case OR:
            case XOR:
            case BANG:
                return Type.TYPE_BOOLEAN;
            case PLUS:
                return (Type.TYPE_NUMBER | Type.TYPE_STRING) & typeMask;
            case ASTERISK:
            case CARET:
            case MINUS:
            case PERCENT:
            case SLASH:
                return Type.TYPE_NUMBER & typeMask;

            case LESS_THAN:
            case LESS_THAN_OR_EQUAL_TO:
            case MORE_THAN:
            case MORE_THAN_OR_EQUAL_TO:
            case NOT_EQUALS:
            case EQUALS:
                return Type.TYPE_BOOLEAN;

            default:
                return 0;
        }
    }

    public int getPrecedence() {
        return mPrecedence;
    }

    public boolean isVirtual() {
        return mIsVirtual;
    }

}
