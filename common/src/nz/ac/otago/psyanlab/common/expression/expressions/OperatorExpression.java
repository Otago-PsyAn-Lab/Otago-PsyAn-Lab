
package nz.ac.otago.psyanlab.common.expression.expressions;

import nz.ac.otago.psyanlab.common.expression.TokenType;
import nz.ac.otago.psyanlab.common.model.Operand;

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

    public int getOperatorChildType() {
        switch (getOperator()) {
            case AND:
            case OR:
            case XOR:
            case BANG:
                return Operand.TYPE_BOOLEAN;
            case PLUS:
                return Operand.TYPE_NUMBER | Operand.TYPE_STRING;
            case ASTERISK:
            case CARET:
            case MINUS:
            case PERCENT:
            case SLASH:

            case LESS_THAN:
            case LESS_THAN_OR_EQUAL_TO:
            case MORE_THAN:
            case MORE_THAN_OR_EQUAL_TO:
                return Operand.TYPE_NUMBER;
            case EQUALS:
                return Operand.TYPE_NON_ASSETS;

            default:
                return 0;
        }
    }

    public int getOperatorResultType(int typeMask) {
        switch (getOperator()) {
            case AND:
            case OR:
            case XOR:
            case BANG:
                return Operand.TYPE_BOOLEAN;
            case PLUS:
                return (Operand.TYPE_NUMBER | Operand.TYPE_STRING) & typeMask;
            case ASTERISK:
            case CARET:
            case MINUS:
            case PERCENT:
            case SLASH:
                return Operand.TYPE_NUMBER & typeMask;

            case LESS_THAN:
            case LESS_THAN_OR_EQUAL_TO:
            case MORE_THAN:
            case MORE_THAN_OR_EQUAL_TO:
            case EQUALS:
                return Operand.TYPE_BOOLEAN;

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
