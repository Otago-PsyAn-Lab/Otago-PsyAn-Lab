
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.util.EventId;
import nz.ac.otago.psyanlab.common.model.util.MethodId;

import java.lang.reflect.Method;

/**
 * An internal pointer to objects stored in the experiment. This reference
 * stores the kind and id of an object so we know what kind of object we are
 * dealing with and can pull it from the experiment data using its id. Note, the
 * id is only unique within its kind.
 */
public class ExperimentObjectReference {
    /**
     * An object that emits events.
     */
    public static final int EMITS_EVENTS = -0x01;

    /**
     * An object that has methods which set a value (void return type).
     */
    public static final int HAS_SETTERS = 0x00;

    /**
     * An object which is a kind of asset.
     */
    public static final int KIND_ASSET = 0x01;

    /**
     * An object which describes how to store collected experiment records.
     */
    public static final int KIND_DATA_CHANNEL = 0x08;

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
     * An object which is a kind of scene.
     */
    public static final int KIND_SCENE = 0x07;

    public static ExperimentObjectFilter getFilter(int filter) {
        switch (filter) {
            case HAS_SETTERS:
                return new HasSettersFilter();
            case EMITS_EVENTS:
                return new EmitsEventsFilter();

            default:
                return new ReturnTypeFilter(filter);
                // throw new RuntimeException("Unknown filter type " + filter);
        }
    }

    /**
     * The object reference id which is unique within the object kind.
     */
    @Expose
    public long id;

    /**
     * The kind of the object. Use this to select which call to make in order to
     * pull the object from the experiment.
     */
    @Expose
    public int kind;

    public ExperimentObjectReference(int kind, long id) {
        this.kind = kind;
        this.id = id;
    }

    public static class EmitsEventsFilter implements ExperimentObjectFilter {
        @Override
        public boolean filter(ExperimentObject object) {
            Method[] methods = object.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.isAnnotationPresent(EventId.class)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static interface ExperimentObjectFilter {
        /**
         * Filter an object.
         * 
         * @return True if the object passes the filter.
         */
        boolean filter(ExperimentObject object);
    }

    public static class HasFloatGettersFilter implements ExperimentObjectFilter {
        @Override
        public boolean filter(ExperimentObject object) {
            Method[] methods = object.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getReturnType().equals(Float.TYPE)
                        && method.isAnnotationPresent(MethodId.class)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class HasIntGettersFilter implements ExperimentObjectFilter {
        @Override
        public boolean filter(ExperimentObject object) {
            Method[] methods = object.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getReturnType().equals(Integer.TYPE)
                        && method.isAnnotationPresent(MethodId.class)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class HasSettersFilter implements ExperimentObjectFilter {
        @Override
        public boolean filter(ExperimentObject object) {
            Method[] methods = object.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getReturnType().equals(Void.TYPE)
                        && method.isAnnotationPresent(MethodId.class)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class HasStringGettersFilter implements ExperimentObjectFilter {
        @Override
        public boolean filter(ExperimentObject object) {
            Method[] methods = object.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.getReturnType().equals(String.class)
                        && method.isAnnotationPresent(MethodId.class)) {
                    return true;
                }
            }
            return false;
        }
    }

    public static class ReturnTypeFilter implements ExperimentObjectFilter {
        private int mFilter;

        public ReturnTypeFilter(int filter) {
            mFilter = filter;
        }

        @Override
        public boolean filter(ExperimentObject object) {
            Method[] methods = object.getClass().getMethods();
            for (int i = 0; i < methods.length; i++) {
                Method method = methods[i];
                if (method.isAnnotationPresent(MethodId.class)) {
                    if ((mFilter & Operand.TYPE_BOOLEAN) != 0
                            && method.getReturnType().equals(Boolean.TYPE)) {
                        return true;
                    }
                    if ((mFilter & Operand.TYPE_STRING) != 0
                            && method.getReturnType().equals(String.class)) {
                        return true;
                    }
                    if ((mFilter & Operand.TYPE_INTEGER) != 0
                            && method.getReturnType().equals(Integer.TYPE)) {
                        return true;
                    }
                    if ((mFilter & Operand.TYPE_FLOAT) != 0
                            && method.getReturnType().equals(Float.TYPE)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }
}
