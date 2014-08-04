
package nz.ac.otago.psyanlab.common;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;

class RecordAdapterWrapper implements StickyGridHeadersSimpleAdapter {
    private final ListAdapter mDelegate;

    private Context mContext;

    public RecordAdapterWrapper(Context context, ListAdapter delegate) {
        mContext = context;
        mDelegate = delegate;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return mDelegate.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return mDelegate.isEnabled(position);
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        mDelegate.registerDataSetObserver(observer);
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        mDelegate.unregisterDataSetObserver(observer);
    }

    @Override
    public int getCount() {
        return mDelegate.getCount();
    }

    @Override
    public Object getItem(int position) {
        return mDelegate.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return mDelegate.getItemId(position);
    }

    @Override
    public boolean hasStableIds() {
        return mDelegate.hasStableIds();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return mDelegate.getView(position, convertView, parent);
    }

    @Override
    public int getItemViewType(int position) {
        return mDelegate.getItemViewType(position);
    }

    @Override
    public int getViewTypeCount() {
        return mDelegate.getViewTypeCount();
    }

    @Override
    public boolean isEmpty() {
        return mDelegate.isEmpty();
    }

    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater)mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.header_record_list, parent, false);
            holder = new ViewHolder();
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        holder.count.setText(mContext.getResources().getString(R.string.format_header_record_count,
                mDelegate.getCount()));
        return null;
    }

    @Override
    public long getHeaderId(int position) {
        return 0;
    }

    class ViewHolder {
        TextView header;

        TextView count;
    }
}
