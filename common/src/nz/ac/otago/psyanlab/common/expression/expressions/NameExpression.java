
package nz.ac.otago.psyanlab.common.expression.expressions;

import nz.ac.otago.psyanlab.common.expression.Precedence;

/**
 * A simple variable name expression like "abc".
 */
public class NameExpression implements Expression {
    private final String mName;
    
    private long mOperandId = -1;

    public NameExpression(String name) {
        mName = name;
    }

    @Override
    public void accept(ExpressionVisitor visitor) {
        visitor.visit(this);
    }

    public String getName() {
        return mName;
    }

    @Override
    public int getPrecedence() {
        return Precedence.IDENTITY;
    }

    public void setOperand(long operandId) {
        mOperandId = operandId;
    }
}
