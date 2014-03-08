
package nz.ac.otago.psyanlab.common.model.operand.kind;

import nz.ac.otago.psyanlab.common.model.util.OperandHolder;

import java.util.List;

public interface ExpressionOperand extends OperandHolder {
    List<Long> getOperands();
}
