
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;

import android.content.Context;

import java.util.ArrayList;

public class Scene implements ExperimentControl {
    public static final int ORIENTATION_LANDSCAPE = 0;

    public static final int ORIENTATION_PORTRAIT = 1;

    @Expose
    public String name;

    @Expose
    public ArrayList<Long> props;

    @Expose
    public ArrayList<Long> rules;

    @Expose
    public int orientation = -1;

    @Expose
    public int stageWidth = -1;

    @Expose
    public int stageHeight = -1;

    public Scene() {
        props = new ArrayList<Long>();
        rules = new ArrayList<Long>();
    }

    @Override
    public String getClassName(Context context) {
        return context.getString(R.string.format_scene_class_name, name);
    }
}
