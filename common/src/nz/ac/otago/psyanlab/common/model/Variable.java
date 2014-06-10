
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;

import android.content.Context;

public abstract class Variable extends ExperimentObject {
    protected static final int METHOD_GET = 0x03;

    protected static final int METHOD_SET = 0x02;

    protected static final int METHOD_SET_AND_USE = 0x01;

    protected static final int PARAM_VALUE = 0x01;

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public static NameResolverFactory getParameterNameFactory() {
        return new ParameterNameFactory();
    }

    @Expose
    String name;

    @Override
    public String getExperimentObjectName(Context context) {
        return name;
    }

    public String getName() {
        return name;
    }

    abstract public String getValue();

    @Override
    public int kind() {
        return ExperimentObject.KIND_VARIABLE;
    }

    public static class MethodNameFactory extends ExperimentObject.MethodNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case METHOD_GET:
                    return R.string.method_variable_get_value;
                case METHOD_SET:
                    return R.string.method_variable_set_value;
                case METHOD_SET_AND_USE:
                    return R.string.method_variable_get_and_use_value;
                default:
                    return super.getResId(lookup);
            }
        }
    }

    public static class ParameterNameFactory extends ExperimentObject.ParameterNameFactory {
        @Override
        public int getResId(int lookup) {
            switch (lookup) {
                case PARAM_VALUE:
                    return R.string.parameter_variable_value;
                default:
                    return super.getResId(lookup);
            }
        }
    }
}
