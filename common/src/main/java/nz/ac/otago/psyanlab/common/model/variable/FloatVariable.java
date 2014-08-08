
package nz.ac.otago.psyanlab.common.model.variable;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Variable;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.ParameterId;

public class FloatVariable extends Variable {
    @Expose
    float value;

    public FloatVariable() {}

    public FloatVariable(FloatVariable variable) {
        super(variable);
        value = variable.value;
    }

    public FloatVariable(Variable variable) {
        super(variable);
    }

    @MethodId(METHOD_SET_AND_USE)
    public float chainSetVariableValue(@ParameterId(PARAM_VALUE) float value) {
        return value;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }

    @MethodId(METHOD_GET)
    public float getVariableValue() {
        return value;
    }

    @MethodId(METHOD_SET)
    public void setVariableValue(@ParameterId(PARAM_VALUE) float value) {}
}
