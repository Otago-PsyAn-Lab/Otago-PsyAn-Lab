
package nz.ac.otago.psyanlab.common.designer;

import com.emilsjolander.components.stickylistheaders.StickyListHeadersAdapter;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

class AssetsAdapter extends BaseAdapter implements StickyListHeadersAdapter {
    /**
     * A list of asset keys. This is sorted according to the referenced asset.
     */
    private Long[] mAssetKeys;

    private HashMap<Long, Asset> mAssets;

    private final LayoutInflater mLayoutInflater;

    public AssetsAdapter(ExperimentDesignerActivity activity, HashMap<Long, Asset> assets) {
        mLayoutInflater = activity.getLayoutInflater();
        mAssets = assets;
        sortKeys(mAssets);
    }

    @Override
    public int getCount() {
        return mAssetKeys.length;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getHeaderId(int pos) {
        return mAssets.get(mAssetKeys[pos]).getTypeId();
    }

    @Override
    public View getHeaderView(int pos, View convertView, ViewGroup parent) {
        TextViewHolder holder;
        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.section_header, parent, false);
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
            convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_activated_1,
                    parent, false);
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
    public void notifyDataSetChanged() {
        sortKeys(mAssets);
        super.notifyDataSetChanged();
    }

    private void sortKeys(HashMap<Long, Asset> assets) {
        mAssetKeys = assets.keySet().toArray(new Long[assets.size()]);
        Arrays.sort(mAssetKeys, new Comparator<Long>() {
            @Override
            public int compare(Long lhs, Long rhs) {
                return mAssets.get(lhs).compareTo(mAssets.get(rhs));
            }
        });
    }
}
