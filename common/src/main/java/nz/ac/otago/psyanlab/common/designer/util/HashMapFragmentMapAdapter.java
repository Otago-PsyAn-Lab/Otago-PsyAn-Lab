
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
