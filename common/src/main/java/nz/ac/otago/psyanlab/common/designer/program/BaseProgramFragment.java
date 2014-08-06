
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.util.TonicFragment;

import android.os.Bundle;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ListAdapter;
import android.widget.ListView;

public abstract class BaseProgramFragment extends TonicFragment {

    private static final String ARG_BACKGROUND = "arg_background";

    protected int mBackgroundResource;

    private ScrollerManager mScrollerManager;

    private int mScrollerPosition;

    /**
     * Get the position of this fragment in the scroller.
     */
    public int getScrollerPos() {
        return mScrollerPosition;
    }

    /**
     * Set the position of this fragment in the scroller.
     */
    public void setScrollerPos(int position) {
        mScrollerPosition = position;
    }

    public void hide() {
        getView().setVisibility(View.INVISIBLE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mBackgroundResource = savedInstanceState.getInt(ARG_BACKGROUND);
        }

        if (mObjectId == INVALID_ID) {
            throw new RuntimeException("Invalid object id for fragment "
                    + this.getClass().getName());
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt(ARG_BACKGROUND, mBackgroundResource);
    }

    public void setIsLastInList(boolean isLastInList) {
        if (isLastInList) {
            mBackgroundResource = R.drawable.background_designer_program_default;
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

    /**
     * Called should the Fragment be passed an invalid object id, thereby
     * indicating the fragment should create a new object to back its views.
     */
    // protected void onCreateNewObject() {
    // }

    /**
     * Set the manager for the scroller.
     *
     * @param scrollerManager Scroller Manager.
     */
    public void setScrollerManager(ScrollerManager scrollerManager) {
        mScrollerManager = scrollerManager;
    }

    protected int getFavouredBackground() {
        return R.drawable.background_designer_program_default;
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

    /**
     * Ask the manager to set the next fragment after this one as the given fragment.
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

    public abstract class ViewHolder<T> extends TonicFragment.ViewHolder<T> {

        public View background;

        public ViewHolder(View view) {
            background = view.findViewById(R.id.background);
            if (mBackgroundResource != -1) {
                background.setBackgroundResource(mBackgroundResource);
            }
        }

        protected void fixGridLayoutOverflow(GridLayout gridLayout, View view) {
            int margin = view.getBottom() - gridLayout.getHeight();

            GridLayout.LayoutParams params = (GridLayout.LayoutParams) view.getLayoutParams();

            params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, margin);
            view.setLayoutParams(params);
            view.requestLayout();
        }

        @Override
        public abstract void initViews();

        @Override
        public abstract void setViewValues(T object);
    }
}
