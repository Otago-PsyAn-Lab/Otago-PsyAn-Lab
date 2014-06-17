
package nz.ac.otago.psyanlab.common.designer.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class TwoOptionsQuestionFragment extends OptionsFragment {
    public static OptionsFragment newInstance(Question q) {
        OptionsFragment f = new TwoOptionsQuestionFragment();
        return init(f, q);
    }

    private ViewHolder mViews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_question_with_two_options, container, false);
        return v;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViews = new ViewHolder(view);

        if (savedInstanceState == null) {
            mViews.setViewValues(mQuestion);
        }
    }

    @Override
    protected void loadQuestionValues() {
        super.loadQuestionValues();
        mQuestion.options.clear();
        mQuestion.options.add(mViews.getOptionOne());
        mQuestion.options.add(mViews.getOptionTwo());
    }

    public static class ViewHolder {
        private EditText mOptionOne;

        private EditText mOptionTwo;

        public ViewHolder(View view) {
            mOptionOne = (EditText)view.findViewById(R.id.option_one);
            mOptionTwo = (EditText)view.findViewById(R.id.option_two);
        }

        public String getOptionOne() {
            return mOptionOne.getText().toString();
        }

        public String getOptionTwo() {
            return mOptionTwo.getText().toString();
        }

        public void setViewValues(Question question) {
            mOptionOne.setText(question.options.get(0));
            mOptionTwo.setText(question.options.get(1));
        }
    }
}
