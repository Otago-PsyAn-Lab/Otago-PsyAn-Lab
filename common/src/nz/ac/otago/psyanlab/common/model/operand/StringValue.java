
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;

public class StringValue extends Operand implements LiteralOperand {
    @Expose
    public String value;

    public StringValue() {
    }

    @Override
    public int type() {
        return TYPE_STRING;
    }
}
