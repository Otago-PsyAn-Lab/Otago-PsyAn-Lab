
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;

public class FloatValue extends Operand {
    @Expose
    public float value;

    public FloatValue() {
    }
}
