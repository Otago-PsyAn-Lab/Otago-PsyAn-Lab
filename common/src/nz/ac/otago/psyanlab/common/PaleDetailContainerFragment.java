
package nz.ac.otago.psyanlab.common;

import nz.ac.otago.psyanlab.common.carousel.FlexibleIndirectViewPagerAdapter;
import nz.ac.otago.psyanlab.common.carousel.FragmentCarouselView;
import nz.ac.otago.psyanlab.common.carousel.TouchInterceptFrameLayout;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A fragment that responsively composes user details and experiments.
 */
public class PaleDetailContainerFragment extends Fragment {
    private static final String KEY_CURRENT_PAGE_INDEX = "current_page_index";

    private static final String KEY_USER_URI = "user_uri";

    /**
     * There are three possible layouts for the user screen: TWO_COLUMN,
     * VIEW_PAGER_AND_TAB_CAROUSEL, FRAGMENT_CAROUSEL.
     */
    private static interface LayoutMode {
        /**
         * Tall and wide screen with details and updates shown side-by-side.
         */
        static final int TWO_COLUMN = 0;

        /**
         * Tall and narrow screen to allow swipe between the details and
         * updates.
         */
        static final int VIEW_PAGER_AND_TAB_CAROUSEL = 1;

        /**
         * Short and wide screen to allow part of the other page to show.
         */
        static final int FRAGMENT_CAROUSEL = 2;
    }

    private int mLayoutMode;

    private FragmentCarouselView mFragmentCarousel;

    private PaleDetailFragment mDetailFragment;

    private PaleRecordListFragment mRecordsFragment;

    private ViewPager mViewPager;

    private Uri mUserUri;

    private LayoutInflater mLayoutInflater;

    private FlexibleIndirectViewPagerAdapter mViewPagerAdapter;

    private OnPageChangeListener mOnPageChangeListener;

    private View mDetailFragmentView;

    private View mExperimentsFragmentView;

    private UserExperimentDelegateI mExperimentDelegate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mLayoutInflater = inflater;
        return inflater.inflate(R.layout.fragment_pale_detail_container, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedState) {
        super.onViewCreated(view, savedState);

        // Retrieve views in case this is view pager and carousel mode
        mViewPager = (ViewPager)view.findViewById(R.id.pager);

        // Retrieve view in case this is in fragment carousel mode
        mFragmentCarousel = (FragmentCarouselView)view.findViewById(R.id.fragment_carousel);

        // Retrieve container views in case they are already in the XML layout
        mDetailFragmentView = view.findViewById(R.id.left_fragment_container);
        mExperimentsFragmentView = view.findViewById(R.id.right_fragment_container);

        if (mViewPager != null) {
            mLayoutMode = LayoutMode.VIEW_PAGER_AND_TAB_CAROUSEL;
        } else if (mFragmentCarousel != null) {
            mLayoutMode = LayoutMode.FRAGMENT_CAROUSEL;
        } else {
            mLayoutMode = LayoutMode.TWO_COLUMN;
        }

        boolean fragmentsAddedToFragmentManager = true;
        FragmentManager fm = getChildFragmentManager();
        mDetailFragment = (PaleDetailFragment)fm.findFragmentByTag(PaleDetailFragment.TAG);
        mRecordsFragment = (PaleRecordListFragment)fm.findFragmentByTag(PaleRecordListFragment.TAG);

        if (mDetailFragment == null) {
            fragmentsAddedToFragmentManager = false;
            mDetailFragment = new PaleDetailFragment();
            mRecordsFragment = new PaleRecordListFragment();
        }

        int currentPageIndex = 0;
        if (savedState != null) {
            mUserUri = savedState.getParcelable(KEY_USER_URI);
            currentPageIndex = savedState.getInt(KEY_CURRENT_PAGE_INDEX, 0);
        }

        switch (mLayoutMode) {
            case LayoutMode.VIEW_PAGER_AND_TAB_CAROUSEL:
                mDetailFragmentView = mLayoutInflater.inflate(
                        R.layout.pale_detail_fragment_container, mViewPager, false);
                mExperimentsFragmentView = mLayoutInflater.inflate(
                        R.layout.pale_records_fragment_container, mViewPager, false);

                mViewPagerAdapter = new FlexibleIndirectViewPagerAdapter();
                mViewPagerAdapter.add(mDetailFragmentView);
                mViewPagerAdapter.add(mExperimentsFragmentView);

                mViewPager.addView(mDetailFragmentView);
                mViewPager.addView(mExperimentsFragmentView);
                mViewPager.setAdapter(mViewPagerAdapter);
                mViewPager.setOnPageChangeListener(mOnPageChangeListener);

                if (!fragmentsAddedToFragmentManager) {
                    addFragments(fm);
                }

                break;
            case LayoutMode.FRAGMENT_CAROUSEL:
                if (!fragmentsAddedToFragmentManager) {
                    addFragments(fm);
                }

                mFragmentCarousel.setFragmentViews((TouchInterceptFrameLayout)mDetailFragmentView,
                        (TouchInterceptFrameLayout)mExperimentsFragmentView);
                mFragmentCarousel.setCurrentPage(currentPageIndex);
                break;
            case LayoutMode.TWO_COLUMN:
                if (!fragmentsAddedToFragmentManager) {
                    addFragments(fm);
                }
                break;
        }

        if (savedState != null) {
            showPale();
        }
    }

    private void addFragments(FragmentManager fm) {
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.add(R.id.left_fragment_container, mDetailFragment, PaleDetailFragment.TAG);
        transaction
                .add(R.id.right_fragment_container, mRecordsFragment, PaleRecordListFragment.TAG);
        transaction.commitAllowingStateLoss();
        fm.executePendingTransactions();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(KEY_USER_URI, mUserUri);
        outState.putInt(KEY_CURRENT_PAGE_INDEX, getCurrentPageIndex());
    }

    private int getCurrentPageIndex() {
        if (mViewPager != null) {
            return mViewPager.getCurrentItem();
        } else if (mFragmentCarousel != null) {
            return mFragmentCarousel.getCurrentPage();
        }

        return 0;
    }

    private void showPale() {
        if (mExperimentDelegate == null) {
            return;
        }
        switch (mLayoutMode) {
            case LayoutMode.TWO_COLUMN:
                break;
            case LayoutMode.VIEW_PAGER_AND_TAB_CAROUSEL:
                resetViewPager();
                break;
            case LayoutMode.FRAGMENT_CAROUSEL:
                mFragmentCarousel.reset();
                mFragmentCarousel.enableSwipe(true);
                mFragmentCarousel.animateAppear();
                break;
            default:
                throw new IllegalStateException("Invalid LayoutMode " + mLayoutMode);
        }

        resetFragments();

        mDetailFragment.setExperimentDelegate(mExperimentDelegate);
        mRecordsFragment.setExperimentDelegate(mExperimentDelegate);
    }

    private void resetFragments() {
        mRecordsFragment.resetAdapter();
    }

    private void resetViewPager() {
        mViewPager.setCurrentItem(0, false);
    }

    public void setExperimentDelegate(UserExperimentDelegateI experimentDelegate) {
        mExperimentDelegate = experimentDelegate;
        showPale();
    }
}
