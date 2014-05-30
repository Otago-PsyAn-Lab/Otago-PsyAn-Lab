
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

public abstract class Generator implements ExperimentObject {
    @Expose
    public int end;

    @Expose
    public String name;

    @Expose
    public int start;

    public Generator() {
        name = "New Generator";
    }

    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.format_generator_class_name, name);
    }

    @Override
    public int kind() {
        return ExperimentObjectReference.KIND_GENERATOR;
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
