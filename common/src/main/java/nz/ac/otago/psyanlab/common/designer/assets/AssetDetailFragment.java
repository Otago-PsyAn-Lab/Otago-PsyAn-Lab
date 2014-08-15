
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

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.asset.Image;
import nz.ac.otago.psyanlab.common.model.asset.Sound;
import nz.ac.otago.psyanlab.common.model.asset.Video;
import nz.ac.otago.psyanlab.common.util.FileUtils;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.TextView;

public class AssetDetailFragment extends Fragment {
    private static final String KEY_ASSET_ID = "key_instance_state";

    public static AssetDetailFragment newInstance(long assetId) {
        AssetDetailFragment f = new AssetDetailFragment();
        f.setAssetId(assetId);
        return f;
    }

    private Asset mAsset;

    private long mAssetId;

    private AssetCallbacks mCallbacks;

    private OnClickListener mDeleteClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Post delayed because we want to let the visual feedback have time
            // to show.
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCallbacks.deleteAsset(mAssetId);
                    mOnDeleteListener.onDeleteAsset();
                }
            }, ViewConfiguration.getTapTimeout());
        }
    };

    private ViewHolder mViews;

    private OnDeleteAssetListener mOnDeleteListener;

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
        inflater.cloneInContext(new ContextThemeWrapper(getActivity(),
                android.R.style.Theme_Holo_Light));
        return inflater.inflate(R.layout.fragment_designer_asset_detail, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putLong(KEY_ASSET_ID, mAssetId);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mViews = new ViewHolder(view);

        if (savedInstanceState != null) {
            mAssetId = savedInstanceState.getLong(KEY_ASSET_ID);
        }

        mAsset = mCallbacks.getAsset(mAssetId);
        mViews.initViews();
        mViews.setViewValues(mAsset);

        if (mAsset instanceof Image) {
            // TODO:
            // FragmentTransaction ft =
            // getChildFragmentManager().beginTransaction();
            // Fragment f = ImageDetailFragment.newInstance(mAssetId);
            // ft.replace(R.id.fragment_container, f);
            // ft.commit();

        } else if (mAsset instanceof Sound) {
            // TODO:

        } else if (mAsset instanceof Video) {
            // TODO:

        } else {
            throw new RuntimeException("Unknown kind of asset.");
        }
    }

    public void setAssetId(long id) {
        mAssetId = id;
    }

    private class ViewHolder {
        public View delete;

        public TextView filename;

        public TextView filesize;

        public ViewHolder(View view) {
            filename = (TextView)view.findViewById(R.id.filename);
            filesize = (TextView)view.findViewById(R.id.filesize);
            delete = view.findViewById(R.id.button_delete);
        }

        public void setViewValues(Asset asset) {
            filename.setText(asset.filename);
            filesize.setText(FileUtils.formatBytes(asset.filesize));
        }

        public void initViews() {
            delete.setOnClickListener(mDeleteClickListener);
        }
    }

    public void setOnAssetDeletedListener(OnDeleteAssetListener listener) {
        mOnDeleteListener = listener;
    }

    public interface OnDeleteAssetListener {
        void onDeleteAsset();
    }
}
