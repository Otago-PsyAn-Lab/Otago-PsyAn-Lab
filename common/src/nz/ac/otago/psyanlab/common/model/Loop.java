
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;

import android.content.Context;

import java.util.ArrayList;

public class Loop implements ExperimentObject {
    @Expose
    public ArrayList<Long> generators;

    @Expose
    public int iterations = 1;

    @Expose
    public String name;

    @Expose
    public ArrayList<Long> scenes;

    public Loop() {
        scenes = new ArrayList<Long>();
        generators = new ArrayList<Long>();
    }

    public boolean contains(long sceneId) {
        for (Long id : scenes) {
            if (id == sceneId) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getPrettyName(Context context) {
        return context.getString(R.string.format_loop_class_name, name);
    }

    @Override
    public int kind() {
        return ExperimentObjectReference.KIND_LOOP;
    }
}
