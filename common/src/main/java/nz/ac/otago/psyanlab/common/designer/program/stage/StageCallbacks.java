
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.designer.util.PropIdPair;
import nz.ac.otago.psyanlab.common.model.Prop;

import android.widget.ArrayAdapter;

public interface StageCallbacks {
    void deleteProp(int propId);

    PropIdPair getProp(int id);

    ArrayAdapter<PropIdPair> getPropAdapter();

    int getStageHeight();

    int getStageMode();

    int getStageOrientation();

    int getStageWidth();

    /**
     * Refresh the stage to reflect the current set properties.
     */
    void refreshStage();

    void saveProp(int propId, Prop prop);

    void saveProp(Prop prop);

    void setStageOrientation(int orientation);

    long getNewPropKey();
}
