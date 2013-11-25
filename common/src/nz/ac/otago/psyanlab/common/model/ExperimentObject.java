
package nz.ac.otago.psyanlab.common.model;

import android.content.Context;

public interface ExperimentObject {
    String getPrettyName(Context context);

    int kind();
}
