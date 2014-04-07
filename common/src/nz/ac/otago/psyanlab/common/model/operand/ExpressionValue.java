
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.ExpressionOperand;

import java.util.ArrayList;
import java.util.List;

public class ExpressionValue extends Operand implements ExpressionOperand {
    @Expose
    public String expression;

    @Expose
    public ArrayList<Long> operands;

    public ExpressionValue() {
        operands = new ArrayList<Long>();
    }

    public ExpressionValue(Operand operand) {
        super(operand);
        operands = new ArrayList<Long>();
    }

    @Override
    public String getValue() {
        return expression;
    }

    @Override
    public List<Long> getOperands() {
        return operands;
    }
}
