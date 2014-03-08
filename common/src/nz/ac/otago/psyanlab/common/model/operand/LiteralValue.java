
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;

public class LiteralValue extends Operand implements LiteralOperand {
    @Expose
    public String valueString;

    public LiteralValue() {
    }

    @Override
    public int type() {
        return type;
    }
}
