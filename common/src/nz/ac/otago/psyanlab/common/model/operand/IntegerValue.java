
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;

public class IntegerValue extends Operand implements LiteralOperand {
    @Expose
    public int value;

    public IntegerValue() {
    }

    @Override
    public int type() {
        return TYPE_INTEGER;
    }
}
