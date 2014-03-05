
package nz.ac.otago.psyanlab.common.designer;

import com.mobeta.android.dslv.DragSortListView.DragSortListener;

import android.support.v4.util.LongSparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ProgramComponentAdapter<T> extends BaseAdapter implements DragSortListener {
    private int mBackgroundResource = -1;

    /**
     * A list of asset keys. This is sorted according to the referenced asset.
     */
    private ArrayList<Long> mKeys;

    private LongSparseArray<T> mMap;

    private ViewBinder<T> mViewBinder;

    public ProgramComponentAdapter(LongSparseArray<T> map, ArrayList<Long> keys,
            ViewBinder<T> viewBinder) {
        mMap = map;
        mViewBinder = viewBinder;
        mKeys = keys;
    }

    @Override
    public void drag(int from, int to) {
    }

    @Override
    public void drop(int from, int to) {
        Long move = getKeys().remove(from);
        getKeys().add(to, move);
        notifyDataSetChanged();
    }

    public void fixItemBackground(int resId) {
        mBackgroundResource = resId;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return getKeys().size();
    }

    @Override
    public T getItem(int pos) {
        return mMap.get(getKeys().get(pos));
    }

    @Override
    public long getItemId(int pos) {
        if (pos < 0 || getKeys().size() <= pos) {
            return ListView.INVALID_ROW_ID;
        }
        return getKeys().get(pos);
    }

    public ArrayList<Long> getKeys() {
        return mKeys;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        View view = mViewBinder.bind(mMap.get(getKeys().get(pos)), convertView, parent);
        if (mBackgroundResource != -1) {
            view.setBackgroundResource(mBackgroundResource);
        }
        return view;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void remove(int which) {
        getKeys().remove(which);
        notifyDataSetChanged();
    }

    public void setKeys(ArrayList<Long> keys) {
        mKeys = keys;
        notifyDataSetChanged();
    }

    public interface ViewBinder<T> {
        public View bind(T t, View convertView, ViewGroup parent);
    }
}
