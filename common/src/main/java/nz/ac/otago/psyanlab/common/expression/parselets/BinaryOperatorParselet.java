
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

package nz.ac.otago.psyanlab.common.expression.parselets;

import nz.ac.otago.psyanlab.common.expression.Parser;
import nz.ac.otago.psyanlab.common.expression.Token;
import nz.ac.otago.psyanlab.common.expression.expressions.Expression;
import nz.ac.otago.psyanlab.common.expression.expressions.InfixExpression;
import nz.ac.otago.psyanlab.common.expression.expressions.OperatorExpression;

/**
 * Generic infix parselet for a binary arithmetic operator. The only difference
 * when parsing, "+", "-", "*", "/", and "^" is precedence and associativity, so
 * we can use a single parselet class for all of those.
 */
public class BinaryOperatorParselet implements InfixParselet {
    private final boolean mIsRight;

    private final int mPrecedence;

    public BinaryOperatorParselet(int precedence, boolean isRight) {
        mPrecedence = precedence;
        mIsRight = isRight;
    }

    @Override
    public int getPrecedence() {
        return mPrecedence;
    }

    @Override
    public Expression parse(Parser parser, Expression left, Token token) {
        // To handle right-associative operators like "^", we allow a slightly
        // lower precedence when parsing the right-hand side. This will let a
        // parselet with the same precedence appear on the right, which will
        // then
        // take *this* parselet's result as its left-hand argument.
        Expression right = parser.parseExpression(mPrecedence - (mIsRight ? 1 : 0));

        return new InfixExpression(left, token.getType(), right, mPrecedence,
                mIsRight ? OperatorExpression.ASSOCIATIVE_RIGHT
                        : OperatorExpression.ASSOCIATIVE_LEFT);
    }
}
