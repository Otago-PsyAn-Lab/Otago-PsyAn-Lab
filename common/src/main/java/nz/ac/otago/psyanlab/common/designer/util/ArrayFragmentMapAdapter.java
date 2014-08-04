
package nz.ac.otago.psyanlab.common.designer.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import java.util.List;

public class ArrayFragmentMapAdapter extends FragmentPagerAdapter {
    private FragmentFactory mFactory;

    private SparseArray<Fragment> mFragments;

    private List<PageData> mItems;

    public ArrayFragmentMapAdapter(FragmentManager fm, FragmentFactory factory, List<PageData> items) {
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
    public Fragment getItem(int position) {
        Fragment f = mFragments.get(position);
        if (f == null) {
            String pageName = mItems.get(position).pageName;
            int scopeLevel = mItems.get(position).scopeLevel;
            f = mFactory.getFragment(pageName, scopeLevel);
            mFragments.put(position, f);
        }
        return f;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mItems.get(position).pageName;
    }

    /**
     * A factory that produces fragments based on the contents of an
     * ArrayFragmentMapAdapter.
     */
    public interface FragmentFactory {
        Fragment getFragment(String title, int scopeLevel);
    }

    public static class PageData {
        public String pageName;

        public int scopeLevel;

        public PageData(String pageName, int scopeLevel) {
            this.pageName = pageName;
            // TODO Auto-generated constructor stub
            this.scopeLevel = scopeLevel;
        }
    }
}
