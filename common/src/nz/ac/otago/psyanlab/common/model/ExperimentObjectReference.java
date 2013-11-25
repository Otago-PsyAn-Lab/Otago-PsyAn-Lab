
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

public class ExperimentObjectReference {
    public static final int EMITS_EVENTS = 0x01;

    public static final int HAS_FLOAT_GETTERS = 0x02;

    public static final int HAS_INT_GETTERS = 0x03;

    public static final int HAS_SETTERS = 0x04;

    public static final int HAS_STRING_GETTERS = 0x05;

    public static final int KIND_ASSET = 0x01;

    public static final int KIND_EXPERIMENT = 0x02;

    public static final int KIND_GENERATOR = 0x03;

    public static final int KIND_LOOP = 0x04;

    public static final int KIND_PROP = 0x05;

    public static final int KIND_SCENE = 0x06;

    @Expose
    public long id;

    @Expose
    public int kind;

    public ExperimentObjectReference(int kind, long id) {
        this.kind = kind;
        this.id = id;
    }
}
