
package nz.ac.otago.psyanlab.common.designer;

import com.mobeta.android.dslv.DragSortListView.DragSortListener;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class ProgramComponentAdapter<T> extends BaseAdapter implements DragSortListener {
    /**
     * A list of asset keys. This is sorted according to the referenced asset.
     */
    private ArrayList<Long> mKeys;

    private HashMap<Long, T> mMap;

    private ViewBinder<T> mViewBinder;

    public ProgramComponentAdapter(HashMap<Long, T> map, ArrayList<Long> keys,
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
        Long move = mKeys.remove(from);
        mKeys.add(to, move);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mKeys.size();
    }

    @Override
    public T getItem(int pos) {
        return mMap.get(mKeys.get(pos));
    }

    @Override
    public long getItemId(int pos) {
        if (pos < 0 || mKeys.size() <= pos) {
            return ListView.INVALID_ROW_ID;
        }
        return mKeys.get(pos);
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        return mViewBinder.bind(mMap.get(mKeys.get(pos)), convertView, parent);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void remove(int which) {
        mKeys.remove(which);
        notifyDataSetChanged();
    }

    public interface ViewBinder<T> {
        public View bind(T t, View convertView, ViewGroup parent);
    }
}
