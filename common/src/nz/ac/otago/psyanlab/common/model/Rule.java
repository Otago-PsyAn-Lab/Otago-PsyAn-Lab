
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Rule {
    @Expose
    public ArrayList<Long> actions;

    /**
     * The condition which must evaluate to true for this rule to execute.
     * Really an operand object id.
     */
    @Expose
    public long conditionId;

    @Expose
    public String name;

    @Expose
    public int triggerEvent;

    @Expose
    public ExperimentObjectReference triggerObject;

    public Rule() {
        actions = new ArrayList<Long>();
        name = "New Rule";
        conditionId = -1;
    }
}
