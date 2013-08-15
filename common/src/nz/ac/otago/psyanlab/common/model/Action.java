
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Action {
    @Expose
    public String method;

    @Expose
    public String object;

    @Expose
    public ArrayList<Operand> operands;

    @Expose
    public String name;

    public Action() {
        operands = new ArrayList<Operand>();
    }
}
