
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;

public class BooleanValue extends Operand implements LiteralOperand {
    @Expose
    public boolean value;

    public BooleanValue() {
    }

    @Override
    public int type() {
        return TYPE_BOOLEAN;
    }
}
