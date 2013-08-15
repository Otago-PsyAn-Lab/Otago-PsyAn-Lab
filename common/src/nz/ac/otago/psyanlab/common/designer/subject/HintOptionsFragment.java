
package nz.ac.otago.psyanlab.common.designer.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.subject.SubjectStatisticTypeDialogFragment.OptionsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

public class HintOptionsFragment extends Fragment implements OptionsFragment {
    private EditText mHint;
    private ArrayList<String> mOptions;

    @Override
    public ArrayList<String> getOptions() {
        ArrayList<String> list = new ArrayList<String>();
        list.add(mHint.getText().toString());
        return list;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_type_hint_option,
                container, false);
        mHint = (EditText)v.findViewById(R.id.hint);
        if (mOptions != null) {
            mHint.setText(mOptions.get(0));
        }
        return v;
    }

    @Override
    public void setOptions(ArrayList<String> options) {
        mOptions = options;
    }
}
