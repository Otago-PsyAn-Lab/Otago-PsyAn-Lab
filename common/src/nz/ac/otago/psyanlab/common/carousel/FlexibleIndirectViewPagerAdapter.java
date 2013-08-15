
package nz.ac.otago.psyanlab.common.carousel;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class FlexibleIndirectViewPagerAdapter extends PagerAdapter {
    /**
     * The initial value for the view count needs to be MAX_FRAGMENT_VIEW_COUNT,
     * otherwise anything smaller would break screen rotation functionality for
     * a user viewing a contact with social updates (i.e. the user was viewing
     * the second page, rotates the device, the view pager requires the second
     * page to exist immediately on launch).
     */
    private List<View> mViews;

    public FlexibleIndirectViewPagerAdapter() {
        mViews = new ArrayList<View>();
    }

    public void add(View view) {
        mViews.add(view);
        notifyDataSetChanged();
    }

    public void add(int position, View view) {
        mViews.add(position, view);
        notifyDataSetChanged();
    }

    public void removeLast() {
        mViews.remove(mViews.size() - 1);
        notifyDataSetChanged();
    }

    public void remove(int position) {
        mViews.remove(position);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mViews.size();
    }

    /** Gets called when the number of items changes. */
    @Override
    public int getItemPosition(Object object) {
        int index = mViews.indexOf(object);
        if (index == -1) {
            return POSITION_NONE;
        }
        return index;
    }

    @Override
    public void startUpdate(ViewGroup container) {
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = mViews.get(position);
        view.setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((View)object).setVisibility(View.GONE);
    }

    @Override
    public void finishUpdate(ViewGroup container) {
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return ((View)object) == view;
    }

    @Override
    public Parcelable saveState() {
        // SavedState ss = new SavedState();
        // ss.mProxyIds = new int[mViews.size()];
        // for (int i = 0; i < mViews.size(); i++) {
        // ss.mProxyIds[i] = mViews.get(i).getId();
        // }
        // return ss;
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        // SavedState ss = (SavedState)state;
        // for (int i = 0; i < ss.mProxyIds.length; i++) {
        //
        // }
    }

    // public Bundle getState() {
    // Bundle out = new Bundle();
    // int[] proxyIds = new int[mViews.size()];
    // for (int i = 0; i < mViews.size(); i++) {
    // proxyIds[i] = mViews.get(i).getId();
    // }
    // out.putIntArray(ARG_PROXY_IDS, proxyIds);
    // return out;
    // }
    //
    // public void loadState(Bundle in) {
    // int[] proxyIds = in.getIntArray(ARG_PROXY_IDS);
    // for (int i = 0; i < proxyIds.length; i++) {
    // View v = new View(mContext);
    // v.setLayoutParams(new LayoutParams(
    // ViewGroup.LayoutParams.MATCH_PARENT,
    // ViewGroup.LayoutParams.MATCH_PARENT));
    // v.setId(proxyIds[i]);
    // mViews.add(v);
    // }
    // notifyDataSetChanged();
    // }
    // public static class SavedState implements Parcelable {
    // int[] mProxyIds;
    //
    // SavedState(Parcelable superState) {
    // }
    //
    // public SavedState() {
    // }
    //
    // private SavedState(Parcel in) {
    // mProxyIds = in.createIntArray();
    // }
    //
    // @Override
    // public void writeToParcel(Parcel out, int flags) {
    // out.writeIntArray(mProxyIds);
    // }
    //
    // public static final Parcelable.Creator<SavedState> CREATOR = new
    // Parcelable.Creator<SavedState>() {
    // @Override
    // public SavedState createFromParcel(Parcel in) {
    // return new SavedState(in);
    // }
    //
    // @Override
    // public SavedState[] newArray(int size) {
    // return new SavedState[size];
    // }
    // };
    //
    // @Override
    // public int describeContents() {
    // return 0;
    // }
    // }
}
