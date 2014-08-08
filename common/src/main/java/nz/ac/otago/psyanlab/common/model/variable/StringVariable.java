package nz.ac.otago.psyanlab.common.model.variable;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Variable;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.ParameterId;

public class StringVariable extends Variable {
    @Expose
    String value;

    public StringVariable() {
    }

    public StringVariable(StringVariable variable) {
        super(variable);
        value = variable.value;
    }

    public StringVariable(Variable variable) {
        super(variable);
    }

    @MethodId(METHOD_SET_AND_USE)
    public String chainSetVariableValue(@ParameterId(PARAM_VALUE) String value) {
        return value;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }

    @MethodId(METHOD_GET)
    public String getVariableValue() {
        return value;
    }

    @MethodId(METHOD_SET)
    public void setVariableValue(@ParameterId(PARAM_VALUE) String value) {

    }
}
