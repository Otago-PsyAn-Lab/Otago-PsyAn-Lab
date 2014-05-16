
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;

public class StringValue extends Operand implements LiteralOperand {
    @Expose
    public String value;

    public StringValue() {
        type = TYPE_STRING;
    }

    public StringValue(Operand operand) {
        super(operand);
        type = TYPE_STRING;
    }

    @Override
    public String getValue() {
        return '"' + value + '"';
    }

    @Override
    public boolean attemptRestrictType(int type) {
        if (type == TYPE_STRING) {
            this.type = type;
            return true;
        }
        return super.attemptRestrictType(type);
    }
}
