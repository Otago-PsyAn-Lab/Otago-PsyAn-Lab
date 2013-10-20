
package nz.ac.otago.psyanlab.common.designer.assets;

import nz.ac.otago.psyanlab.common.R;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AssetsFragment extends Fragment implements AssetsListFragment.OnShowAssetListener {
    private static final int TWO_PANE = 2;

    private int mDisplayMode;

    private AssetTabFragmentsCallbacks mCallbacks;

    private AssetsListFragment mListFragment;

    private AssetDetailFragment mDetailFragment;

    @Override
    public void showAsset(long id) {
        FragmentManager fm = getChildFragmentManager();

        if (mDisplayMode == TWO_PANE) {
            FragmentTransaction ft = fm.beginTransaction();
            mDetailFragment = AssetDetailFragment.newInstance(id);
            ft.replace(R.id.asset_detail_container, mDetailFragment, "detail");
            ft.commit();
        } else {
            mCallbacks.displayAsset(id);
        }
    }

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
        return inflater.inflate(R.layout.fragment_designer_assets, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // TODO: non two pane version - just use a configuration variable to
        // detect the display mode.
        mDisplayMode = getResources().getInteger(R.integer.assets_display_mode);

        // Attach asset list.
        AssetsListFragment listFragment = (AssetsListFragment)getChildFragmentManager()
                .findFragmentByTag("list");
        if (listFragment == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            listFragment = new AssetsListFragment();
            ft.replace(R.id.datalist, listFragment, "list");
            ft.commit();
        }
        mListFragment = listFragment;

        // Register listener so we can push asset selection through to the
        // detail view.
        mListFragment.setOnAssetClickedListener(this);
    }
}
