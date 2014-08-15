
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

import nz.ac.otago.psyanlab.common.expression.Precedence;

/**
 * A ternary conditional expression like "a ? b : c".
 */
public class ConditionalExpression implements Expression {
    private final Expression mCondition;

    private final Expression mElseArm;

    private final Expression mThenArm;

    public ConditionalExpression(Expression condition, Expression thenArm, Expression elseArm) {
        mCondition = condition;
        mThenArm = thenArm;
        mElseArm = elseArm;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public Expression getCondition() {
        return mCondition;
    }

    public Expression getElseArm() {
        return mElseArm;
    }

    @Override
    public int getPrecedence() {
        return Precedence.CONDITIONAL;
    }

    public Expression getThenArm() {
        return mThenArm;
    }
}
