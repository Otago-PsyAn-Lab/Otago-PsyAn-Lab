
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Loop {
    @Expose
    public String name;

    @Expose
    public int iterations = 1;

    @Expose
    public ArrayList<Long> scenes;

    @Expose
    public ArrayList<Long> generators;

    public Loop() {
        scenes = new ArrayList<Long>();
        generators = new ArrayList<Long>();
    }
}
