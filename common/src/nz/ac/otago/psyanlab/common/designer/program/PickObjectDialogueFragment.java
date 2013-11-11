
package nz.ac.otago.psyanlab.common.designer.program;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.HashMapAdapter.FragmentFactoryI;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;

public class PickObjectDialogueFragment extends DialogFragment {
    public static final int FILTER_EMITS_EVENTS = 0x01;

    public static final int FILTER_HAS_FLOAT_GETTERS = 0x02;

    public static final int FILTER_HAS_INT_GETTERS = 0x03;

    public static final int FILTER_HAS_SETTERS = 0x04;

    public static final int FILTER_HAS_STRING_GETTERS = 0x05;

    public static final String TAG = "PickObjectDialogue";

    private static final String ARG_FILTER = "arg_filter";

    private static final String ARG_OBJECT_ID = "arg_object_id";

    private static final String ARG_REQUEST_CODE = "arg_request_code";

    private static final String ARG_SCENE_ID = "arg_scene_id";

    private static final long INVALID_ID = -1;

    /**
     * Create a new dialogue to edit the number of iterations a loop undergoes.
     */
    public static PickObjectDialogueFragment newDialog(long sceneId, int filter, String requestCode) {
        PickObjectDialogueFragment f = new PickObjectDialogueFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SCENE_ID, sceneId);
        args.putInt(ARG_FILTER, filter);
        args.putString(ARG_REQUEST_CODE, requestCode);
        f.setArguments(args);
        return f;
    }

    public FragmentFactoryI<Integer> mFragmentFactory = new FragmentFactory();

    private ProgramCallbacks mCallbacks;

    private DialogueResultCallbacks mDialogueCallbacks;

    private int mFilter;

    private String mRequestCode;

    private long mSceneId;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProgramCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (ProgramCallbacks)activity;

        if (!(activity instanceof DialogueResultCallbacks)) {
            throw new RuntimeException("Activity must implement dialogue result callbacks.");
        }
        mDialogueCallbacks = (DialogueResultCallbacks)activity;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater inflater = getActivity().getLayoutInflater();

        Bundle args = getArguments();
        if (args != null) {
            mSceneId = args.getLong(ARG_SCENE_ID, INVALID_ID);
            mFilter = args.getInt(ARG_FILTER);
            mRequestCode = args.getString(ARG_REQUEST_CODE);
        }

        if (mSceneId == INVALID_ID) {
            throw new RuntimeException("Invalid scene id given.");
        }

        View view = inflater.inflate(R.layout.dialogue_object_picker, null);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.title_edit_iterations).setView(view)
                .setNegativeButton(R.string.action_discard, new OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });

        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        return dialog;
    }

    public void onObjectPicked(long objectId) {
        Bundle data = new Bundle();
        data.putLong(ARG_OBJECT_ID, objectId);
        mDialogueCallbacks.onDialogueResult(mRequestCode, data);
    }

    private final class FragmentFactory implements FragmentFactoryI<Integer> {
        @Override
        public Fragment getFragment(String fragmentTitle, Integer section) {
            return PickObjectListFragment.newInstance(mSceneId, section, mFilter);
        }
    }

    class ViewHolder {
        ViewPager pager;

        PagerSlidingTabStrip tabs;

        public ViewHolder(View view) {
            pager = (ViewPager)view.findViewById(R.id.pager);
            tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
        }

        public void initViews() {
            // Set the pager with an adapter
            pager.setAdapter(mCallbacks.getObjectsPagerAdapter(mSceneId, getChildFragmentManager(),
                    mFragmentFactory));

            // Bind the widget to the adapter
            tabs.setViewPager(pager);

        }
    }
}
