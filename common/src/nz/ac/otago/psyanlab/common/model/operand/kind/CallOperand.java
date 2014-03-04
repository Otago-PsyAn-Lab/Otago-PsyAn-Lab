
package nz.ac.otago.psyanlab.common.model.operand.kind;

import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.Operand;

import java.util.ArrayList;

public interface CallOperand {
    int getActionMethod();

    ExperimentObjectReference getActionObject();

    ArrayList<Operand> getOperands();
}
