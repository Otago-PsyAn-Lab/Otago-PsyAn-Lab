
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.CallOperand;
import nz.ac.otago.psyanlab.common.model.operand.kind.StringOperand;

import java.util.ArrayList;

public class StringCallValue extends StringOperand implements CallOperand {
    @Expose
    public int actionMethod;

    @Expose
    public ExperimentObjectReference actionObject;

    @Expose
    public ArrayList<Operand> operands;

    public StringCallValue() {
        operands = new ArrayList<Operand>();
    }

    @Override
    public int getActionMethod() {
        return actionMethod;
    }

    @Override
    public ExperimentObjectReference getActionObject() {
        return actionObject;
    }

    @Override
    public ArrayList<Operand> getOperands() {
        return operands;
    }
}
