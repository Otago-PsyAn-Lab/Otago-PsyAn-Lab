
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Rule {
    @Expose
    public String name;

    @Expose
    public String triggerObject;

    @Expose
    public String triggerEvent;

    @Expose
    public Condition condition;

    @Expose
    public ArrayList<Long> actions;

    public Rule() {
        actions = new ArrayList<Long>();
        condition = new Condition();
        name = "New Rule";
    }
}
