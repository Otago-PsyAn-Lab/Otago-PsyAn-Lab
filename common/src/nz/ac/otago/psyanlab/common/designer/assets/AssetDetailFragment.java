
package nz.ac.otago.psyanlab.common.designer.assets;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.asset.Csv;
import nz.ac.otago.psyanlab.common.model.asset.Image;
import nz.ac.otago.psyanlab.common.model.asset.Sound;
import nz.ac.otago.psyanlab.common.model.asset.Video;
import nz.ac.otago.psyanlab.common.util.FileUtils;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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

    private AssetTabFragmentsCallbacks mCallbacks;

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
        if (!(activity instanceof AssetTabFragmentsCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (AssetTabFragmentsCallbacks)activity;
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

        if (mAsset instanceof Csv) {
            CSVDataDetailFragment f = (CSVDataDetailFragment)getChildFragmentManager()
                    .findFragmentByTag("extra_detail");
            if (f == null) {
                FragmentTransaction ft = getChildFragmentManager().beginTransaction();
                f = CSVDataDetailFragment.newInstance(mAssetId);
                ft.replace(R.id.fragment_container, f, "extra_detail");
                ft.commit();
            }

        } else if (mAsset instanceof Image) {
            // FragmentTransaction ft =
            // getChildFragmentManager().beginTransaction();
            // Fragment f = ImageDetailFragment.newInstance(mAssetId);
            // ft.replace(R.id.fragment_container, f);
            // ft.commit();

        } else if (mAsset instanceof Sound) {

        } else if (mAsset instanceof Video) {

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
