
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

public class Action {
    @Expose
    public int actionMethod;

    @Expose
    public long operandId;

    @Expose
    public String name;

    public Action() {
    }
}
