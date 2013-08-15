
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

public abstract class Generator {
    @Expose
    public int end;

    @Expose
    public String name;

    @Expose
    public int start;

    public Generator() {
        name = "New Generator";
    }
}
