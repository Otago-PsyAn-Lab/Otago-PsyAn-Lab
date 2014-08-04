
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Program {
    @Expose
    public ArrayList<Long> loops;

    public Program() {
        loops = new ArrayList<Long>();
    }
}
