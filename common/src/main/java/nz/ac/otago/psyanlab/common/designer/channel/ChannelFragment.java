
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

package nz.ac.otago.psyanlab.common.designer.channel;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.channel.ChannelDetailFragment.OnChannelDeletedListener;
import nz.ac.otago.psyanlab.common.designer.channel.ChannelListFragment.ShowChannelListener;

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

public class ChannelFragment extends Fragment implements ShowChannelListener,
        OnChannelDeletedListener {
    private static final String TAG_DETAIL_FRAGMENT = "channel_detail_fragment";

    private static final String TAG_LIST_FRAGMENT = "channel_list_fragment";

    private ChannelCallbacks mCallbacks;

    private View mDetailContainer;

    private ChannelDetailFragment mDetailFragment;

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

    private ChannelListFragment mListFragment;

    private SlidingPaneLayout mSlidingContainer;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ChannelCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (ChannelCallbacks)activity;
    }

    @Override
    public void onChannelDeleted() {
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
        mListFragment = (ChannelListFragment)getChildFragmentManager().findFragmentByTag(
                TAG_LIST_FRAGMENT);
        if (mListFragment == null) {
            FragmentTransaction ft = getChildFragmentManager().beginTransaction();
            mListFragment = new ChannelListFragment();
            ft.replace(R.id.asset_list_container, mListFragment, TAG_LIST_FRAGMENT);
            ft.commit();
        }

        // Check to see if we have a detail panel.
        mDetailFragment = (ChannelDetailFragment)fm.findFragmentByTag(TAG_DETAIL_FRAGMENT);
        if (mDetailFragment != null) {
            mDetailFragment.setOnChannelDeletedListener(this);
            mSlidingContainer.closePane();
        } else {
            mSlidingContainer.removeView(mDetailContainer);
        }

        // Register listener so we can push asset selection through to the
        // detail view.
        mListFragment.setShowChannelListener(this);

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
    public void showChannel(long id) {
        FragmentManager fm = getChildFragmentManager();

        mSlidingContainer.removeView(mDetailContainer);
        mSlidingContainer.addView(mDetailContainer);
        FragmentTransaction ft = fm.beginTransaction();
        mDetailFragment = ChannelDetailFragment.newInstance(id);
        mDetailFragment.setOnChannelDeletedListener(this);
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
