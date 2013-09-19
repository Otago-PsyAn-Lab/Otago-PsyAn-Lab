
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.EditText;
import android.widget.Spinner;

public class EditPropDialogueFragment extends DialogFragment {

    private static final String ARG_ID = "arg_id";

    private static final int INVALID_ID = -1;

    protected static final String TAG_PROPERTIES_FRAGMENT = "tag_properties_fragment";

    public static EditPropDialogueFragment newDialogue(int propId) {
        EditPropDialogueFragment f = new EditPropDialogueFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ID, propId);
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

    public OnItemSelectedListener mOnTypeSelectedListener = new OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> spinner, View view, int position, long id) {
            if (mWasOptionsFragmentPersisted) {
                mWasOptionsFragmentPersisted = false;
                return;
            }
            final String kind = getResources().getStringArray(R.array.prop_types)[position];

            mViews.mName.setInputType(InputType.TYPE_NULL);

            mProp = getConfiguredProp();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            if (TextUtils.equals(kind, "Text")) {
                mProp = new Text(mProp);
            } else if (TextUtils.equals(kind, "Image")) {
                mProp = new Image(mProp);
            } else if (TextUtils.equals(kind, "Button")) {
                mProp = new Button(mProp);
            } else {
                if (mPropertiesFragment != null) {
                    ft.remove(mPropertiesFragment);
                    ft.commit();
                }
                return;
            }

            mViews.setViewValues(mProp);
            mPropertiesFragment = EditPropPropertiesFragment.newInstance(mProp);

            mViews.mName.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);

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

    private Callbacks mCallbacks;

    private int mPropId = INVALID_ID;

    private ViewHolder mViews;

    protected EditPropPropertiesFragment mPropertiesFragment;

    protected boolean mWasOptionsFragmentPersisted;

    public TextWatcher mNameChangedListener = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mProp.name = s.toString();
            mCallbacks.saveProp(mPropId, mProp);
        }
    };

    private Prop mProp;

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
        if (!(activity instanceof Callbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogue_edit_prop, null);

        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(ARG_ID)) {
                mPropId = args.getInt(ARG_ID, INVALID_ID);
            }
        }

        Prop prop = null;
        if (mPropId != INVALID_ID) {
            prop = mCallbacks.getProp(mPropId);
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

        return view;
    }

    protected Prop getConfiguredProp() {
        if (mPropertiesFragment != null) {
            mProp = mViews.getConfiguredProp(mPropertiesFragment.getConfiguredProp());
            mViews.getConfiguredProp(mProp);
        }
        return mProp;
    }

    public interface Callbacks {
        Prop getProp(int id);

        void saveProp(Prop prop);

        void saveProp(int propId, Prop prop);
    }

    public class ViewHolder {
        private EditText mHeight;

        private EditText mName;

        private Spinner mType;

        private EditText mWidth;

        private EditText mXPos;

        private EditText mYPos;

        public ViewHolder(View view) {
            mName = (EditText)view.findViewById(R.id.name);
            mType = (Spinner)view.findViewById(R.id.type);
            mXPos = (EditText)view.findViewById(R.id.xPos);
            mYPos = (EditText)view.findViewById(R.id.yPos);
            mWidth = (EditText)view.findViewById(R.id.width);
            mHeight = (EditText)view.findViewById(R.id.height);
        }

        public Prop getConfiguredProp(Prop prop) {
            prop.name = mName.getText().toString();

            try {
                prop.xPos = Integer.valueOf(mXPos.getText().toString());
            } catch (NumberFormatException e) {
                // Bad input here can be ignored.
            }

            try {
                prop.yPos = Integer.valueOf(mYPos.getText().toString());
            } catch (NumberFormatException e) {
                // Bad input here can be ignored.
            }

            try {
                prop.width = Integer.valueOf(mWidth.getText().toString());
            } catch (NumberFormatException e) {
                // Bad input here can be ignored.
            }

            try {
                prop.height = Integer.valueOf(mHeight.getText().toString());
            } catch (NumberFormatException e) {
                // Bad input here can be ignored.
            }

            return prop;
        }

        public void initViews() {
            mType.setOnItemSelectedListener(mOnTypeSelectedListener);
            if (mPropId != INVALID_ID) {
                mName.addTextChangedListener(mNameChangedListener);
            }
        }

        public void setViewValues(Prop prop) {
            if (prop == null) {
                return;
            }

            mName.setText(prop.name);
            mXPos.setText(String.valueOf(prop.xPos));
            mYPos.setText(String.valueOf(prop.yPos));
            mWidth.setText(String.valueOf(prop.width));
            mHeight.setText(String.valueOf(prop.height));
        }
    }
}
