
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;

public class LiteralValue extends Operand implements LiteralOperand {
    @Expose
    public String valueString;

    public LiteralValue() {
    }

    public LiteralValue(Operand operand) {
        super(operand);
    }

    @Override
    public int getType() {
        return type;
    }

    @Override
    public String getValue() {
        return valueString;
    }
}
