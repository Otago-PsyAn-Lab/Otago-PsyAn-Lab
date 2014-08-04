
package nz.ac.otago.psyanlab.common.designer.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NoOptionsQuestionFragment extends OptionsFragment {
    public static OptionsFragment newInstance(Question q) {
        OptionsFragment f = new NoOptionsQuestionFragment();
        return init(f, q);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_question_with_no_options, container, false);
    }
}
