
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.ExpressionOperand;

import java.util.HashMap;

public class ExpressionValue extends Operand implements ExpressionOperand {
    @Expose
    public String expression;

    @Expose
    public HashMap<String, Long> operands;

    @Expose
    public int operandType;

    public ExpressionValue() {
        operands = new HashMap<String, Long>();
    }

    @Override
    public String getName() {
        return expression;
    }

    @Override
    public HashMap<String, Long> getOperands() {
        return operands;
    }

    @Override
    public int type() {
        return operandType;
    }
}
