
package nz.ac.otago.psyanlab.common.designer.assets;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Asset;
import nz.ac.otago.psyanlab.common.model.asset.Csv;
import nz.ac.otago.psyanlab.common.model.asset.Image;
import nz.ac.otago.psyanlab.common.model.asset.Sound;
import nz.ac.otago.psyanlab.common.model.asset.Video;
import nz.ac.otago.psyanlab.common.util.ConfirmDialogFragment;
import nz.ac.otago.psyanlab.common.util.FileUtils;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

public class AssetDetailFragment extends Fragment {
    private static final String KEY_ASSET_ID = "key_instance_state";

    public static AssetDetailFragment newInstance(long assetId) {
        AssetDetailFragment f = new AssetDetailFragment();
        f.setAssetId(assetId);
        Log.d("AssetDetailFragment", "Create for asset id " + assetId);
        return f;
    }

    private Asset mAsset;

    private long mAssetId;

    private AssetTabFragmentsCallbacks mCallbacks;

    private OnClickListener mDeleteClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.deleteAsset(mAssetId);
            getView().setVisibility(View.GONE);
        }
    };

    private DetailFragmentI mDetailFragmentI;

    private ViewHolder mViews;

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
    public void onDetach() {
        super.onDetach();

        mCallbacks.updateAsset(mAssetId, mAsset);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            DialogFragment dialog = ConfirmDialogFragment.newInstance(
                    R.string.title_confirm_delete_asset, R.string.action_delete,
                    R.string.action_cancel, new ConfirmDialogFragment.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            mCallbacks.deleteAsset(mAssetId);
                            dialog.dismiss();
                        }
                    }, new ConfirmDialogFragment.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                        }
                    });
            dialog.show(getChildFragmentManager(), "ConfirmDeleteDialog");
        }
        return super.onOptionsItemSelected(item);
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
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            CSVDataDetailFragment f = CSVDataDetailFragment.newInstance(mAssetId);
            mDetailFragmentI = f;
            ft.replace(R.id.fragment_container, f, "extra_detail");
            ft.commit();

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

    public void saveAsset() {
        if (getView().getVisibility() != View.GONE) {
            mDetailFragmentI.saveAsset();
        }
    }

    public void setAssetId(long id) {
        mAssetId = id;
    }

    public interface DetailFragmentI {
        void saveAsset();
    }

    private class ViewHolder {
        public ImageButton delete;

        public TextView filename;

        public TextView filesize;

        public ViewHolder(View view) {
            filename = (TextView)view.findViewById(R.id.filename);
            filesize = (TextView)view.findViewById(R.id.filesize);
            delete = (ImageButton)view.findViewById(R.id.button_delete);
        }

        public void setViewValues(Asset asset) {
            filename.setText(asset.filename);
            filesize.setText(FileUtils.formatBytes(asset.filesize));
        }

        public void initViews() {
            delete.setOnClickListener(mDeleteClickListener);
        }
    }
}
