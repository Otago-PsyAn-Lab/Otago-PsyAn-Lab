
package nz.ac.otago.psyanlab.common.designer;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.DataChannel;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

class DataChannelsAdapter extends BaseAdapter implements ListAdapter {
    /**
     * A list of asset keys. This is sorted according to the referenced asset.
     */
    private Long[] mAssetKeys;

    private HashMap<Long, DataChannel> mDataChannels;

    private final LayoutInflater mInflater;

    public DataChannelsAdapter(Context context, HashMap<Long, DataChannel> dataChannels) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mDataChannels = dataChannels;
        sortKeys(mDataChannels);
    }

    @Override
    public int getCount() {
        return mAssetKeys.length;
    }

    @Override
    public DataChannel getItem(int pos) {
        return mDataChannels.get(mAssetKeys[pos]);
    }

    @Override
    public long getItemId(int pos) {
        if (pos < 0 || mAssetKeys.length <= pos) {
            return ListView.INVALID_ROW_ID;
        }
        return mAssetKeys[pos];
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        TextViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_data_channel, parent, false);
            holder = new TextViewHolder(1);
            holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder)convertView.getTag();
        }

        holder.textViews[0].setText(mDataChannels.get(mAssetKeys[pos]).name);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return mAssetKeys[position] >= 0;
    }

    @Override
    public void notifyDataSetChanged() {
        sortKeys(mDataChannels);
        super.notifyDataSetChanged();
    }

    private void sortKeys(HashMap<Long, DataChannel> dataChannels) {
        mAssetKeys = new Long[dataChannels.size()];
        int i = 0;
        for (Long entry : dataChannels.keySet()) {
            mAssetKeys[i] = entry;
            i++;
        }

        Arrays.sort(mAssetKeys, new Comparator<Long>() {
            final DataChannel.Comparator compr = new DataChannel.Comparator();

            @Override
            public int compare(Long lhs, Long rhs) {
                return compr.compare(mDataChannels.get(lhs), mDataChannels.get(rhs));
            }
        });
    }
}
