
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;
import nz.ac.otago.psyanlab.common.model.util.Type;

public class BooleanValue extends Operand implements LiteralOperand {
    @Expose
    public boolean value;

    public BooleanValue() {
        type = Type.TYPE_BOOLEAN;
    }

    public BooleanValue(Operand operand) {
        super(operand);
        type = Type.TYPE_BOOLEAN;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }
}
