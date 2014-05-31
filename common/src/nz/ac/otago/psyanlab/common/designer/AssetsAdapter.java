
package nz.ac.otago.psyanlab.common.designer;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

class AssetsAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {
    /**
     * A list of asset keys. This is sorted according to the referenced asset.
     */
    private Long[] mAssetKeys;

    private HashMap<Long, Asset> mAssets;

    private final LayoutInflater mInflater;

    public AssetsAdapter(Context context, HashMap<Long, Asset> assets) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mAssets = assets;
        sortKeys(mAssets);
    }

    @Override
    public int getCount() {
        return mAssetKeys.length;
    }

    @Override
    public long getHeaderId(int pos) {
        return mAssets.get(mAssetKeys[pos]).getTypeId();
    }

    @Override
    public View getHeaderView(int pos, View convertView, ViewGroup parent) {
        TextViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_section_header, parent, false);
            holder = new TextViewHolder(1);
            holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder)convertView.getTag();
        }

        holder.textViews[0].setText(mAssets.get(mAssetKeys[pos]).getHeaderResId());

        return convertView;
    }

    @Override
    public Asset getItem(int pos) {
        return mAssets.get(mAssetKeys[pos]);
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
            convertView = mInflater.inflate(R.layout.list_item_asset, parent, false);
            holder = new TextViewHolder(1);
            holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder)convertView.getTag();
        }

        holder.textViews[0].setText(mAssets.get(mAssetKeys[pos]).name);

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
        sortKeys(mAssets);
        super.notifyDataSetChanged();
    }

    private void sortKeys(HashMap<Long, Asset> assets) {
        mAssetKeys = new Long[assets.size()];
        int i = 0;
        for (Long entry : assets.keySet()) {
            mAssetKeys[i] = entry;
            i++;
        }

        Arrays.sort(mAssetKeys, new Comparator<Long>() {
            final Asset.Comparator compr = new Asset.Comparator();

            @Override
            public int compare(Long lhs, Long rhs) {
                return compr.compare(mAssets.get(lhs), mAssets.get(rhs));
            }
        });
    }
}
