
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

    public OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mShowAssetListener.showAsset(id);
        }
    };

    private AssetTabFragmentsCallbacks mCallbacks;

    private OnClickListener mImportClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.doImportAsset();
        }
    };

    private OnShowAssetListener mShowAssetListener = sDummy;

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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_asset_list, container, false);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
