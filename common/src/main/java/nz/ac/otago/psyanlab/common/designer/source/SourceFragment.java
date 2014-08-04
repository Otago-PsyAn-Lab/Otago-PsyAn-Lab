
package nz.ac.otago.psyanlab.common.designer.source;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.source.SourceDetailFragment.OnSourceDeletedListener;
import nz.ac.otago.psyanlab.common.designer.source.SourceListFragment.ShowSourceListener;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.DrawerLayout.DrawerListener;
import android.support.v4.widget.SlidingPaneLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class SourceFragment extends Fragment implements ShowSourceListener, OnSourceDeletedListener {
    private static final String TAG_DETAIL_FRAGMENT = "source_detail_fragment";

    private static final String TAG_LIST_FRAGMENT = "source_list_fragment";

    private SourceCallbacks mCallbacks;

    private View mDetailContainer;

    private SourceDetailFragment mDetailFragment;

    private DrawerListener mDrawerListener = new DrawerListener() {
        @Override
        public void onDrawerClosed(View arg0) {
        }

        @Override
        public void onDrawerOpened(View arg0) {
        }

        @Override
        public void onDrawerSlide(View arg0, float arg1) {
        }

        @Override
        public void onDrawerStateChanged(final int newState) {
            if (mSlidingContainer.isOpen()) {
                if (newState == DrawerLayout.STATE_DRAGGING
                        || newState == DrawerLayout.STATE_SETTLING) {
                    mSlidingContainer.closePane();
                }
            }
        }
    };

    private SourceListFragment mListFragment;

    private SlidingPaneLayout mSlidingContainer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof SourceCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (SourceCallbacks)activity;
    }

    @Override
    public void onSourceDeleted(long id) {
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.remove(mDetailFragment);
        ft.commit();

        mDetailFragment = null;
        mSlidingContainer.removeView(mDetailContainer);
        mListFragment.deselectItem();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_designer_assets, container, false);
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks.removeDrawerListener(mDrawerListener);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mCallbacks.addDrawerListener(mDrawerListener);

        mSlidingContainer = (SlidingPaneLayout)view.findViewById(R.id.sliding_container);
        mSlidingContainer.setParallaxDistance((int)getResources().getDimension(
                R.dimen.sliding_container_parallax));

        mDetailContainer = view.findViewById(R.id.asset_detail_container);

        FragmentManager fm = getChildFragmentManager();

        // Attach asset list.
        mListFragment = (SourceListFragment)getChildFragmentManager().findFragmentByTag(
                TAG_LIST_FRAGMENT);
        if (mListFragment == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            mListFragment = new SourceListFragment();
            ft.replace(R.id.asset_list_container, mListFragment, TAG_LIST_FRAGMENT);
            ft.commit();
        }

        // Check to see if we have a detail panel.
        mDetailFragment = (SourceDetailFragment)fm.findFragmentByTag(TAG_DETAIL_FRAGMENT);
        if (mDetailFragment != null) {
            mDetailFragment.setOnSourceDeletedListener(this);
            mSlidingContainer.closePane();
        } else {
            mSlidingContainer.removeView(mDetailContainer);
        }

        // Register listener so we can push asset selection through to the
        // detail view.
        mListFragment.setShowSourceListener(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mSlidingContainer.isSlideable()) {
                    mSlidingContainer.setShadowResource(R.drawable.opal_list_background);
                    mSlidingContainer.invalidate();
                } else {
                    mSlidingContainer.setShadowResource(R.drawable.opal_list_background_flat);
                    mSlidingContainer.invalidate();
                }
            }
        }, 100);
    }

    @Override
    public void showSource(long id) {
        FragmentManager fm = getChildFragmentManager();

        mSlidingContainer.removeView(mDetailContainer);
        mSlidingContainer.addView(mDetailContainer);
        FragmentTransaction ft = fm.beginTransaction();
        mDetailFragment = SourceDetailFragment.newInstance(id);
        mDetailFragment.setOnSourceDeletedListener(this);
        ft.replace(R.id.asset_detail_container, mDetailFragment, TAG_DETAIL_FRAGMENT);
        ft.commit();
        mDetailContainer.setVisibility(View.VISIBLE);

        new Handler().post(new Runnable() {
            @Override
            public void run() {
                mSlidingContainer.closePane();

                if (mSlidingContainer.isSlideable()) {
                    mSlidingContainer.setShadowResource(R.drawable.opal_list_background);
                    mSlidingContainer.invalidate();
                } else {
                    mSlidingContainer.setShadowResource(R.drawable.opal_list_background_flat);
                    mSlidingContainer.invalidate();
                }
            }
        });
    }
}
