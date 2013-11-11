
package nz.ac.otago.psyanlab.common.designer.program;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

public class PickObjectListFragment extends ListFragment {
    private static final String ARG_SCENE_ID = "arg_scene_id";

    private static final String ARG_SECTION = "arg_section";

    private static final String ARG_FILTER = "arg_filter";

    public static PickObjectListFragment newInstance(long sceneId, Integer section, int filter) {
        PickObjectListFragment f = new PickObjectListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION, section);
        args.putLong(ARG_SCENE_ID, sceneId);
        args.putInt(ARG_FILTER, filter);
        f.setArguments(args);
        return f;
    }

    private ProgramCallbacks mCallbacks;

    private long mSceneId;

    private int mSection;

    private int mFilter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProgramCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (ProgramCallbacks)activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args.isEmpty()) {
            throw new RuntimeException("Missing arguments.");
        }

        mSection = args.getInt(ARG_SECTION);
        mSceneId = args.getLong(ARG_SCENE_ID);
        mFilter = args.getInt(ARG_FILTER);

        setListAdapter(mCallbacks.getObjectSectionListAdapter(mSceneId, mSection, mFilter));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ((PickObjectDialogueFragment)mCallbacks.getFragment(PickObjectDialogueFragment.TAG))
                .onObjectPicked(id);
    }
}
