
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class Scene {
    @Expose
    public String name;

    @Expose
    public ArrayList<Long> props;

    @Expose
    public ArrayList<Long> rules;

    @Expose
    public int orientation = 0;

    public Scene() {
        props = new ArrayList<Long>();
        rules = new ArrayList<Long>();
    }
}
