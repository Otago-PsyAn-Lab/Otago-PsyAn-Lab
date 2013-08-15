
package nz.ac.otago.psyanlab.common.carousel;

import nz.ac.otago.psyanlab.common.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class FlexibleFragmentPagerAdapter extends PagerAdapter {
    private static final String ARG_NUM_FRAGMENTS = "arg_num_fragments";

    public static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    public static interface TitleFragment {
        CharSequence getTitle();
    }

    private List<Fragment> mFragments;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private int mMaxFragmentsSeen;
    private Fragment mCurrentPrimaryFragment;

    public FlexibleFragmentPagerAdapter(FragmentManager fm) {
        mFragmentManager = fm;
    }

    /**
     * Add a new fragment to the adapter. Triggers the view pager to update.
     * 
     * @param fragment Fragment to add.
     */
    public void add(Fragment fragment) {
        mFragments.add(fragment);
        notifyDataSetChanged();
    }

    /**
     * Insert a fragment into the adapter data set at some position. Tiggers the
     * view pager to update.
     * 
     * @param position Position to insert into.
     * @param fragment Fragment to add.
     * @param title Title of fragment.
     */
    public void insert(int position, Fragment fragment) {
        mFragments.add(position, fragment);
        notifyDataSetChanged();
    }

    @Override
    public void destroyItem(View container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.detach((Fragment)object);
    }

    @Override
    public void finishUpdate(View container) {
        if (mCurTransaction != null) {
            mCurTransaction.commitAllowingStateLoss();
            mCurTransaction = null;
            mFragmentManager.executePendingTransactions();
        }
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    public Fragment getFragment(int position) {
        return mFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        Fragment fragment = mFragments.get(position);
        if (fragment instanceof TitleFragment) {
            return ((TitleFragment)fragment).getTitle();
        }
        return null;
    }

    public Bundle getState() {
        Bundle out = new Bundle();
        out.putInt(ARG_NUM_FRAGMENTS, mMaxFragmentsSeen);
        return out;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        if (mMaxFragmentsSeen < position + 1) {
            mMaxFragmentsSeen = position + 1;
        }
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }

        // Do we already have this fragment?
        String name = makeFragmentName(container.getId(), position);
        Fragment fragment = mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            mCurTransaction.attach(fragment);
        } else {
            fragment = getFragment(position);
            mCurTransaction.add(container.getId(), fragment,
                    makeFragmentName(container.getId(), position));
        }
        if (fragment != mCurrentPrimaryFragment) {
            fragment.setMenuVisibility(false);
        }

        return fragment;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    public void loadState(Bundle in) {
        mMaxFragmentsSeen = in.getInt(ARG_NUM_FRAGMENTS);

        for (int i = 0; i < mMaxFragmentsSeen; i++) {
            Fragment f = mFragmentManager.findFragmentByTag(makeFragmentName(
                    R.id.pager, i));
            mFragments.add(i, f);
        }

        notifyDataSetChanged();
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void setPrimaryItem(View container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryFragment) {
            if (mCurrentPrimaryFragment != null) {
                mCurrentPrimaryFragment.setMenuVisibility(false);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
            }
            mCurrentPrimaryFragment = fragment;
        }
    }
}
