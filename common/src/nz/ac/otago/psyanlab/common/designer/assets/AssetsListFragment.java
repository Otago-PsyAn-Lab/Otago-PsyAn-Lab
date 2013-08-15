
package nz.ac.otago.psyanlab.common.designer.assets;

import nz.ac.otago.psyanlab.common.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class AssetsListFragment extends ListFragment {
    private static final OnShowAssetListener sDummy = new OnShowAssetListener() {
        @Override
        public void showAsset(long id) {
        }
    };

    private AssetTabFragmentsCallbacks mCallbacks;

    private OnShowAssetListener mShowAssetListener = sDummy;

    private OnClickListener mImportClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCallbacks.doImportAsset();
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof AssetTabFragmentsCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (AssetTabFragmentsCallbacks)activity;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
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
    public void onListItemClick(ListView l, View v, int p, long id) {
        super.onListItemClick(l, v, p, id);
        mShowAssetListener.showAsset(id);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        setListAdapter(mCallbacks.getAssetsAdapter());
        View buttonImport = view.findViewById(R.id.button_import);
        buttonImport.setOnClickListener(mImportClickListener);
    }

    public void setOnAssetClickedListener(OnShowAssetListener listener) {
        mShowAssetListener = listener;
    }

    public interface OnShowAssetListener {
        void showAsset(long id);
    }
}
