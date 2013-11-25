
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.util.EventMethod;

import java.lang.reflect.Method;

public class ExperimentObjectReference {
    /**
     * An object that emits events.
     */
    public static final int EMITS_EVENTS = 0x01;

    /**
     * An object that has methods that return floating point numbers.
     */
    public static final int HAS_FLOAT_GETTERS = 0x02;

    /**
     * An object that has methods that return integers.
     */
    public static final int HAS_INT_GETTERS = 0x03;

    /**
     * An object that has methods which set a value (void return type).
     */
    public static final int HAS_SETTERS = 0x04;

    /**
     * An object that has methods that return strings.
     */
    public static final int HAS_STRING_GETTERS = 0x05;

    /**
     * An object which is a kind of asset.
     */
    public static final int KIND_ASSET = 0x01;

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
     * An object which is a kind of prop.
     */
    public static final int KIND_PROP = 0x05;

    /**
     * An object which is a kind of scene.
     */
    public static final int KIND_SCENE = 0x06;

    public static ExperimentObjectFilter getFilter(int filter) {
        switch (filter) {
            case EMITS_EVENTS:
                return new EmitsEventsFilter();

            default:
                throw new RuntimeException("Unknown filter type " + filter);
        }
    }

    /**
     * The object reference id which is unique within the object kind.
     */
    @Expose
    public long id;

    /**
     * The kind of the object.
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
                if (methods[i].isAnnotationPresent(EventMethod.class)) {
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
                if (methods[i].getReturnType().equals(Float.TYPE)) {
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
                if (methods[i].getReturnType().equals(Integer.TYPE)) {
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
                if (methods[i].getReturnType().equals(Void.TYPE)) {
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
                if (methods[i].getReturnType().equals(String.class)) {
                    return true;
                }
            }
            return false;
        }
    }
}
