
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class BaseProgramFragment extends Fragment {
    protected static final String ARG_OBJECT_ID = "arg_object_id";

    protected static final long INVALID_ID = -1;

    private static final String ARG_BACKGROUND = "arg_background";

    /**
     * Perform common initialisation tasks to all base program fragments.
     * 
     * @param f Fragment to initialise.
     * @param objectId Porgram object id the fragment will represent.
     * @return Initialised fragment.
     */
    protected static <T extends BaseProgramFragment> T init(T f, long objectId) {
        Bundle args = new Bundle();
        args.putLong(ARG_OBJECT_ID, objectId);
        f.setArguments(args);
        return f;
    }

    private ScrollerManager mScrollerManager;

    private int mScrollerPosition;

    protected int mBackgroundResource;

    protected ProgramCallbacks mCallbacks;

    /**
     * The id of the program object this fragment instance represents.
     */
    protected long mObjectId;

    /**
     * Get the position of this fragment in the scroller.
     */
    public int getScrollerPos() {
        return mScrollerPosition;
    }

    public void hide() {
        getView().setVisibility(View.INVISIBLE);
    }

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
            mBackgroundResource = savedInstanceState.getInt(ARG_BACKGROUND);
        }

        Bundle args = getArguments();
        if (args != null) {
            mObjectId = args.getLong(ARG_OBJECT_ID, INVALID_ID);
            if (mObjectId == INVALID_ID) {
                throw new RuntimeException("Invalid object id for fragment "
                        + this.getClass().getName());
                // onCreateNewObject();
            }
        }

    }

    public void setIsLastInList(boolean isLastInList) {
        if (isLastInList) {
            mBackgroundResource = R.drawable.opal_list_background_flat;
            if (getViewHolder() != null) {
                getViewHolder().background.setBackgroundResource(mBackgroundResource);
            }
        } else {
            mBackgroundResource = getFavouredBackground();
            if (getViewHolder() != null) {
                getViewHolder().background.setBackgroundResource(mBackgroundResource);
            }
        }
    }

    protected int getFavouredBackground() {
        return R.drawable.opal_list_background_flat;
    }

    /**
     * Set the manager for the scroller.
     * 
     * @param scrollerManager Scroller Manager.
     */
    public void setScrollerManager(ScrollerManager scrollerManager) {
        mScrollerManager = scrollerManager;
    }

    /**
     * Called should the Fragment be passed an invalid object id, thereby
     * indicating the fragment should create a new object to back its views.
     */
    // protected void onCreateNewObject() {
    // }

    /**
     * Set the position of this fragment in the scroller.
     */
    public void setScrollerPos(int position) {
        mScrollerPosition = position;
    }

    protected abstract ViewHolder<?> getViewHolder();

    /**
     * Ask the manager to hide the next fragment after this one.
     * 
     * @param f Fragment to be next in the scroller.
     */
    protected void hideNextFragment() {
        mScrollerManager.hideNextFragment(this);
    }

    /**
     * Ask the manager to remove self from the scroller.
     */
    protected void removeSelf() {
        mScrollerManager.removeFragment(this);
    }

    protected void requestMoveTo(int x) {
        mScrollerManager.requestMoveTo(x);
    }

    protected void requestMoveToView(View view) {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        requestMoveTo(location[0]);
    }

    protected void selectItemInList(long id, ListView list) {
        ListAdapter adapter = list.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItemId(i) == id) {
                list.setItemChecked(i, true);
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ARG_BACKGROUND, mBackgroundResource);
    }

    /**
     * Ask the manager to set the next fragment after this one as the given
     * fragment.
     * 
     * @param f Fragment to be next in the scroller.
     */
    protected void setNextFragment(BaseProgramFragment f) {
        mScrollerManager.setNextFragment(this, f);
    }

    public interface ScrollerManager {
        void hideNextFragment(BaseProgramFragment baseProgramFragment);

        void removeFragment(BaseProgramFragment f);

        void requestMoveTo(int x);

        void setNextFragment(BaseProgramFragment requester, BaseProgramFragment f);
    }

    protected abstract class ViewHolder<T> {
        public View background;

        public ViewHolder(View view) {
            background = view.findViewById(R.id.background);
            if (mBackgroundResource != -1) {
                background.setBackgroundResource(mBackgroundResource);
            }
        }

        abstract void initViews();

        abstract void setViewValues(T object);
    }
}
