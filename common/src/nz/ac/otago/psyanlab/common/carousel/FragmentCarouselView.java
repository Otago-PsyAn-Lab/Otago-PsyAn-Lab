/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nz.ac.otago.psyanlab.common.carousel;

import nz.ac.otago.psyanlab.common.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewPropertyAnimator;
import android.widget.HorizontalScrollView;

/**
 * This is a horizontally scrolling carousel with 2 fragments. Depending on the
 * scroll position and user selection of which fragment to currently view, the
 * touch interceptors over each fragment are configured accordingly.
 */
public class FragmentCarouselView extends HorizontalScrollView implements
        OnTouchListener {
    /**
     * Number of pixels that this view can be scrolled horizontally.
     */
    private int mAllowedHorizontalScrollLength = Integer.MIN_VALUE;
    /**
     * Minimum X scroll position that must be surpassed (if the user is on the
     * left page of the contact card), in order for this view to automatically
     * snap to the right page.
     */
    private int mLowerThreshold = Integer.MIN_VALUE;

    /**
     * Maximum X scroll position (if the user is on the right page of the
     * contact card), below which this view will automatically snap to the left
     * page.
     */
    private int mUpperThreshold = Integer.MIN_VALUE;

    /**
     * Minimum width of a fragment (if there is more than 1 fragment in the
     * carousel, then this is the width of one of the fragments).
     */
    private int mMinFragmentWidth = Integer.MIN_VALUE;

    /**
     * Fragment width (if there are 1+ fragments in the carousel) as defined as
     * a fraction of the screen width.
     */
    private static final float FRAGMENT_WIDTH_SCREEN_WIDTH_FRACTION = 0.85f;

    private static final int LEFT_PAGE = 0;
    private static final int RIGHT_PAGE = 1;

    private static final int MAX_FRAGMENT_VIEW_COUNT = 2;

    private boolean mEnableSwipe;

    private int mCurrentPage = LEFT_PAGE;
    private int mLastScrollPosition;

    private TouchInterceptFrameLayout mLeftFragment;
    private TouchInterceptFrameLayout mRightFragment;

    public FragmentCarouselView(Context context) {
        this(context, null);
    }

    public FragmentCarouselView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FragmentCarouselView(Context context, AttributeSet attrs,
            int defStyle) {
        super(context, attrs, defStyle);

        final LayoutInflater inflater = (LayoutInflater)context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.fragment_carousel, this);

        setOnTouchListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
        int screenHeight = MeasureSpec.getSize(heightMeasureSpec);

        // Take the width of this view as the width of the screen and compute
        // necessary thresholds.
        // Only do this computation 1x.
        if (mAllowedHorizontalScrollLength == Integer.MIN_VALUE) {
            mMinFragmentWidth = (int)(FRAGMENT_WIDTH_SCREEN_WIDTH_FRACTION * screenWidth);
            mAllowedHorizontalScrollLength = (MAX_FRAGMENT_VIEW_COUNT * mMinFragmentWidth)
                    - screenWidth;
            mLowerThreshold = (screenWidth - mMinFragmentWidth)
                    / MAX_FRAGMENT_VIEW_COUNT;
            mUpperThreshold = mAllowedHorizontalScrollLength - mLowerThreshold;
        }

        if (getChildCount() > 0) {
            View child = getChildAt(0);
            // If we enable swipe, then the {@link LinearLayout} child width
            // must be the sum of the
            // width of all its children fragments.
            if (mEnableSwipe) {
                child.measure(MeasureSpec.makeMeasureSpec(mMinFragmentWidth
                        * MAX_FRAGMENT_VIEW_COUNT, MeasureSpec.EXACTLY),
                        MeasureSpec.makeMeasureSpec(screenHeight,
                                MeasureSpec.EXACTLY));
            } else {
                // Otherwise, the {@link LinearLayout} child width will just be
                // the screen width
                // because it will only have 1 child fragment.
                child.measure(MeasureSpec.makeMeasureSpec(screenWidth,
                        MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(
                        screenHeight, MeasureSpec.EXACTLY));
            }
        }

        setMeasuredDimension(resolveSize(screenWidth, widthMeasureSpec),
                resolveSize(screenHeight, heightMeasureSpec));
    }

    /**
     * Set the current page. This dims out the non-selected page but doesn't do
     * any scrolling of the carousel.
     */
    public void setCurrentPage(int pageIndex) {
        mCurrentPage = pageIndex;

        updateTouchInterceptors();
    }

    /**
     * Set the view containers for the left and right fragment.
     */
    public void setFragmentViews(TouchInterceptFrameLayout left,
            TouchInterceptFrameLayout right) {
        mLeftFragment = left;
        mRightFragment = right;

        mLeftFragment
                .setOverlayOnClickListener(mLeftFragTouchInterceptListener);
        mRightFragment
                .setOverlayOnClickListener(mRightFragTouchInterceptListener);
    }

    /**
     * Enable swiping if the left and right fragments should be showing.
     * Otherwise disable swiping if only the left fragment should be showing.
     */
    public void enableSwipe(boolean enable) {
        if (mEnableSwipe != enable) {
            mEnableSwipe = enable;
            if (mRightFragment != null) {
                mRightFragment.setVisibility(enable ? View.VISIBLE : View.GONE);
                if (mCurrentPage == LEFT_PAGE) {
                    mLeftFragment.requestFocus();
                } else {
                    mRightFragment.requestFocus();
                }
                updateTouchInterceptors();
            }
        }
    }

    /**
     * Reset the fragment carousel to show the left page.
     */
    public void reset() {
        if (mCurrentPage != LEFT_PAGE) {
            mCurrentPage = LEFT_PAGE;
            snapToEdge();
        }
    }

    public int getCurrentPage() {
        return mCurrentPage;
    }

    private final OnClickListener mLeftFragTouchInterceptListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCurrentPage = LEFT_PAGE;
            snapToEdge();
        }
    };

    private final OnClickListener mRightFragTouchInterceptListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            mCurrentPage = RIGHT_PAGE;
            snapToEdge();
        }
    };

    private void updateTouchInterceptors() {
        // Disable the touch-interceptor on the selected page, and enable it on
        // the other.
        if (mLeftFragment != null) {
            mLeftFragment.setOverlayClickable(mCurrentPage != LEFT_PAGE);
        }
        if (mRightFragment != null) {
            mRightFragment.setOverlayClickable(mCurrentPage != RIGHT_PAGE);
        }
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (!mEnableSwipe) {
            return;
        }
        mLastScrollPosition = l;
    }

    private void snapToEdge() {
        final int x = mCurrentPage == LEFT_PAGE ? 0
                : mAllowedHorizontalScrollLength;
        smoothScrollTo(x, 0);
        updateTouchInterceptors();
    }

    /**
     * Returns the desired page we should scroll to based on the current X
     * scroll position and the current page.
     */
    private int getDesiredPage() {
        switch (mCurrentPage) {
            case LEFT_PAGE:
                // If the user is on the left page, and the scroll position
                // exceeds the lower threshold, then we should switch to the
                // right page.
                return (mLastScrollPosition > mLowerThreshold) ? RIGHT_PAGE
                        : LEFT_PAGE;
            case RIGHT_PAGE:
                // If the user is on the right page, and the scroll position
                // goes below the upper threshold, then we should switch to the
                // left page.
                return (mLastScrollPosition < mUpperThreshold) ? LEFT_PAGE
                        : RIGHT_PAGE;
        }
        throw new IllegalStateException("Invalid current page " + mCurrentPage);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!mEnableSwipe) {
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            mCurrentPage = getDesiredPage();
            snapToEdge();
            return true;
        }
        return false;
    }

    /**
     * Starts an "appear" animation by moving in the right page from the right.
     */
    public void animateAppear() {
        final int x = Math.round((1.0f - FRAGMENT_WIDTH_SCREEN_WIDTH_FRACTION)
                * getWidth());
        mRightFragment.setTranslationX(x);
        final ViewPropertyAnimator animator = mRightFragment.animate();
        animator.translationX(0.0f);
    }
}
