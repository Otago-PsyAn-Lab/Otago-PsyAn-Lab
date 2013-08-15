
package nz.ac.otago.psyanlab.common.designer.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.subject.SubjectFragment.Callbacks;
import nz.ac.otago.psyanlab.common.model.LandingPage;
import nz.ac.otago.psyanlab.common.model.Subject;
import nz.ac.otago.psyanlab.common.model.subject.Date;
import nz.ac.otago.psyanlab.common.model.subject.DateTime;
import nz.ac.otago.psyanlab.common.model.subject.Dropdown;
import nz.ac.otago.psyanlab.common.model.subject.MultiChoice;
import nz.ac.otago.psyanlab.common.model.subject.Number;
import nz.ac.otago.psyanlab.common.model.subject.SingleChoice;
import nz.ac.otago.psyanlab.common.model.subject.SubjectDetailWithOptions;
import nz.ac.otago.psyanlab.common.model.subject.Text;
import nz.ac.otago.psyanlab.common.model.subject.Time;
import nz.ac.otago.psyanlab.common.model.subject.Toggle;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;

public class SubjectStatisticTypeDialogFragment extends DialogFragment {
    private static final String TAG_OPTIONS_FRAGMENT = "tag_options_fragment";

    public static DialogFragment newDialog(int rowId, String title) {
        SubjectStatisticTypeDialogFragment f = new SubjectStatisticTypeDialogFragment();
        f.setRowId(rowId);
        f.setTitle(title);
        return f;
    }

    public OnItemSelectedListener mOnTypeSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (mWasOptionsFragmentPersisted) {
                mWasOptionsFragmentPersisted = false;
                return;
            }

            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            String opt = getResources().getStringArray(R.array.subject_stat_types)[position];
            Fragment optionsFragment;

            mViews.name.setInputType(InputType.TYPE_NULL);
            if (TextUtils.equals(opt, "Toggle")) {
                mViews.name.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                optionsFragment = new TwoOptionsFragment();
            } else if (TextUtils.equals(opt, "Dropdown") || TextUtils.equals(opt, "Single Choice")
                    || TextUtils.equals(opt, "Multiple Choice")) {
                mViews.name.setImeOptions(EditorInfo.IME_ACTION_DONE);
                optionsFragment = new ManyOptionsFragment();
            } else if (TextUtils.equals(opt, "Text") || TextUtils.equals(opt, "Number")
                    || TextUtils.equals(opt, "Date") || TextUtils.equals(opt, "DateTime")
                    || TextUtils.equals(opt, "Time")) {
                mViews.name.setImeOptions(EditorInfo.IME_ACTION_NEXT);
                optionsFragment = new HintOptionsFragment();
            } else {
                mViews.name.setImeOptions(EditorInfo.IME_ACTION_DONE);
                optionsFragment = new NoOptionsFragment();
            }
            mViews.name.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            if (mRow != null && mRow instanceof SubjectDetailWithOptions) {
                ((OptionsFragment)optionsFragment)
                        .setOptions(((SubjectDetailWithOptions)mRow).options);
            }
            ft.replace(R.id.options_container, optionsFragment, TAG_OPTIONS_FRAGMENT);
            ft.commit();
        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            Fragment fragment = new NoOptionsFragment();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.options_container, fragment, TAG_OPTIONS_FRAGMENT);
            ft.commit();
        }
    };

    private Callbacks mCallbacks;

    private View.OnClickListener mConfirmButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            storeConfiguration();
            dismiss();
        }
    };

    private View.OnClickListener mDismissButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    };

    private LandingPage mLandingPage;

    private TextWatcher mNameTextChangeListener = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            getDialog().setTitle(mTitle + " " + s.toString());
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    private Subject mRow;

    private int mRowId;

    private String mTitle;

    private ViewHolder mViews;

    private boolean mWasOptionsFragmentPersisted;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mWasOptionsFragmentPersisted = getChildFragmentManager().findFragmentByTag(
                TAG_OPTIONS_FRAGMENT) != null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogue_subject_stat_type, null);

        mViews = new ViewHolder(view);
        mViews.initViews();

        mLandingPage = mCallbacks.getLandingPage();
        if (mRowId >= 0) {
            mRow = mLandingPage.subjectDetails.get(mRowId);
        }

        mViews.setViewValues(mRow);

        String append = mViews.name.getText().toString();
        if (TextUtils.isEmpty(append)) {
            append = "New Statistic";
        }
        getDialog().setTitle(mTitle + " " + append);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    private void setRowId(int rowId) {
        mRowId = rowId;
    }

    protected void storeConfiguration() {
        OptionsFragment of = (OptionsFragment)getChildFragmentManager().findFragmentByTag(
                TAG_OPTIONS_FRAGMENT);

        String description = "";
        if (mRow != null) {
            description = mRow.text;
        }

        switch (mViews.typeSpinner.getSelectedItemPosition()) {
            case Date.ID:
                mRow = new Date();
                break;
            case DateTime.ID:
                mRow = new DateTime();
                break;
            case Dropdown.ID:
                mRow = new Dropdown(of.getOptions());
                break;
            case MultiChoice.ID:
                mRow = new MultiChoice(of.getOptions());
                break;
            case Number.ID:
                mRow = new Number();
                break;
            case SingleChoice.ID:
                mRow = new SingleChoice(of.getOptions());
                break;
            case Text.ID:
                mRow = new Text();
                break;
            case Time.ID:
                mRow = new Time();
                break;
            case Toggle.ID:
                mRow = new Toggle(of.getOptions());
                break;

            default:
                return;
        }

        mRow.required = mViews.required.isChecked();
        mRow.name = mViews.name.getText().toString();
        mRow.text = description;

        if (mRowId == -1) {
            mLandingPage.subjectDetails.add(mRow);
        } else {
            mLandingPage.subjectDetails.set(mRowId, mRow);
        }
        mCallbacks.storeLandingPage(mLandingPage);
    }

    public static interface OptionsFragment {
        public ArrayList<String> getOptions();

        public void setOptions(ArrayList<String> options);
    }

    public class ViewHolder {
        public Button confirm;

        public Button discard;

        public EditText name;

        public CheckBox required;

        public Spinner typeSpinner;

        public ViewHolder(View view) {
            required = (CheckBox)view.findViewById(R.id.required);
            typeSpinner = (Spinner)view.findViewById(R.id.type);
            name = (EditText)view.findViewById(R.id.name);
            confirm = (Button)view.findViewById(R.id.confirm);
            discard = (Button)view.findViewById(R.id.discard);

        }

        public void initViews() {
            typeSpinner.setOnItemSelectedListener(mOnTypeSelectedListener);
            typeSpinner.setSelection(8);
            name.addTextChangedListener(mNameTextChangeListener);
            confirm.setOnClickListener(mConfirmButtonClickListener);
            discard.setOnClickListener(mDismissButtonClickListener);
        }

        public void setViewValues(Subject row) {
            if (row == null) {
                return;
            }

            name.setText(row.name);
            typeSpinner.setSelection(row.getTypeId());
            required.setChecked(row.required);
        }
    }
}
