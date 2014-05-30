
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

import java.util.ArrayList;

public class Scene implements ExperimentObject {
    public static final int ORIENTATION_LANDSCAPE = 0;

    public static final int ORIENTATION_PORTRAIT = 1;

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Expose
    public String name;

    @Expose
    public int orientation = -1;

    @Expose
    public ArrayList<Long> props;

    @Expose
    public ArrayList<Long> rules;

    @Expose
    public int stageHeight = -1;

    @Expose
    public int stageWidth = -1;

    public Scene() {
        props = new ArrayList<Long>();
        rules = new ArrayList<Long>();
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.format_scene_class_name, name);
    }

    @Override
    public int kind() {
        return ExperimentObjectReference.KIND_SCENE;
    }

    protected static class EventNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                default:
                    return R.string.event_missing_string;
            }
        }
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
