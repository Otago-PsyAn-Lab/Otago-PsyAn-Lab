
package nz.ac.otago.psyanlab.common.designer.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class HashMapAdapter<K, V> extends FragmentPagerAdapter {
    private HashMap<K, V> mItems;

    private Object[] mKeys;

    private FragmentFactory<K, V> mFactory;

    private ArrayList<Fragment> mFragments;

    public HashMapAdapter(FragmentManager fm, FragmentFactory<K, V> factory, HashMap<K, V> items) {
        super(fm);
        mFactory = factory;
        mItems = items;
        Set<K> keySet = mItems.keySet();
        mKeys = keySet.toArray();
        mFragments = new ArrayList<Fragment>();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Fragment getItem(int position) {
        Fragment f = mFragments.get(position);
        if (f == null) {
            f = mFactory.getFragment((K)mKeys[position], mItems.get((K)mKeys[position]));
        }
        return f;
    }

    public interface FragmentFactory<K, V> {
        Fragment getFragment(K key, V value);
    }
}
