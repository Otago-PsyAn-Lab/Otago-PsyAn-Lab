
package nz.ac.otago.psyanlab.common.designer.program.util;

import nz.ac.otago.psyanlab.common.R;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class ProgramPagerAdapter extends PagerAdapter {
    private static final String ARG_NUM_FRAGMENTS = "arg_num_fragments";

    protected static String makeFragmentName(int viewId, int index) {
        return "android:switcher:" + viewId + ":" + index;
    }

    private FragmentTransaction mCurTransaction = null;

    private FragmentManager mFragmentManager;

    private int mMaxFragmentsSeen;

    private ArrayList<Fragment> mFragments = new ArrayList<Fragment>();

    private Fragment mCurrentPrimaryItem;

    private Activity mActivity;

    private ViewGroup mPager;

    public ProgramPagerAdapter(Activity activity, FragmentManager fragmentManager, ViewGroup pager) {
        mActivity = activity;
        mFragmentManager = fragmentManager;
        mPager = pager;
    }

    public void addFragment(Fragment fragment) {
        mFragments.add(fragment);
        notifyDataSetChanged();
    }

    public void removeFragment(int position) {
        mFragments.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public float getPageWidth(int position) {
        Display display = mActivity.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int screenWidth = size.x;
        float desiredWidth;
        if (position % 2 == 0) {
            // Even position.
            desiredWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 540, mActivity
                    .getResources().getDisplayMetrics());
        } else {
            // Odd position.
            desiredWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 640, mActivity
                    .getResources().getDisplayMetrics());
        }

        if (desiredWidth > screenWidth) {
            return super.getPageWidth(position);
        }

        // Return fraction of screen our page will be.
        return desiredWidth / screenWidth;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        if (mCurTransaction == null) {
            mCurTransaction = mFragmentManager.beginTransaction();
        }
        mCurTransaction.remove((Fragment)object);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
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
            fragment = getItem(position);
            mCurTransaction.add(container.getId(), fragment,
                    makeFragmentName(container.getId(), position));
        }
        fragment.setMenuVisibility(false);
        fragment.setUserVisibleHint(false);

        return fragment;
    }

    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((Fragment)object).getView() == view;
    }

    public void loadState(Bundle in) {
        mMaxFragmentsSeen = in.getInt(ARG_NUM_FRAGMENTS);

        for (int i = 0; i < mMaxFragmentsSeen; i++) {
            Fragment f = mFragmentManager.findFragmentByTag(makeFragmentName(R.id.pager, i));
            mFragments.add(f);
        }

        notifyDataSetChanged();
    }

    @Override
    public void setPrimaryItem(View container, int position, Object object) {
        Fragment fragment = (Fragment)object;
        if (fragment != mCurrentPrimaryItem) {
            if (mCurrentPrimaryItem != null) {
                mCurrentPrimaryItem.setMenuVisibility(false);
                mCurrentPrimaryItem.setUserVisibleHint(true);
            }
            if (fragment != null) {
                fragment.setMenuVisibility(true);
                fragment.setUserVisibleHint(true);
            }
            mCurrentPrimaryItem = fragment;
        }
    }
}
