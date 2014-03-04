
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.operand.kind.FloatOperand;

public class FloatValue extends FloatOperand {
    @Expose
    public float value;

    public FloatValue() {
    }

    public void setValue(float value) {
        this.value = value;
        name = String.valueOf(value);
    }
}
