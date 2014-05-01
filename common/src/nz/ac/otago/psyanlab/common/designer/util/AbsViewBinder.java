
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.designer.program.util.ProgramCallbacks;

import android.app.Activity;

public abstract class AbsViewBinder {
    protected final ProgramCallbacks mCallbacks;

    protected final Activity mActivity;

    public AbsViewBinder(Activity activity, ProgramCallbacks callbacks) {
        mActivity = activity;
        mCallbacks = callbacks;
    }

}
