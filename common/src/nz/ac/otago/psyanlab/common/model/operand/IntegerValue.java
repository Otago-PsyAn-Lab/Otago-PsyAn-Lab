
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;

public class IntegerValue extends Operand {
    @Expose
    public int value;

    public IntegerValue() {
    }
}
