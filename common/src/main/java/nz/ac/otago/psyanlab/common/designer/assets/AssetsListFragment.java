
/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.common.designer.assets;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersGridView;

import nz.ac.otago.psyanlab.common.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class AssetsListFragment extends Fragment {
    private static final OnShowAssetListener sDummy = new OnShowAssetListener() {
        @Override
        public void showAsset(long id) {
        }
    };

    private AssetCallbacks mCallbacks;

    private OnShowAssetListener mShowAssetListener = sDummy;

    private ViewHolder mViews;

    protected OnClickListener mImportClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.startImportAssetUI();
        }
    };

    protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mShowAssetListener.showAsset(id);
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof AssetCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (AssetCallbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_asset_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViews = new ViewHolder(view);
        mViews.initViews();
    }

    public void setOnAssetClickedListener(OnShowAssetListener listener) {
        mShowAssetListener = listener;
    }

    public interface OnShowAssetListener {
        void showAsset(long id);
    }

    class ViewHolder {
        public View addAsset;

        public StickyGridHeadersGridView list;

        public ViewHolder(View view) {
            list = (StickyGridHeadersGridView)view.findViewById(R.id.stickyList);
            addAsset = view.findViewById(R.id.button_import);
        }

        public void initViews() {
            addAsset.setOnClickListener(mImportClickListener);
            list.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            list.setOnItemClickListener(mOnItemClickListener);
            list.setAdapter(mCallbacks.getAssetsAdapter());
            list.setDrawSelectorOnTop(false);
            list.setAreHeadersSticky(false);
        }
    }
}
