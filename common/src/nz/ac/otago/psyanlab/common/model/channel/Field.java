
package nz.ac.otago.psyanlab.common.model.channel;

import com.google.gson.annotations.Expose;

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
}
