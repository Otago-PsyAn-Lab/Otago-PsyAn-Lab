
package nz.ac.otago.psyanlab.common.model.operand.kind;

import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.util.OperandHolder;

public interface CallOperand extends OperandHolder {
    int getMethod();

    ExperimentObjectReference getObject();
}
