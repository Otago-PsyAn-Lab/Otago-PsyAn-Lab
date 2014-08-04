
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

import java.util.ArrayList;

public class Loop extends ExperimentObject {
    protected static final int METHOD_FINISH_LOOP = 0x101;

    protected static final int METHOD_GET_SELECTED_ROW = 0x104;

    protected static final int METHOD_GET_STEP = 0x102;

    protected static final int METHOD_GET_TOTAL_ITERATIONS = 0x103;

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
    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    @Override
    public int kind() {
        return ExperimentObject.KIND_LOOP;
    }

    @MethodId(METHOD_FINISH_LOOP)
    public void stubFinishLoop() {
    }

    @MethodId(METHOD_GET_SELECTED_ROW)
    public int stubGetSelectedRow() {
        return 0;
    }

    @MethodId(METHOD_GET_STEP)
    public int stubGetStep() {
        return 0;
    }

    @MethodId(METHOD_GET_TOTAL_ITERATIONS)
    public int stubGetTotalIterations() {
        return 0;
    }

    protected static class MethodNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case METHOD_FINISH_LOOP:
                    return R.string.method_finish_loop;
                case METHOD_GET_SELECTED_ROW:
                    return R.string.method_get_selected_row;
                case METHOD_GET_STEP:
                    return R.string.method_get_step;
                case METHOD_GET_TOTAL_ITERATIONS:
                    return R.string.method_get_total_iterations;

                default:
                    return R.string.method_missing_string;
            }
        }
    }
}
