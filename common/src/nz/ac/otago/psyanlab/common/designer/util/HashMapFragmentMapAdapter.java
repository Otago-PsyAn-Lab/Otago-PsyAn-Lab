
package nz.ac.otago.psyanlab.common.designer.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Set;

public class HashMapFragmentMapAdapter<V> extends FragmentPagerAdapter {
    private HashMap<String, V> mItems;

    private String[] mKeys;

    private Factory<V> mFactory;

    private SparseArray<Fragment> mFragments;

    public HashMapFragmentMapAdapter(FragmentManager fm, Factory<V> factory, HashMap<String, V> items) {
        super(fm);
        mFactory = factory;
        mItems = items;
        Set<String> keySet = mItems.keySet();
        mKeys = keySet.toArray(new String[mItems.size()]);
        Arrays.sort(mKeys);
        mFragments = new SparseArray<Fragment>();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (String)mKeys[position];
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = mFragments.get(position);
        if (f == null) {
            f = mFactory.getFragment((String)mKeys[position], mItems.get(mKeys[position]));
            mFragments.put(position, f);
        }
        return f;
    }

    public interface Factory<V> {
        Fragment getFragment(String title, V value);
    }
}
