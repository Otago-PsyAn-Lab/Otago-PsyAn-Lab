
package nz.ac.otago.psyanlab.common.designer.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.subject.OptionsFragment.OnQuestionKindChangeListener;
import nz.ac.otago.psyanlab.common.designer.subject.SubjectFragment.Callbacks;
import nz.ac.otago.psyanlab.common.model.Question;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class EditQuestionDialogueFragment extends DialogFragment implements
        OnQuestionKindChangeListener {
    private static final String ARG_IS_NEW = "arg_is_new";

    private static final String ARG_QUESTION_ID = "arg_question_id";

    private static final String TAG_CHILD_FRAGMENT = "tag_child_fragment";

    /**
     * Get a new dialogue. The dialogue will create a new question item if the
     * question id is negative.
     * 
     * @param questionId Id of the question.
     * @return A new edit question dialogue fragment.
     */
    public static DialogFragment newDialogue(long questionId) {
        EditQuestionDialogueFragment f = new EditQuestionDialogueFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_QUESTION_ID, questionId);
        f.setArguments(args);
        return f;
    }

    public static DialogFragment newDialogue(long questionId, boolean isNew) {
        DialogFragment f = newDialogue(questionId);
        Bundle args = f.getArguments();
        args.putBoolean(ARG_IS_NEW, isNew);
        f.setArguments(args);
        return f;
    }

    private Callbacks mCallbacks;

    private View.OnClickListener mConfirmButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.putQuestion(mQuestionId, mFragment.getQuestion());
            dismiss();
        }
    };

    private View.OnClickListener mDiscardButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private long mQuestionId;

    private ViewHolder mViews;

    protected OptionsFragment mFragment;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogue_subject_stat_type, null);

        Bundle args = getArguments();
        mQuestionId = args.getLong(ARG_QUESTION_ID);
        mViews = new ViewHolder(view);
        mViews.initViews(args.getBoolean(ARG_IS_NEW));

        Question q = mCallbacks.getQuestion(mQuestionId);

        mFragment = (OptionsFragment)getChildFragmentManager()
                .findFragmentByTag(TAG_CHILD_FRAGMENT);
        if (mFragment == null) {
            mFragment = getOptionsFragment(mQuestionId, q);
            getChildFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, mFragment, TAG_CHILD_FRAGMENT).commit();
        }
        mFragment.setOnQuestionTypeChangeListener(this);

        return view;
    }

    @Override
    public void onQuestionKindChange(int newKind) {
        Question q = Question.getNewInstance(newKind);
        FragmentManager fragmentManager = getChildFragmentManager();
        fragmentManager.beginTransaction().remove(mFragment).commit();
        mFragment = getOptionsFragment(mQuestionId, q);
        mFragment.setOnQuestionTypeChangeListener(this);
        fragmentManager.beginTransaction()
                .add(R.id.fragment_container, mFragment, TAG_CHILD_FRAGMENT).commit();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    private OptionsFragment getOptionsFragment(long id, Question q) {
        switch (q.getKind()) {
            case Question.KIND_DROPDOWN:
            case Question.KIND_SINGLE_CHOICE:
            case Question.KIND_MULTI_CHOICE:
                return ManyOptionsQuestionFragment.newInstance(q);
            case Question.KIND_TOGGLE:
                return TwoOptionsQuestionFragment.newInstance(q);

            case Question.KIND_DATE:
            case Question.KIND_DATE_TIME:
            case Question.KIND_NUMBER:
            case Question.KIND_TEXT:
            case Question.KIND_TIME:
            default:
                return NoOptionsQuestionFragment.newInstance(q);
        }
    }

    public class ViewHolder {
        private Button mConfirm;

        private Button mDiscard;

        public ViewHolder(View view) {
            mConfirm = (Button)view.findViewById(R.id.confirm);
            mDiscard = (Button)view.findViewById(R.id.discard);
        }

        public void initViews(boolean isNew) {
            if (isNew) {
                getDialog().setTitle(R.string.title_new_question);

                mConfirm.setText(R.string.action_create);
            } else {
                getDialog().setTitle(R.string.title_edit_question);

                mConfirm.setText(R.string.action_confirm);
            }
            mConfirm.setOnClickListener(mConfirmButtonClickListener);
            mDiscard.setOnClickListener(mDiscardButtonClickListener);
        }

    }

}
