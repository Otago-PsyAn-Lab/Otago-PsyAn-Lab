
package nz.ac.otago.psyanlab.common.designer.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.subject.SubjectStatisticTypeDialogFragment.OptionsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class NoOptionsFragment extends Fragment implements OptionsFragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_type_no_options, container,
                false);
    }

    @Override
    public ArrayList<String> getOptions() {
        return null;
    }

    @Override
    public void setOptions(ArrayList<String> options) {
    }
}
