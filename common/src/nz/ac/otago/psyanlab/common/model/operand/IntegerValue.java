
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.operand.kind.IntegerOperand;

public class IntegerValue extends IntegerOperand {
    @Expose
    public int value;

    public IntegerValue() {
    }

    public void setValue(int value) {
        this.value = value;
        name = String.valueOf(value);
    }
}
