
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

public class EditorSectionManager {
    private int mContainerResId;

    private final FragmentActivity mContext;

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
            Bundle bundle = (Bundle)state;
            bundle.setClassLoader(loader);
            Parcelable[] fragmentSavedStates = bundle.getParcelableArray("states");
            int[] stateKeys = bundle.getIntArray("state_keys");
            mSavedState.clear();
            if (fragmentSavedStates != null) {
                for (int i = 0; i < fragmentSavedStates.length; i++) {
                    mSavedState.put(stateKeys[i], (Fragment.SavedState)fragmentSavedStates[i]);
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
