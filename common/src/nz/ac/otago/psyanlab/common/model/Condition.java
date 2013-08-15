
package nz.ac.otago.psyanlab.common.model;

import java.util.ArrayList;

import com.google.gson.annotations.Expose;

public class Condition {
    @Expose
    public String formula;

    @Expose
    public ArrayList<Operand> operands;

    public Condition() {
        operands = new ArrayList<Operand>();
    }
}
