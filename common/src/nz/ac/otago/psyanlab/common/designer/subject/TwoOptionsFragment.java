
package nz.ac.otago.psyanlab.common.designer.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.subject.SubjectStatisticTypeDialogFragment.OptionsFragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;

public class TwoOptionsFragment extends Fragment implements OptionsFragment {
    private EditText mOptionOne;
    private ArrayList<String> mOptions;
    private EditText mOptionTwo;

    @Override
    public ArrayList<String> getOptions() {
        ArrayList<String> options = new ArrayList<String>();

        String option = mOptionOne.getText().toString();
        if (TextUtils.isEmpty(option)) {
            option = mOptionOne.getHint().toString();
        }
        options.add(option);

        option = mOptionTwo.getText().toString();
        if (TextUtils.isEmpty(option)) {
            option = mOptionTwo.getHint().toString();
        }
        options.add(option);
        return options;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_user_detail_type_two_options,
                container, false);
        mOptionOne = (EditText)v.findViewById(R.id.first_option);
        mOptionTwo = (EditText)v.findViewById(R.id.second_option);
        if (mOptions != null) {
            mOptionOne.setText(mOptions.get(0));
            mOptionTwo.setText(mOptions.get(1));
        }
        return v;
    }

    @Override
    public void setOptions(ArrayList<String> options) {
        mOptions = options;
    }
}
