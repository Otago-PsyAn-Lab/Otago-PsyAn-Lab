
package nz.ac.otago.psyanlab.common.model;

import com.google.gson.annotations.Expose;

/**
 * An internal pointer to objects stored in the experiment. This reference
 * stores the kind and id of an object so we know what kind of object we are
 * dealing with and can pull it from the experiment data using its id. Note, the
 * id is only unique within its kind.
 */
public class ExperimentObjectReference {
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

    public boolean equals(ExperimentObjectReference other) {
        return kind == other.kind && id == other.id;
    }
}
