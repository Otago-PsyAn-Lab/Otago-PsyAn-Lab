
package nz.ac.otago.psyanlab.common.model.operand;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;

public class StringValue extends Operand {
    @Expose
    public String value;

    public StringValue() {
    }
}
