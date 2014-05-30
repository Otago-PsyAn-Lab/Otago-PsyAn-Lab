
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

import java.util.ArrayList;

public class Loop implements ExperimentObject {
    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

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
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.format_loop_class_name, name);
    }

    @Override
    public int kind() {
        return ExperimentObjectReference.KIND_LOOP;
    }

    protected static class MethodNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                default:
                    return R.string.method_missing_string;
            }
        }
    }
}
