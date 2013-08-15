
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;

public class TargetScreen {
    public static final int ORIENTATION_LANDSCAPE = 0x01;

    public static final int ORIENTATION_PORTRAIT = 0x02;

    @Expose
    public int height;

    @Expose
    public int orientation;

    @Expose
    public int width;

    @Expose
    public ArrayList<Double> pastScaleFactors;

    public TargetScreen() {
        pastScaleFactors = new ArrayList<Double>();
    }
}
