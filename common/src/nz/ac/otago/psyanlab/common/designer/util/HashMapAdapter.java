
package nz.ac.otago.psyanlab.common.designer.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class HashMapAdapter<V> extends FragmentPagerAdapter {
    private HashMap<String, V> mItems;

    private Object[] mKeys;

    private FragmentFactory<V> mFactory;

    private ArrayList<Fragment> mFragments;

    public HashMapAdapter(FragmentManager fm, FragmentFactory<V> factory, HashMap<String, V> items) {
        super(fm);
        mFactory = factory;
        mItems = items;
        Set<String> keySet = mItems.keySet();
        mKeys = keySet.toArray();
        mFragments = new ArrayList<Fragment>();
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
        }
        return f;
    }

    public interface FragmentFactory<V> {
        Fragment getFragment(String title, V value);
    }
}
