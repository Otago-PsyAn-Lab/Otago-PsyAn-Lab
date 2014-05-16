
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;

public class IntegerValue extends Operand implements LiteralOperand {
    @Expose
    public int value;

    public IntegerValue() {
        type = TYPE_INTEGER;
    }

    public IntegerValue(Operand operand) {
        super(operand);
        type = TYPE_INTEGER;
    }

    @Override
    public String getValue() {
        return String.valueOf(value);
    }

    @Override
    public boolean attemptRestrictType(int type) {
        if (type == TYPE_INTEGER) {
            this.type = type;
            return true;
        }
        return super.attemptRestrictType(type);
    }
}
