
package nz.ac.otago.psyanlab.common.model.operand.kind;

import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.util.OperandHolder;

import java.util.ArrayList;

public interface CallOperand extends OperandHolder {
    int getActionMethod();

    ExperimentObjectReference getActionObject();

    ArrayList<Long> getOperands();
}
