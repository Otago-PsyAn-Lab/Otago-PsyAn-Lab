
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.util.MethodAdapter.MethodData;
import nz.ac.otago.psyanlab.common.model.util.EventData;
import nz.ac.otago.psyanlab.common.model.util.MethodId;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.Type;

import android.content.Context;

import java.lang.reflect.Method;
import java.util.SortedSet;

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
    public static final int KIND_ACTION = 0x01;

    /**
     * An object which is a kind of asset.
     */
    public static final int KIND_ASSET = 0x02;

    /**
     * An object which describes how to store collected experiment records.
     */
    public static final int KIND_DATA_CHANNEL = 0x03;

    /**
     * An virtual object which is a kind of event.
     */
    public static final int KIND_EVENT = 0x04;

    /**
     * An object which is a kind of experiment.
     */
    public static final int KIND_EXPERIMENT = 0x05;

    /**
     * An object which is a kind of generator.
     */
    public static final int KIND_GENERATOR = 0x06;

    /**
     * An object which is a kind of loop.
     */
    public static final int KIND_LOOP = 0x07;

    /**
     * An object which is a king of operand.
     */
    public static final int KIND_OPERAND = 0x08;

    /**
     * An object which is a kind of prop.
     */
    public static final int KIND_PROP = 0x09;

    /**
     * An object which is a kind of rule.
     */
    public static final int KIND_RULE = 0x0a;

    /**
     * An object which is a kind of scene.
     */
    public static final int KIND_SCENE = 0x0b;

    /**
     * An object which acts as a source of data.
     */
    public static final int KIND_SOURCE = 0x0c;

    /**
     * An object which is a kind of a global variable.
     */
    public static final int KIND_VARIABLE = 0x0a;

    public static NameResolverFactory getEventNameFactory() {
        return new EventNameFactory();
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
            case KIND_EVENT:
                return R.string.label_event;

            default:
                throw new RuntimeException("Unknown experiment object kind " + kind);
        }
    }

    @Expose
    public int tag;

    /**
     * A formatted, readable name for the object.
     * 
     * @param context Application context to pull i18n strings from.
     * @return I18n sensitive name of the object.
     */
    abstract public String getExperimentObjectName(Context context);

    public NameResolverFactory getMethodNameFactory() {
        return new MethodNameFactory();
    }

    public int getTag() {
        return tag;
    }

    /**
     * The kind of object this experiment object is.
     * 
     * @return The kind of object.
     */
    abstract public int kind();

    public void loadInMatchingMethods(int returnType, SortedSet<MethodData> out) {
        Class<? extends ExperimentObject> clazz = getClass();
        Method[] methods = clazz.getMethods();

        // Filter methods for those which register listeners for events.
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            MethodId annotation = method.getAnnotation(MethodId.class);
            if (annotation != null && returnTypeIntersects(method, returnType)) {
                MethodData data = new MethodData();
                data.id = annotation.value();
                data.method = method;
                out.add(data);
            }
        }
    }

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

    /**
     * Checks to see the given ored set of return types intersects with the
     * given method's return type.
     * 
     * @param method Method to test.
     * @param returnTypes Ored set of return types.
     * @return True if intersection.
     */
    private boolean returnTypeIntersects(Method method, int returnTypes) {
        if ((returnTypes & Type.TYPE_BOOLEAN) != 0 && method.getReturnType().equals(Boolean.TYPE)) {
            return true;
        }
        if ((returnTypes & Type.TYPE_INTEGER) != 0 && method.getReturnType().equals(Integer.TYPE)) {
            return true;
        }
        if ((returnTypes & Type.TYPE_FLOAT) != 0 && method.getReturnType().equals(Float.TYPE)) {
            return true;
        }
        if ((returnTypes & Type.TYPE_STRING) != 0 && method.getReturnType().equals(String.class)) {
            return true;
        }
        if (returnTypes == 0 && method.getReturnType().equals(Void.TYPE)) {
            return true;
        }
        return false;
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
