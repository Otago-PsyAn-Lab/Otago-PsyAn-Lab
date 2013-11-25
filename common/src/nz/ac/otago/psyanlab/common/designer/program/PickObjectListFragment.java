
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.designer.util.ExperimentObjectAdapter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private ExperimentObjectAdapter mAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProgramCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (ProgramCallbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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

        mAdapter = mCallbacks.getObjectSectionListAdapter(mSceneId, mSection, mFilter);
        setListAdapter(mAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        ((PickObjectDialogueFragment)getParentFragment()).onObjectPicked(id,
                mAdapter.getObjectKind(position), l.getItemAtPosition(position));
    }
}
