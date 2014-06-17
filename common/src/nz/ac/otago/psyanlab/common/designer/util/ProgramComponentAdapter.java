
package nz.ac.otago.psyanlab.common.designer.util;

import com.mobeta.android.dslv.DragSortListView.DragSortListener;

import nz.ac.otago.psyanlab.common.R;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ProgramComponentAdapter<T> extends BaseAdapter implements DragSortListener {
    private int mBackgroundResource = -1;

    private boolean mHideItems;

    /**
     * A list of asset keys. This is sorted according to the referenced asset.
     */
    private List<Long> mKeys;

    private HashMap<Long, T> mMap;

    private ViewBinder<T> mViewBinder;

    private boolean mOnlySingleGrabbable = true;

    public ProgramComponentAdapter(HashMap<Long, T> map, List<Long> keys, ViewBinder<T> viewBinder) {
        mMap = map;
        mViewBinder = viewBinder;
        mKeys = keys;
        mHideItems = false;
    }

    @Override
    public void drag(int from, int to) {
    }

    @Override
    public void drop(int from, int to) {
        if (mKeys == null) {
            return;
        }

        Long move = mKeys.remove(from);
        mKeys.add(to, move);
        notifyDataSetChanged();
    }

    public void fixItemBackground(int resId) {
        mBackgroundResource = resId;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        if (mKeys == null || mHideItems) {
            return 0;
        }
        return mKeys.size();
    }

    @Override
    public T getItem(int pos) {
        if (mKeys == null) {
            return null;
        }

        return mMap.get(mKeys.get(pos));
    }

    @Override
    public long getItemId(int pos) {
        if (mKeys == null || pos < 0 || mKeys.size() <= pos) {
            return ListView.INVALID_ROW_ID;
        }
        return mKeys.get(pos);
    }

    public Collection<Long> getKeys() {
        return mKeys;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = mViewBinder.bind(mMap.get(mKeys.get(position)), convertView, parent);
        if (mBackgroundResource != -1) {
            view.setBackgroundResource(mBackgroundResource);
        }

        ListView list;
        if (parent instanceof ListView) {
            list = (ListView)parent;
        } else {
            list = (ListView)parent.getParent();
        }

        View handle = view.findViewById(R.id.handle);
        if (handle != null && mOnlySingleGrabbable) {
            if (list.getChoiceMode() == ListView.CHOICE_MODE_SINGLE && list.isItemChecked(position)) {
                handle.setEnabled(true);
                handle.setVisibility(View.VISIBLE);
            } else {
                handle.setEnabled(false);
                handle.setVisibility(View.GONE);
            }
        }

        return view;
    }

    public void setOnlySingleGrabbable(boolean onlySingleGrabbable) {
        mOnlySingleGrabbable = onlySingleGrabbable;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void hideItems() {
        if (!mHideItems) {
            mHideItems = true;
            notifyDataSetChanged();
        }
    }

    @Override
    public void remove(int which) {
        if (mKeys == null) {
            return;
        }

        mKeys.remove(which);
        notifyDataSetChanged();
    }

    public void setKeys(List<Long> keys) {
        mKeys = keys;
        notifyDataSetChanged();
    }

    public void showItems() {
        if (mHideItems) {
            mHideItems = false;
            notifyDataSetChanged();
        }
    }

    public interface ViewBinder<T> {
        public View bind(T t, View convertView, ViewGroup parent);
    }
}
