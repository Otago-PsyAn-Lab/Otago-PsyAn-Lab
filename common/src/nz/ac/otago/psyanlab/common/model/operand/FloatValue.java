
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;
import nz.ac.otago.psyanlab.common.model.util.Type;

public class FloatValue extends Operand implements LiteralOperand {
    @Expose
    public float value;

    public FloatValue() {
        type = Type.TYPE_FLOAT;
    }

    public FloatValue(Operand operand) {
        super(operand);
        type = Type.TYPE_FLOAT;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }
}
