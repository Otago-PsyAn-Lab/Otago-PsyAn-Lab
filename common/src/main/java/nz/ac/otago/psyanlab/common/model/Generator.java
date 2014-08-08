package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

public abstract class Generator extends ExperimentObject {
    private static final int METHOD_GENERATE_NUMBER = 0x01;

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
        return ExperimentObject.KIND_GENERATOR;
    }

    @MethodId(METHOD_GENERATE_NUMBER)
    public int generateNumber() {
        return 0;
    }

    protected static class MethodNameFactory implements NameResolverFactory {
        @Override
        public String getName(Context context, int lookup) {
            switch (lookup) {
                case METHOD_GENERATE_NUMBER:
                    return context.getString(R.string.method_generator_generate_number);
                default:
                    return context.getString(R.string.method_missing_string);
            }
        }
    }
}
