
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
