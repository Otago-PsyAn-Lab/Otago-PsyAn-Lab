
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Action {
    @Expose
    public int actionMethod;

    @Expose
    public ExperimentObjectReference actionObject;

    @Expose
    public String method;

    @Expose
    public String name;

    @Expose
    public String object;

    @Expose
    public ArrayList<Operand> operands;

    public Action() {
        operands = new ArrayList<Operand>();
    }
}
