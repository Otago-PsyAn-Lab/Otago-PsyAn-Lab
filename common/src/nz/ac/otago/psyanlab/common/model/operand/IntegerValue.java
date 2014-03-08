
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
    
    @Override
    public String getValue() {
        return String.valueOf(value);
    }
}
