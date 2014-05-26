
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.model.Prop;

import java.util.ArrayList;

public interface PropCallbacks {
    Prop getProp(long id);

    ArrayList<Prop> getPropsArray(long stageId);
}
