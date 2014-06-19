
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.prop.Button;
import nz.ac.otago.psyanlab.common.model.prop.Image;
import nz.ac.otago.psyanlab.common.model.prop.Text;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

public class EditPropDialogueFragment extends DialogFragment {

    private static final String ARG_ID = "arg_id";

    private static final String ARG_IS_DIALOGUE = "arg_is_dialogue";

    private static final int INVALID_ID = -1;

    protected static final String TAG_PROPERTIES_FRAGMENT = "tag_properties_fragment";

    public static EditPropDialogueFragment newAddDialogue() {
        return newEditDialogue(INVALID_ID);
    }

    public static EditPropDialogueFragment newEditDialogue(int propId) {
        EditPropDialogueFragment f = new EditPropDialogueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, propId);
        args.putBoolean(ARG_IS_DIALOGUE, true);
        f.setArguments(args);
        return f;
    }

    public static EditPropDialogueFragment newInstance(int propId) {
        EditPropDialogueFragment f = new EditPropDialogueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, propId);
        f.setArguments(args);
        return f;
    }

    public OnClickListener mConfirmListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            doSave();
            dismiss();
        }
    };

    public OnClickListener mDeleteListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.deleteProp(mPropId);
            dismiss();
        }
    };

    public OnClickListener mDiscardListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            getDialog().cancel();
        }
    };

    public TextWatcher mNameChangedListener = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mProp.name = s.toString();
            if (!mIsDialogue) {
                mCallbacks.saveProp(mPropId, mProp);
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    public OnItemSelectedListener mOnTypeSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
            if (mWasOptionsFragmentPersisted) {
                mWasOptionsFragmentPersisted = false;
                return;
            }
            final String kind = getResources().getStringArray(R.array.prop_types)[position];

            mViews.name.setInputType(InputType.TYPE_NULL);

            mProp = getConfiguredProp();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            if (TextUtils.equals(kind, "Text")) {
                mProp = new Text(getActivity(), mProp);
            } else if (TextUtils.equals(kind, "Image")) {
                mProp = new Image(getActivity(), mProp);
            } else if (TextUtils.equals(kind, "Button")) {
                mProp = new Button(getActivity(), mProp);
            } else {
                if (mPropertiesFragment != null) {
                    ft.remove(mPropertiesFragment);
                    ft.commit();
                }
                return;
            }

            mViews.setViewValues(mProp);
            mPropertiesFragment = EditPropPropertiesFragment.newInstance(mProp);

            mViews.name.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

            ft.replace(R.id.prop_properties_container, mPropertiesFragment, TAG_PROPERTIES_FRAGMENT);
            ft.commit();

        }

        @Override
        public void onNothingSelected(AdapterView<?> arg0) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.remove(mPropertiesFragment);
            ft.commit();
        }
    };

    private StageCallbacks mCallbacks;

    private boolean mIsDialogue;

    private Prop mProp;

    private int mPropId = INVALID_ID;

    private ViewHolder mViews;

    protected EditPropPropertiesFragment mPropertiesFragment;

    protected boolean mWasOptionsFragmentPersisted;

    public void doSave() {
        mProp = getConfiguredProp();
        if (mPropId == INVALID_ID) {
            mCallbacks.saveProp(mProp);
        } else {
            mCallbacks.saveProp(mPropId, mProp);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof StageCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (StageCallbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogue_edit_prop, null);

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_ID)) {
                mPropId = args.getInt(ARG_ID, INVALID_ID);
            }

            mIsDialogue = args.getBoolean(ARG_IS_DIALOGUE, false);
        }

        Prop prop = null;
        if (mPropId != INVALID_ID) {
            prop = mCallbacks.getProp(mPropId).getProp();
        }

        mViews = new ViewHolder(view);
        mViews.setViewValues(prop);
        mViews.initViews();

        mPropertiesFragment = (EditPropPropertiesFragment)getChildFragmentManager()
                .findFragmentByTag(TAG_PROPERTIES_FRAGMENT);
        if (mPropertiesFragment == null && prop != null) {
            mPropertiesFragment = EditPropPropertiesFragment.newInstance(prop);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.prop_properties_container, mPropertiesFragment, TAG_PROPERTIES_FRAGMENT);
            ft.commit();
        }

        if (mIsDialogue) {
            getDialog().setTitle(
                    mPropId == INVALID_ID ? R.string.title_new_prop : R.string.title_edit_prop);
        }

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getDialog().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
    }

    protected Prop getConfiguredProp() {
        if (mPropertiesFragment != null) {
            mProp = mViews.getConfiguredProp(mPropertiesFragment.getConfiguredProp());
            mViews.getConfiguredProp(mProp);
        }
        return mProp;
    }

    public class ViewHolder {
        public View buttonBar;

        public android.widget.Button confirm;

        public android.widget.Button delete;

        public android.widget.Button discard;

        public EditText height;

        public EditText name;

        public Spinner type;

        public EditText width;

        public EditText xPos;

        public EditText yPos;

        public ViewHolder(View view) {
            name = (EditText)view.findViewById(R.id.name);
            type = (Spinner)view.findViewById(R.id.type);
            xPos = (EditText)view.findViewById(R.id.xPos);
            yPos = (EditText)view.findViewById(R.id.yPos);
            width = (EditText)view.findViewById(R.id.width);
            height = (EditText)view.findViewById(R.id.height);
            buttonBar = view.findViewById(R.id.button_bar);
            discard = (android.widget.Button)view.findViewById(R.id.discard);
            delete = (android.widget.Button)view.findViewById(R.id.delete);
            confirm = (android.widget.Button)view.findViewById(R.id.confirm);
        }

        public Prop getConfiguredProp(Prop prop) {
            prop.name = name.getText().toString();

            try {
                prop.xPos = Integer.valueOf(xPos.getText().toString());
            } catch (NumberFormatException e) {
                // Bad input here can be ignored.
            }

            try {
                prop.yPos = Integer.valueOf(yPos.getText().toString());
            } catch (NumberFormatException e) {
                // Bad input here can be ignored.
            }

            try {
                prop.width = Integer.valueOf(width.getText().toString());
            } catch (NumberFormatException e) {
                // Bad input here can be ignored.
            }

            try {
                prop.height = Integer.valueOf(height.getText().toString());
            } catch (NumberFormatException e) {
                // Bad input here can be ignored.
            }

            return prop;
        }

        public void initViews() {
            type.setOnItemSelectedListener(mOnTypeSelectedListener);
            if (mPropId != INVALID_ID) {
                name.addTextChangedListener(mNameChangedListener);
            }

            if (!mIsDialogue) {
                // Hide action buttons.
                buttonBar.setVisibility(View.GONE);
            } else {
                delete.setOnClickListener(mDeleteListener);
                discard.setOnClickListener(mDiscardListener);
                confirm.setOnClickListener(mConfirmListener);
            }

            if (mPropId == INVALID_ID) {
                delete.setVisibility(View.GONE);
            }
        }

        public void setViewValues(Prop prop) {
            if (prop == null) {
                confirm.setText(R.string.action_create);
                return;
            }

            int typeSelected = 0;
            if (prop instanceof Button) {
                typeSelected = 2;
            } else if (prop instanceof Image) {
                typeSelected = 1;
            } else if (prop instanceof Text) {
                typeSelected = 0;

            }
            type.setSelection(typeSelected);

            name.setText(prop.name);
            xPos.setText(String.valueOf(prop.xPos));
            yPos.setText(String.valueOf(prop.yPos));
            width.setText(String.valueOf(prop.width));
            height.setText(String.valueOf(prop.height));
        }
    }
}
