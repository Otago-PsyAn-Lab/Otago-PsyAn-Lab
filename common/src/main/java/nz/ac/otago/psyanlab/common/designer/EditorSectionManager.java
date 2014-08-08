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

package nz.ac.otago.psyanlab.common.designer;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment.SavedState;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.util.SparseArrayCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class that manages adding and removing editor section fragments and maintains their
 * state.
 */
public class EditorSectionManager {
    private final FragmentActivity mContext;

    private int mContainerResId;

    private Fragment mCurrentFragment;

    private int mCurrentPosition;

    private FragmentManager mFragmentManager;

    private ArrayList<EditorSectionItem> mItems;

    private SparseArrayCompat<SavedState> mSavedState = new SparseArrayCompat<SavedState>();

    public EditorSectionManager(FragmentActivity activity, int containerResId,
                                FragmentManager fragmentManager) {
        mContext = activity;
        mItems = new ArrayList<EditorSectionItem>();
        mContainerResId = containerResId;
        mFragmentManager = fragmentManager;
    }

    public void addSection(int labelResId, Class<?> clazz, Bundle args) {
        EditorSectionItem info = new EditorSectionItem(labelResId, clazz, args);
        mItems.add(info);
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public Fragment getFragment(int position) {
        EditorSectionItem item = mItems.get(position);
        return Fragment.instantiate(mContext, item.clazz.getName(), item.args);
    }

    public List<EditorSectionItem> getItems() {
        return mItems;
    }

    public int getTitle(int position) {
        return mItems.get(position).titleId;
    }

    public void restoreState(Parcelable state, ClassLoader loader) {
        if (state != null) {
            // Store fragment saved states.
            Bundle bundle = (Bundle) state;
            bundle.setClassLoader(loader);
            Parcelable[] fragmentSavedStates = bundle.getParcelableArray("states");
            int[] stateKeys = bundle.getIntArray("state_keys");
            mSavedState.clear();
            if (fragmentSavedStates != null) {
                for (int i = 0; i < fragmentSavedStates.length; i++) {
                    mSavedState.put(stateKeys[i], (Fragment.SavedState) fragmentSavedStates[i]);
                }
            }

            mCurrentPosition = bundle.getInt("current_position");
        }
    }

    public Parcelable saveState() {
        Bundle state = null;

        saveCurrentFragmentState();
        if (mSavedState.size() > 0) {
            state = new Bundle();
            Fragment.SavedState[] fss = new Fragment.SavedState[mSavedState.size()];
            int[] keys = new int[mSavedState.size()];
            for (int i = 0; i < mSavedState.size(); i++) {
                int key = mSavedState.keyAt(i);
                keys[i] = key;
                fss[i] = mSavedState.get(key);
            }
            state.putParcelableArray("states", fss);
            state.putIntArray("state_keys", keys);
        }

        state.putInt("current_position", mCurrentPosition);

        return state;
    }

    public void selectSection(int position) {
        saveCurrentFragmentState();

        // Instantiate new fragment and load any saved state.
        mCurrentFragment = getFragment(position);
        mCurrentPosition = position;

        SavedState state = mSavedState.get(mCurrentPosition);
        if (state != null) {
            mCurrentFragment.setInitialSavedState(state);
        }

        // Do fragment transaction.
        FragmentTransaction ft = mFragmentManager.beginTransaction();
        ft.replace(mContainerResId, mCurrentFragment);
        ft.commit();
    }

    private void saveCurrentFragmentState() {
        if (mCurrentFragment != null) {
            mSavedState.put(mCurrentPosition,
                            mFragmentManager.saveFragmentInstanceState(mCurrentFragment));
        }
    }

    static final class EditorSectionItem {
        public final Bundle args;

        public final Class<?> clazz;

        public int titleId;

        EditorSectionItem(int titleId, Class<?> clazz, Bundle args) {
            this.titleId = titleId;
            this.clazz = clazz;
            this.args = args;
        }
    }
}
