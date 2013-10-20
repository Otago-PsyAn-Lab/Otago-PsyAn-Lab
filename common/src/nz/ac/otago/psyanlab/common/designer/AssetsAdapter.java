
package nz.ac.otago.psyanlab.common.designer;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.support.v4.util.LongSparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;

class AssetsAdapter extends BaseAdapter implements StickyGridHeadersSimpleAdapter {
    /**
     * A list of asset keys. This is sorted according to the referenced asset.
     */
    private Long[] mAssetKeys;

    private LongSparseArray<Asset> mAssets;

    private final LayoutInflater mLayoutInflater;

    public AssetsAdapter(ExperimentDesignerActivity activity, LongSparseArray<Asset> assets) {
        mLayoutInflater = activity.getLayoutInflater();
        // for (int i = 0; i < assets.size(); i++) {
        // long key = assets.keyAt(i);
        // if (key < 0) {
        // assets.remove(key);
        // }
        // }
        mAssets = assets;
        sortKeys(mAssets);
    }

    @Override
    public int getCount() {
        return mAssetKeys.length;
    }

    // @Override
    // public int getViewTypeCount() {
    // return 2;
    // }
    //
    // @Override
    // public int getItemViewType(int position) {
    // if (mAssetKeys[position] < 0) {
    // return 1;
    // }
    // return 0;
    // }

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
    public boolean isEnabled(int position) {
        return mAssetKeys[position] >= 0;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        TextViewHolder holder;

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(R.layout.list_item_loop, parent, false);
            convertView.findViewById(R.id.handle).setVisibility(View.GONE);
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
    public void notifyDataSetChanged() {
        sortKeys(mAssets);
        super.notifyDataSetChanged();
    }

    private void sortKeys(LongSparseArray<Asset> assets) {
        mAssetKeys = new Long[assets.size()];
        for (int i = 0; i < assets.size(); i++) {
            mAssetKeys[i] = assets.keyAt(i);
        }

        Arrays.sort(mAssetKeys, new Comparator<Long>() {
            @Override
            public int compare(Long lhs, Long rhs) {
                return mAssets.get(lhs).compareTo(mAssets.get(rhs));
            }
        });
        // long insertions = -1;
        // long lastHeaderId = -1;
        // long lastKey = 0;
        // ArrayList<Long> mMod = new ArrayList<Long>();
        // for (long key : mAssetKeys) {
        // long headerId = mAssets.get(key).getTypeId();
        // if (lastHeaderId != -1 && headerId != lastHeaderId) {
        // mMod.add(insertions);
        // mAssets.put(insertions, mAssets.get(lastKey));
        // insertions--;
        // Log.d("asdfadsf", "added");
        // }
        // lastHeaderId = headerId;
        // mMod.add(key);
        // lastKey = key;
        // }
        //
        // mAssetKeys = mMod.toArray(new Long[mMod.size()]);
    }
}
