
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Rule {
    @Expose
    public String name;

    @Expose
    public ExperimentObjectReference triggerObject;

    @Expose
    public int triggerEvent;

    @Expose
    public Expression condition;

    @Expose
    public ArrayList<Long> actions;

    public Rule() {
        actions = new ArrayList<Long>();
        condition = new Expression();
        name = "New Rule";
    }
}
