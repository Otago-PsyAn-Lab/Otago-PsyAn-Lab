
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.operand.kind.StringOperand;

public class StringValue extends StringOperand {
    @Expose
    public String value;

    public StringValue() {
    }

    public void setValue(String value) {
        this.value = value;
        name = value;
    }
}
