
package nz.ac.otago.psyanlab.common.model;

import nz.ac.otago.psyanlab.common.R;

import android.content.Context;

public class TouchMotionEvent extends ExperimentObject {
    @Override
    public String getExperimentObjectName(Context context) {
        return context.getString(R.string.label_touch_event);
    }

    @Override
    public int kind() {
        return ExperimentObject.KIND_EVENT;
    }

}
