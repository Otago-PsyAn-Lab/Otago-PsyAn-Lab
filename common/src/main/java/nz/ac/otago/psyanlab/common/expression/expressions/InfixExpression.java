
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

/**
 * A binary arithmetic expression like "a + b" or "c ^ d".
 */
public class InfixExpression extends OperatorExpression implements Expression {
    private final Expression mLeft;

    private final Expression mRight;

    public InfixExpression(Expression left, TokenType operator, Expression right, int precedence,
            int associativity) {
        super(operator, precedence, associativity);
        mLeft = left;
        mRight = right;
    }

    public InfixExpression(Expression left, TokenType operator, Expression right, int precedence,
            int associativity, boolean isVirtual) {
        super(operator, precedence, associativity, isVirtual);
        mLeft = left;
        mRight = right;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public Expression getLeft() {
        return mLeft;
    }

    public Expression getRight() {
        return mRight;
    }
}
