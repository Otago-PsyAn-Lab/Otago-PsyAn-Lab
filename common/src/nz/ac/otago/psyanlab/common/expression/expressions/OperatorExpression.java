
package nz.ac.otago.psyanlab.common.expression.expressions;

import nz.ac.otago.psyanlab.common.expression.TokenType;

public class OperatorExpression {
    public static final int ASSOCIATIVE_LEFT = 2;

    public static final int ASSOCIATIVE_RIGHT = 1;

    protected final int mAssociativity;

    protected final TokenType mOperator;

    protected final int mPrecedence;

    public OperatorExpression(TokenType operator, int precedence, int associativity) {
        mOperator = operator;
        mPrecedence = precedence;
        mAssociativity = associativity;
    }

    public int getAssociativity() {
        return mAssociativity;
    }

    public TokenType getOperator() {
        return mOperator;
    }

    public int getPrecedence() {
        return mPrecedence;
    }

}
