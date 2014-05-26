
package nz.ac.otago.psyanlab.common.designer.program.object;

import com.astuetz.viewpager.extensions.PagerSlidingTabStrip;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.util.ProgramCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.ArrayFragmentMapAdapter;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultCallbacks;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

public class PickObjectDialogueFragment extends DialogFragment {
    public static final String RESULT_OBJECT_ID = "result_object_id";

    public static final String RESULT_OBJECT_KIND = "result_object_kind";

    public static final String TAG = "PickObjectDialogue";

    private static final String ARG_FILTER = "arg_filter";

    private static final String ARG_REQUEST_CODE = "arg_request_code";

    private static final String ARG_SCENE_ID = "arg_scene_id";

    private static final long INVALID_ID = -1;

    /**
     * Create a new dialogue to edit the number of iterations a loop undergoes.
     */
    public static PickObjectDialogueFragment newDialog(long sceneId, int filter, int requestCode) {
        PickObjectDialogueFragment f = new PickObjectDialogueFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SCENE_ID, sceneId);
        args.putInt(ARG_FILTER, filter);
        args.putInt(ARG_REQUEST_CODE, requestCode);
        f.setArguments(args);
        return f;
    }

    public ArrayFragmentMapAdapter.Factory mFragmentFactory;

    private ProgramCallbacks mCallbacks;

    private DialogueResultCallbacks mDialogueCallbacks;

    private int mFilter;

    private OnClickListener mOnCancelListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            getDialog().cancel();
        }
    };

    private int mRequestCode;

    private long mSceneId;

    private ViewHolder mViews;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialogue_experiment_object_picker, container, false);
    }

    public void onObjectPicked(long objectId, int objectKind, Object object) {
        Bundle data = new Bundle();
        data.putLong(RESULT_OBJECT_ID, objectId);
        data.putInt(RESULT_OBJECT_KIND, objectKind);
        mDialogueCallbacks.onDialogueResult(mRequestCode, data);
        getDialog().dismiss();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Dialog dialog = getDialog();
        dialog.setTitle(R.string.title_dialogue_pick_object);
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        Bundle args = getArguments();
        if (args != null) {
            mSceneId = args.getLong(ARG_SCENE_ID, INVALID_ID);
            mFilter = args.getInt(ARG_FILTER);
            mRequestCode = args.getInt(ARG_REQUEST_CODE);
        }

        if (mSceneId == INVALID_ID) {
            throw new RuntimeException("Invalid scene id given.");
        }

        mFragmentFactory = new FragmentFactory(mSceneId, mFilter);
        mViews = new ViewHolder(view);
        mViews.initViews();
    }

    static final class FragmentFactory implements ArrayFragmentMapAdapter.Factory {
        private int mFilter;

        private long mSceneId;

        public FragmentFactory(long sceneId, int filter) {
            mSceneId = sceneId;
            mFilter = filter;
        }

        @Override
        public Fragment getFragment(String fragmentTitle, int section) {
            return PickObjectListFragment.newInstance(mSceneId, section, mFilter);
        }
    };

    class ViewHolder {
        Button cancel;

        ViewPager pager;

        PagerSlidingTabStrip tabs;

        public ViewHolder(View view) {
            pager = (ViewPager)view.findViewById(R.id.pager);
            tabs = (PagerSlidingTabStrip)view.findViewById(R.id.tabs);
            cancel = (Button)view.findViewById(R.id.action_cancel);
        }

        public void initViews() {
            // Set the pager with an adapter
            pager.setAdapter(mCallbacks.getObjectsPagerAdapter(getChildFragmentManager(), mSceneId,
                    mFragmentFactory));

            cancel.setOnClickListener(mOnCancelListener);

            // Bind the widget to the adapter
            tabs.setViewPager(pager);
        }
    }
}
