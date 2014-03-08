
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;

public class FloatValue extends Operand implements LiteralOperand {
    @Expose
    public float value;

    public FloatValue() {
        type = TYPE_FLOAT;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }
}
