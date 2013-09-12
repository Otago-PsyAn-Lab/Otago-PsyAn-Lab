
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Prop;
import nz.ac.otago.psyanlab.common.model.prop.Button;
import nz.ac.otago.psyanlab.common.model.prop.Image;
import nz.ac.otago.psyanlab.common.model.prop.Text;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
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

            mViews.name.setInputType(InputType.TYPE_NULL);

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

    private Callbacks mCallbacks;

    private int mPropId = INVALID_ID;

    private Prop mProp;

    private ViewHolder mViews;

    protected Fragment mPropertiesFragment;

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

        if (mPropId != INVALID_ID) {
            mProp = mCallbacks.getProp(mPropId);
        }

        mViews = new ViewHolder(view);
        mViews.initViews();

        mPropertiesFragment = getChildFragmentManager().findFragmentByTag(TAG_PROPERTIES_FRAGMENT);
        if (mPropertiesFragment == null && mProp != null) {
            mPropertiesFragment = EditPropPropertiesFragment.newInstance(mPropId);
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            ft.replace(R.id.prop_properties_container, mPropertiesFragment, TAG_PROPERTIES_FRAGMENT);
            ft.commit();
        }

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        if (mPropId == INVALID_ID) {
            mCallbacks.saveProp(mProp);
        }
    }

    public interface Callbacks {
        Prop getProp(int id);

        void saveProp(Prop prop);
    }

    public class ViewHolder {
        public EditText name;

        public Spinner type;

        public ViewHolder(View view) {
            name = (EditText)view.findViewById(R.id.name);
            type = (Spinner)view.findViewById(R.id.type);
        }

        public void initViews() {
            type.setOnItemSelectedListener(mOnTypeSelectedListener);
        }

        public void setViewValues(Prop prop) {
            name.setText(prop.name);
        }
    }
}
