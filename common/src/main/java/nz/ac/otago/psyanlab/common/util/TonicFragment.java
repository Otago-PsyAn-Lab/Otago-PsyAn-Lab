
package nz.ac.otago.psyanlab.common.util;

import nz.ac.otago.psyanlab.common.designer.program.util.ProgramCallbacks;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Provides basic features of data representative fragments in OPAL.
 */
public class TonicFragment extends Fragment {
    protected static final String ARG_OBJECT_ID = "arg_object_id";

    protected static final long INVALID_ID = -1;

    /**
     * Perform common initialisation tasks to all base program fragments.
     * 
     * @param f Fragment to initialise.
     * @param objectId Object id the fragment will represent.
     * @return Initialised fragment.
     */
    public static <T extends TonicFragment> T init(T f, long objectId) {
        Bundle args = new Bundle();
        args.putLong(ARG_OBJECT_ID, objectId);
        f.setArguments(args);
        return f;
    }

    protected ProgramCallbacks mCallbacks;

    /**
     * The id of the program object this fragment instance represents.
     */
    protected long mObjectId;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ProgramCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (ProgramCallbacks)activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mObjectId = savedInstanceState.getLong(ARG_OBJECT_ID, INVALID_ID);
        } else {
            Bundle args = getArguments();
            if (args != null) {
                if (!args.containsKey(ARG_OBJECT_ID)) {
                    throw new RuntimeException("Expected object id.");
                }
                mObjectId = args.getLong(ARG_OBJECT_ID, INVALID_ID);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(ARG_OBJECT_ID, mObjectId);
    }

    public abstract class ViewHolder<T> {
        public View background;

        public abstract void initViews();

        public abstract void setViewValues(T object);
    }
}
