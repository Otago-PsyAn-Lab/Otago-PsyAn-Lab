
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.ExpressionOperand;

import java.util.ArrayList;

public class ExpressionValue extends Operand implements ExpressionOperand {
    @Expose
    public String expression;

    @Expose
    public ArrayList<Long> variables;

    public ExpressionValue() {
        variables = new ArrayList<Long>();
    }

    public ExpressionValue(Operand operand) {
        super(operand);
        variables = new ArrayList<Long>();
    }

    @Override
    public String getValue() {
        return expression;
    }

    @Override
    public ArrayList<Long> getOperands() {
        return variables;
    }
}
