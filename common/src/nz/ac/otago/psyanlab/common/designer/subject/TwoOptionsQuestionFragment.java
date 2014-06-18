
package nz.ac.otago.psyanlab.common.designer.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
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
            mViews.setViewValues(getActivity(), mQuestion);
        }
    }

    @Override
    protected void loadQuestionValues() {
        super.loadQuestionValues();

        String optionOne = mViews.getOptionOne();
        if (!TextUtils.isEmpty(optionOne)) {
            if (mQuestion.options.size() == 0) {
                mQuestion.options.add(optionOne);
            } else {
                mQuestion.options.set(0, optionOne);
            }
        } else {
            if (mQuestion.options.size() == 0) {
                mQuestion.options.add(getString(R.string.default_entry_option_yes));
            } else {
                mQuestion.options.set(0, getString(R.string.default_entry_option_yes));
            }
        }
        String optionTwo = mViews.getOptionTwo();
        if (!TextUtils.isEmpty(optionTwo)) {
            if (mQuestion.options.size() == 1) {
                mQuestion.options.add(optionTwo);
            } else {
                mQuestion.options.set(1, optionTwo);
            }
        } else {
            if (mQuestion.options.size() == 1) {
                mQuestion.options.add(getString(R.string.default_entry_option_no));
            } else {
                mQuestion.options.set(1, getString(R.string.default_entry_option_no));
            }
        }
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

        public void setViewValues(Context context, Question question) {
            if (question.options.size() > 1) {
                mOptionTwo.setText(question.options.get(1));
                mOptionOne.setText(question.options.get(0));
            } else if (question.options.size() == 1) {
                mOptionOne.setText(question.options.get(0));
            }
        }
    }
}
