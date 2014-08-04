
package nz.ac.otago.psyanlab.common.util;

import nz.ac.otago.psyanlab.common.UserExperimentDelegateI;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class ExperimentDelegateHolder extends Fragment {
    public UserExperimentDelegateI delegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }
}
