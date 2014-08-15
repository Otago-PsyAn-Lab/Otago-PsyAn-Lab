
/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
 */

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
