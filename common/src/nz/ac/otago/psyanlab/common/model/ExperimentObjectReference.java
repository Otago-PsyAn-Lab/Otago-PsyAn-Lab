
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

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
}
