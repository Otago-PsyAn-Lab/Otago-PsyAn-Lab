
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.ExperimentObjectReference;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.CallOperand;

import java.util.ArrayList;

public class CallValue extends Operand implements CallOperand {
    public static final int INVALID_METHOD = -1;

    @Expose
    public int method;

    @Expose
    public ExperimentObjectReference object;

    @Expose
    public ArrayList<Long> parameters = new ArrayList<Long>();

    @Expose
    public StubOperand originalStub;

    public CallValue() {
    }

    public CallValue(Operand operand) {
        super(operand);
        method = INVALID_METHOD;

        if (operand instanceof StubOperand) {
            originalStub = (StubOperand)operand;
        }
    }

    @Override
    public int getMethod() {
        return method;
    }

    @Override
    public ExperimentObjectReference getObject() {
        return object;
    }

    @Override
    public ArrayList<Long> getOperands() {
        return parameters;
    }

    @Override
    public int getType() {
        return type;
    }
}
