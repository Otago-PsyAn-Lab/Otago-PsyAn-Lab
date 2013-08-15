
package nz.ac.otago.psyanlab.common.model.operand;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.Operand;

public class CallValue extends Operand {
    @Expose
    public String object;

    @Expose
    public String method;

    @Expose
    public ArrayList<Operand> operands;

    public CallValue() {
        operands = new ArrayList<Operand>();
    }
}
