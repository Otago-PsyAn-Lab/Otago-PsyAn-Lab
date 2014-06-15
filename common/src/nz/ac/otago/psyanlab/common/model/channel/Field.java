
package nz.ac.otago.psyanlab.common.model.channel;

import com.google.gson.annotations.Expose;

import nz.ac.otago.psyanlab.common.model.util.Type;

public class Field {
    /**
     * Id used to track field changes when updating data channel references.
     */
    @Expose
    public int id;

    /**
     * Field name.
     */
    @Expose
    public String name;

    /**
     * The type of the field recorded as a bit mask.
     */
    @Expose
    public int type;

    public Field() {
    }

    @Override
    public String toString() {
        return "Field : " + id + " :: " + name + " :: " + Type.getTypeString(type);
    }
}
