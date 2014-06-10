
package nz.ac.otago.psyanlab.common.model;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.util.EventData;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.Type;

import android.content.Context;

import java.lang.reflect.Method;

public abstract class ExperimentObject {
    /**
     * An object that emits events.
     */
    public static final int EMITS_EVENTS = -0x01;

    /**
     * An object that has methods which set a value (void return type).
     */
    public static final int HAS_SETTERS = 0x00;

    /**
     * An object which is a kind of action.
     */
    public static final int KIND_ACTION = 0x0c;

    /**
     * An object which is a kind of asset.
     */
    public static final int KIND_ASSET = 0x01;

    /**
     * An object which describes how to store collected experiment records.
     */
    public static final int KIND_DATA_CHANNEL = 0x08;

    /**
     * A generated object which makes event data accessible.
     */
    public static final int KIND_EVENT = 0x09;

    /**
     * An object which is a kind of experiment.
     */
    public static final int KIND_EXPERIMENT = 0x02;

    /**
     * An object which is a kind of generator.
     */
    public static final int KIND_GENERATOR = 0x03;

    /**
     * An object which is a kind of loop.
     */
    public static final int KIND_LOOP = 0x04;

    /**
     * An object which is a king of operand.
     */
    public static final int KIND_OPERAND = 0x05;

    /**
     * An object which is a kind of prop.
     */
    public static final int KIND_PROP = 0x06;

    /**
     * An object which is a kind of rule.
     */
    public static final int KIND_RULE = 0x0d;

    /**
     * An object which is a kind of scene.
     */
    public static final int KIND_SCENE = 0x07;

    /**
     * An object which acts as a source of data.
     */
    public static final int KIND_SOURCE = 0x0b;

    /**
     * An object which is a kind of a global variable.
     */
    public static final int KIND_VARIABLE = 0x0a;

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
    }

    public static NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public static NameResolverFactory getParameterNameFactory() {
        return new ParameterNameFactory();
    }

    public static int kindToResId(int kind) {
        switch (kind) {
            case KIND_ACTION:
                return R.string.label_action;
            case KIND_ASSET:
                return R.string.label_asset;
            case KIND_DATA_CHANNEL:
                return R.string.label_data_channel;
            case KIND_EVENT:
                return R.string.label_event;
            case KIND_EXPERIMENT:
                return R.string.label_experiment;
            case KIND_GENERATOR:
                return R.string.label_generator;
            case KIND_LOOP:
                return R.string.label_loop;
            case KIND_OPERAND:
                return R.string.label_operand;
            case KIND_PROP:
                return R.string.label_prop;
            case KIND_RULE:
                return R.string.label_rule;
            case KIND_SCENE:
                return R.string.label_scene;
            case KIND_SOURCE:
                return R.string.label_data_source;

            default:
                throw new RuntimeException("Unknown experiment object kind " + kind);
        }
    }

    /**
     * A formatted, readable name for the object.
     * 
     * @param context Application context to pull i18n strings from.
     * @return I18n sensitive name of the object.
     */
    abstract public String getExperimentObjectName(Context context);

    /**
     * The kind of object this experiment object is.
     * 
     * @return The kind of object.
     */
    abstract public int kind();

    public boolean satisfiesFilter(int filter) {
        switch (filter) {
            case HAS_SETTERS: {
                Method[] methods = getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    if (method.getReturnType().equals(Void.TYPE)
                            && method.isAnnotationPresent(MethodId.class)) {
                        return true;
                    }
                }
                return false;
            }
            case EMITS_EVENTS: {
                Method[] methods = getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    if (method.isAnnotationPresent(EventData.class)) {
                        return true;
                    }
                }
                return false;
            }
            default: {
                Method[] methods = getClass().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    Method method = methods[i];
                    if (method.isAnnotationPresent(MethodId.class)) {
                        if ((filter & Type.TYPE_BOOLEAN) != 0
                                && method.getReturnType().equals(Boolean.TYPE)) {
                            return true;
                        }
                        if ((filter & Type.TYPE_STRING) != 0
                                && method.getReturnType().equals(String.class)) {
                            return true;
                        }
                        if ((filter & Type.TYPE_INTEGER) != 0
                                && method.getReturnType().equals(Integer.TYPE)) {
                            return true;
                        }
                        if ((filter & Type.TYPE_FLOAT) != 0
                                && method.getReturnType().equals(Float.TYPE)) {
                            return true;
                        }
                    }
                }
                return false;
            }
        }
    }

    public static class EventNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            return R.string.event_missing_string;
        }
    }

    public static class MethodNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            return R.string.method_missing_string;
        }
    }

    public static class ParameterNameFactory implements NameResolverFactory {
        @Override
        public int getResId(int lookup) {
            return R.string.parameter_missing_string;
        }
    }
}
