
package nz.ac.otago.psyanlab.common.model.operand.kind;

import nz.ac.otago.psyanlab.common.model.Operand;

public abstract class StringOperand extends Operand {
    @Override
    public int type() {
        return OPERAND_TYPE_STRING;
    }
}
