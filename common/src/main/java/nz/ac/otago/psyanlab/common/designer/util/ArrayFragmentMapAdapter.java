
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
