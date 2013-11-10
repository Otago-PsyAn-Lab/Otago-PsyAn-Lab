
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;

import android.content.Context;

import java.util.ArrayList;

public class Loop implements ExperimentControl {
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

    @Override
    public String getClassName(Context context) {
        return context.getString(R.string.format_loop_class_name, name);
    }

    public boolean contains(long sceneId) {
        for (Long id : scenes) {
            if (id == sceneId) {
                return true;
            }
        }
        return false;
    }
}
