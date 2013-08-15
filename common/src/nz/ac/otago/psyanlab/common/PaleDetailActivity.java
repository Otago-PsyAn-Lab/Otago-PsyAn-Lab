/*
 Copyright (C) 2012, 2013 University of Otago, Tonic Artos <tonic.artos@gmail.com>

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

package nz.ac.otago.psyanlab.common;

import nz.ac.otago.psyanlab.common.util.Args;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.MenuItem;

public class PaleDetailActivity extends FragmentActivity implements PaleDetailFragment.Callbacks,
        OnPageChangeListener {
    private UserExperimentDelegateI mUserExperimentDelegate;

    private ViewPager mPager;

    private Tab mExperimentTab;

    private Tab mRecordsTab;

    private ActionBar mActionBar;

    @Override
    public void onExperimentDeleted() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pale_detail);

        mActionBar = getActionBar();
        mActionBar.setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(Args.USER_EXPERIMENT_DELEGATE)) {
            mUserExperimentDelegate = getIntent().getParcelableExtra(Args.USER_EXPERIMENT_DELEGATE);
        } else {
            throw new IllegalStateException("User experiment delegate must be provided.");
        }

        mPager = (ViewPager)findViewById(R.id.pager);
        mPager.setAdapter(new FragmentPagerAdapterExtension(getSupportFragmentManager()));
        mPager.setOnPageChangeListener(this);

        mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        mExperimentTab = mActionBar.newTab()
                .setText(getResources().getString(R.string.label_experiment))
                .setTabListener(new TabListener() {
                    @Override
                    public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
                    }

                    @Override
                    public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
                        mPager.setCurrentItem(0);
                    }

                    @Override
                    public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
                    }
                });
        mActionBar.addTab(mExperimentTab);
        mRecordsTab = mActionBar.newTab().setText(getResources().getString(R.string.label_records))
                .setTabListener(new TabListener() {
                    @Override
                    public void onTabUnselected(Tab tab, android.app.FragmentTransaction ft) {
                    }

                    @Override
                    public void onTabSelected(Tab tab, android.app.FragmentTransaction ft) {
                        mPager.setCurrentItem(1);
                    }

                    @Override
                    public void onTabReselected(Tab tab, android.app.FragmentTransaction ft) {
                    }
                });
        mActionBar.addTab(mRecordsTab);
    }

    private final class FragmentPagerAdapterExtension extends FragmentPagerAdapter {
        private Fragment[] mFragments;

        private FragmentPagerAdapterExtension(FragmentManager fm) {
            super(fm);

            Bundle arguments = new Bundle();
            arguments.putParcelable(Args.USER_EXPERIMENT_DELEGATE, mUserExperimentDelegate);
            PaleDetailFragment experimentDetailFragment = new PaleDetailFragment();
            experimentDetailFragment.setArguments(arguments);
            PaleRecordListFragment experimentRecordsFragment = new PaleRecordListFragment();
            experimentRecordsFragment.setArguments(arguments);
            mFragments = new Fragment[] {
                    experimentDetailFragment, experimentRecordsFragment
            };
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public Fragment getItem(int pos) {
            return mFragments[pos];
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int pos) {
        mActionBar.setSelectedNavigationItem(pos);
    }
}
