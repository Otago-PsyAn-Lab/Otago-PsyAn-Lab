
package nz.ac.otago.psyanlab.common.designer.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public class ExperimentObjectAdapter<T extends Comparable<T>> extends BaseAdapter implements
        ListAdapter {
    private Comparator<Long> mComparator = new Comparator<Long>() {
        @Override
        public int compare(Long lhs, Long rhs) {
            return mItems.get(lhs).compareTo(mItems.get(rhs));
        }
    };

    private final LayoutInflater mInflater;

    private HashMap<Long, T> mItems;

    /**
     * A list of asset keys. This is sorted according to the referenced asset.
     */
    private Long[] mKeys;

    private ViewBinder<T> mViewBinder;

    public ExperimentObjectAdapter(Context context, HashMap<Long, T> items, ViewBinder<T> viewBinder) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mItems = items;
        mViewBinder = viewBinder;
        sortKeys(mItems);
    }

    @Override
    public int getCount() {
        return mKeys.length;
    }

    @Override
    public T getItem(int pos) {
        return mItems.get(mKeys[pos]);
    }

    @Override
    public long getItemId(int pos) {
        if (pos < 0 || mKeys.length <= pos) {
            return ListView.INVALID_ROW_ID;
        }
        return mKeys[pos];
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        return mViewBinder.bind(mInflater, mItems.get(mKeys[pos]), pos, convertView, parent);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return mKeys[position] >= 0;
    }

    @Override
    public void notifyDataSetChanged() {
        sortKeys(mItems);
        super.notifyDataSetChanged();
    }

    private void sortKeys(HashMap<Long, T> dataChannels) {
        mKeys = new Long[dataChannels.size()];
        int i = 0;
        for (Long entry : dataChannels.keySet()) {
            mKeys[i] = entry;
            i++;
        }

        Arrays.sort(mKeys, mComparator);
    }

    public interface ViewBinder<T> {
        View bind(LayoutInflater inflater, T item, int pos, View convertView, ViewGroup parent);
    }
}
