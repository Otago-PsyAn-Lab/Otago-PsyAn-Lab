
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.operand.kind.CallOperand;
import nz.ac.otago.psyanlab.common.model.operand.kind.FloatOperand;

import java.util.ArrayList;

public class FloatCallValue extends FloatOperand implements CallOperand {
    @Expose
    public int actionMethod;

    @Expose
    public ExperimentObjectReference actionObject;

    @Expose
    public ArrayList<Long> operands;

    public FloatCallValue() {
        operands = new ArrayList<Long>();
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
    public ArrayList<Long> getOperands() {
        return operands;
    }
}
