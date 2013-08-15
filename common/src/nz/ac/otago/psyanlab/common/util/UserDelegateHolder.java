
package nz.ac.otago.psyanlab.common.util;

import nz.ac.otago.psyanlab.common.UserDelegateI;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class UserDelegateHolder extends Fragment {
    public UserDelegateI delegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);
    }
}
