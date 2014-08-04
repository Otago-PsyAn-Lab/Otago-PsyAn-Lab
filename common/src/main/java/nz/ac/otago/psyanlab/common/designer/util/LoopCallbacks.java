
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.LoopDataChangeListener;
import nz.ac.otago.psyanlab.common.model.Loop;

public interface LoopCallbacks {

    long addLoop(Loop loop);

    void deleteLoop(long id);

    void addLoopDataChangeListener(LoopDataChangeListener listener);

    Loop getLoop(long loopId);

    ProgramComponentAdapter<Loop> getLoopAdapter();

    void removeLoopDataChangeListener(LoopDataChangeListener listener);

    void putLoop(long id, Loop loop);

}
