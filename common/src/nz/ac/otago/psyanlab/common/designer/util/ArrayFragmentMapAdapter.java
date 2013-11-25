
package nz.ac.otago.psyanlab.common.designer.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import java.util.List;

public class ArrayFragmentMapAdapter extends FragmentPagerAdapter {
    private List<String> mItems;

    private Factory mFactory;

    private SparseArray<Fragment> mFragments;

    public ArrayFragmentMapAdapter(FragmentManager fm, Factory factory, List<String> items) {
        super(fm);
        mFactory = factory;
        mItems = items;
        mFragments = new SparseArray<Fragment>();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (String)mItems.get(position);
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = mFragments.get(position);
        if (f == null) {
            f = mFactory.getFragment(mItems.get(position), position);
            mFragments.put(position, f);
        }
        return f;
    }

    public interface Factory {
        Fragment getFragment(String title, int position);
    }
}
