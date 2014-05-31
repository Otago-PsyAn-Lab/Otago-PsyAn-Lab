
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;
import nz.ac.otago.psyanlab.common.model.util.Type;

public class IntegerValue extends Operand implements LiteralOperand {
    @Expose
    public int value;

    public IntegerValue() {
        type = Type.TYPE_INTEGER;
    }

    public IntegerValue(Operand operand) {
        super(operand);
        type = Type.TYPE_INTEGER;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }
}
