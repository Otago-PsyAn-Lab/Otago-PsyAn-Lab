
package nz.ac.otago.psyanlab.common.model;

import android.content.Context;

public interface ExperimentObject {
    String getExperimentObjectName(Context context);

    int kind();
}
