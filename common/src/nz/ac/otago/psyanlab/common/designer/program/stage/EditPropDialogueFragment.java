
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
import android.text.InputType;
import android.text.TextUtils;
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

            Prop prop = getConfiguredProp();
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            if (TextUtils.equals(kind, "Text")) {
                prop = new Text(prop);
            } else if (TextUtils.equals(kind, "Image")) {
                prop = new Image(prop);
            } else if (TextUtils.equals(kind, "Button")) {
                prop = new Button(prop);
            } else {
                if (mPropertiesFragment != null) {
                    ft.remove(mPropertiesFragment);
                    ft.commit();
                }
                return;
            }

            mViews.setViewValues(prop);
            mPropertiesFragment = EditPropPropertiesFragment.newInstance(prop);

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

    @Override
    public void onDetach() {
        super.onDetach();
        Prop prop = getConfiguredProp();
        if (mPropId == INVALID_ID) {
            mCallbacks.saveProp(prop);
        } else {
            mCallbacks.setProp(mPropId, prop);
        }
    }

    protected Prop getConfiguredProp() {
        if (mPropertiesFragment != null) {
            return mViews.getConfiguredProp(mPropertiesFragment.getConfiguredProp());
        }
        return null;
    }

    public interface Callbacks {
        Prop getProp(int id);

        void saveProp(Prop prop);

        void setProp(int propId, Prop prop);
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
        }

        public void setViewValues(Prop prop) {
            mName.setText(prop.name);
            mXPos.setText(String.valueOf(prop.xPos));
            mYPos.setText(String.valueOf(prop.yPos));
            mWidth.setText(String.valueOf(prop.width));
            mHeight.setText(String.valueOf(prop.height));
        }
    }
}
