
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.stage.StageView.StageAdapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.support.v4.util.LongSparseArray;
import android.support.v4.util.SparseArrayCompat;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.util.StateSet;
import android.view.HapticFeedbackConstants;
import android.view.MotionEvent;
import android.view.SoundEffectConstants;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Adapter;
import android.widget.AdapterView;

import java.util.ArrayList;
import java.util.Arrays;

public class StageView extends AdapterView<StageAdapter> {
    public static final int INVALID_POSITION = -1;

    private static final int NO_MATCHED_CHILD = INVALID_POSITION;

    protected static final int TOUCH_MODE_AT_REST = -0x01;

    protected static final int TOUCH_MODE_DONE_WAITING = 0x02;

    protected static final int TOUCH_MODE_DOWN = 0x00;

    protected static final int TOUCH_MODE_FINISHED_LONG_PRESS = -0x02;

    protected static final int TOUCH_MODE_TAP = 0x01;

    public StageAdapter mAdapter;

    public boolean mDataChanged;

    public int mItemCount;

    public int mOldItemCount;

    private ArrayList<View> mCachedViews = new ArrayList<View>();

    private DataSetObserver mDataSetObserver;

    private int[] mForceFingersExemptions = new int[] {};

    private int mForceFingersWhenEmpty = 1;

    private int mNativeHeight;

    private int mNativeWidth;

    private SparseArray<OnStageClickListener> mOnStageClickListeners = new SparseArray<StageView.OnStageClickListener>(
            1);

    private SparseArray<OnStageLongClickListener> mOnStageLongClickListeners = new SparseArray<StageView.OnStageLongClickListener>(
            1);

    private CheckForLongPress mPendingCheckForLongPress;

    private CheckForTap mPendingCheckForTap;

    private PerformClick mPerformPropClick;

    private float mScaleFactor;

    private Rect mSelectorRect = new Rect();

    private SparseArray<Drawable> mSelectors = new SparseArray<Drawable>();

    /**
     * Rect for hit testing children.
     */
    private Rect mTouchFrame;

    private Runnable mTouchModeReset;

    private LongSparseArray<View> mViewIdMap = new LongSparseArray<View>();

    protected int mMaxFingersDown;

    protected int mMotionPosition;

    protected SparseArrayCompat<Float> mMotionX = new SparseArrayCompat<Float>(10);

    protected SparseArrayCompat<Float> mMotionY = new SparseArrayCompat<Float>(10);

    protected int mTouchMode;

    protected int mTouchSlop;

    public StageView(Context context) {
        super(context);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public StageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public StageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    public long childViewPositionToId(int clickMotionPosition) {
        // TODO Auto-generated method stub
        return INVALID_POSITION;
    }

    /**
     * Exempt multi-touch interaction from forced adapter empty condition.
     * 
     * @param fingers Number of fingers identifying the multi-touch action to be
     *            exempted.
     */
    public void exemptMultiTouchFromEmptyCondition(int... fingers) {
        mForceFingersExemptions = fingers;
    }

    /**
     * Force the stage to trigger a multi-touch action on click when the adapter
     * is empty.
     * 
     * @param fingers Number of fingers to simulate in multi-touch action.
     */
    public void forceMultiTouchWhenEmpty(int fingers) {
        mForceFingersWhenEmpty = fingers;
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public StageAdapter getAdapter() {
        return mAdapter;
    }

    public int getNativeHeight() {
        return mNativeHeight;
    }

    public int getNativeWidth() {
        return mNativeWidth;
    }

    @Override
    public View getSelectedView() {
        throw new RuntimeException("Unsupport method: View setSelectedView()");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            // Ignore touch events if not enabled.
            return false;
        }

        final int action = event.getAction();
        final int pointerCount = event.getPointerCount();
        final Handler handler = getHandler();

        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN: {
                // Throw away event if we have already seen at least this many
                // fingers before.
                if (mMaxFingersDown > pointerCount) {
                    return true;
                }

                if (handler != null) {
                    handler.removeCallbacks(mPendingCheckForTap);
                    handler.removeCallbacks(mPendingCheckForLongPress);
                }

                mPendingCheckForTap = new CheckForTap();
                postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());

                mMaxFingersDown = pointerCount;

                mMotionPosition = INVALID_POSITION;
                updateMotionCoords(event, pointerCount);

                mTouchMode = TOUCH_MODE_DOWN;

                return true;
            }

            case MotionEvent.ACTION_DOWN: {
                mMaxFingersDown = pointerCount;

                if (mPendingCheckForTap == null) {
                    mPendingCheckForTap = new CheckForTap();
                }
                postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());

                updateMotionCoords(event, pointerCount);
                mMotionPosition = pointToPosition(mMotionX.get(0).intValue(), mMotionY.get(0)
                        .intValue());

                mTouchMode = TOUCH_MODE_DOWN;

                return true;
            }

            case MotionEvent.ACTION_MOVE: {
                if (mMaxFingersDown == 1
                        && mMotionPosition == pointToPosition((int)event.getX(), (int)event.getY())) {
                    // Ignore movement in single touch mode until the user has
                    // moved out of the prop hit area.
                    return true;
                }

                boolean moveIsOverSlop = false;
                int touchSlop = (mMaxFingersDown > 1) ? mTouchSlop * 6 : mTouchSlop;
                for (int pointerIndex = 0; pointerIndex < pointerCount; pointerIndex++) {
                    int pointerId = event.getPointerId(pointerIndex);
                    moveIsOverSlop = moveIsOverSlop
                            || (Math.abs(event.getY(pointerIndex) - mMotionY.get(pointerId)) > touchSlop || Math
                                    .abs(event.getX(pointerIndex) - mMotionX.get(pointerId)) > touchSlop);
                }

                if (mTouchMode != TOUCH_MODE_AT_REST
                        && (getVirtualFingers() > 1 || mMotionPosition != NO_MATCHED_CHILD)
                        && moveIsOverSlop) {
                    // Too much movement to be a tap event.
                    mTouchMode = TOUCH_MODE_AT_REST;
                    final View child = getChildAt(mMotionPosition);
                    if (child != null) {
                        child.setPressed(false);
                    }
                    setPressed(false);
                    if (handler != null) {
                        handler.removeCallbacks(mPendingCheckForLongPress);
                    }
                    mMotionPosition = NO_MATCHED_CHILD;
                    updateSelectorState();
                    invalidate();
                }
                return true;
            }

            case MotionEvent.ACTION_UP: {
                if (mTouchMode == TOUCH_MODE_FINISHED_LONG_PRESS) {
                    return true;
                }

                if (mTouchMode == TOUCH_MODE_AT_REST) {
                    break;
                }

                // Handle stage multi-touch.

                if (getVirtualFingers() > 1) {
                    if (mPerformPropClick == null) {
                        mPerformPropClick = new PerformClick();
                    }

                    final PerformClick performPropClick = mPerformPropClick;
                    performPropClick.mClickMotionPosition = mMotionPosition;
                    performPropClick.rememberWindowAttachCount();

                    if (mTouchMode != TOUCH_MODE_DOWN || mTouchMode != TOUCH_MODE_TAP) {
                        if (handler != null) {
                            handler.removeCallbacks(mTouchMode == TOUCH_MODE_DOWN ? mPendingCheckForTap
                                    : mPendingCheckForLongPress);
                        }

                        if (!mDataChanged) {
                            // Got here so must be a tap. The long press would
                            // have triggered inside the delayed runnable.
                            mTouchMode = TOUCH_MODE_TAP;
                            positionSelector(this);
                            setPressed(true);
                            updateSelectorState();
                            invalidate();

                            resetSelectorTransition(getVirtualFingers());

                            if (mTouchModeReset != null) {
                                removeCallbacks(mTouchModeReset);
                            }
                            mTouchModeReset = new Runnable() {
                                @Override
                                public void run() {
                                    mTouchMode = TOUCH_MODE_AT_REST;
                                    setPressed(false);
                                    if (!mDataChanged) {
                                        performPropClick.run();
                                    }
                                    updateSelectorState();
                                    invalidate();
                                }
                            };
                            postDelayed(mTouchModeReset,
                                    ViewConfiguration.getPressedStateDuration());
                        } else {
                            mTouchMode = TOUCH_MODE_AT_REST;
                        }
                    } else if (!mDataChanged) {
                        performPropClick.run();
                    }
                } else {

                    // Handle touch on child.

                    if (mMotionPosition == NO_MATCHED_CHILD) {
                        break;
                    }

                    final View child = getChildAt(mMotionPosition);
                    if (child != null && !child.hasFocusable()) {
                        if (mTouchMode != TOUCH_MODE_DOWN) {
                            child.setPressed(false);
                        }

                        if (mPerformPropClick == null) {
                            mPerformPropClick = new PerformClick();
                        }

                        final PerformClick performPropClick = mPerformPropClick;
                        performPropClick.mClickMotionPosition = mMotionPosition;
                        performPropClick.rememberWindowAttachCount();

                        if (mTouchMode != TOUCH_MODE_DOWN || mTouchMode != TOUCH_MODE_TAP) {
                            if (handler != null) {
                                handler.removeCallbacks(mTouchMode == TOUCH_MODE_DOWN ? mPendingCheckForTap
                                        : mPendingCheckForLongPress);
                            }

                            if (!mDataChanged) {
                                // Got here so must be a tap. The long press
                                // would
                                // have triggered inside the delayed runnable.
                                mTouchMode = TOUCH_MODE_TAP;
                                child.setPressed(true);
                                positionSelector(child);
                                setPressed(true);
                                updateSelectorState();
                                invalidate();

                                resetSelectorTransition(getVirtualFingers());

                                if (mTouchModeReset != null) {
                                    removeCallbacks(mTouchModeReset);
                                }
                                mTouchModeReset = new Runnable() {
                                    @Override
                                    public void run() {
                                        mTouchMode = TOUCH_MODE_AT_REST;
                                        child.setPressed(false);
                                        setPressed(false);
                                        updateSelectorState();
                                        invalidate();
                                        if (!mDataChanged) {
                                            performPropClick.run();
                                        }
                                    }
                                };
                                postDelayed(mTouchModeReset,
                                        ViewConfiguration.getPressedStateDuration());
                            } else {
                                mTouchMode = TOUCH_MODE_AT_REST;
                                updateSelectorState();
                                invalidate();
                            }
                        } else if (!mDataChanged) {
                            performPropClick.run();
                        }
                    }
                }
                return true;
            }
        }
        return true;
    }

    public boolean performLongPress(View view, int position, long id) {
        OnItemLongClickListener listener = getOnItemLongClickListener();
        if (listener != null) {
            return doLongPressFeedback(listener.onItemLongClick(this, view, position, id), view);
        }
        return false;
    }

    public boolean performStageMultipleFingerClick(int fingersDown) {
        OnStageClickListener listener = mOnStageClickListeners.get(fingersDown);
        if (listener != null) {
            playSoundEffect(SoundEffectConstants.CLICK);
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_CLICKED);
            listener.onStageClick(this);
            return true;
        }

        return false;
    }

    public boolean performStageMultipleFingerLongPress(int fingersDown) {
        OnStageLongClickListener listener = mOnStageLongClickListeners.get(fingersDown);
        if (listener != null) {
            return doLongPressFeedback(listener.onStageLongClick(this), this);
        }
        return false;
    }

    @Override
    public void setAdapter(StageAdapter adapter) {
        if (null != mAdapter) {
            mAdapter.unregisterDataSetObserver(mDataSetObserver);
        }

        resetStage();

        mAdapter = adapter;

        if (mAdapter != null) {
            mItemCount = mAdapter.getCount();

            mDataSetObserver = new AdapterDataSetObserver();
            mAdapter.registerDataSetObserver(mDataSetObserver);
        }

        requestLayout();
    }

    public void setItemSelector(Drawable s) {
        if (mSelectors.size() > 0) {
            Drawable selector = mSelectors.get(0);
            selector.setCallback(null);
            unscheduleDrawable(selector);
        }
        mSelectors.put(0, s);
        s.setCallback(this);
        updateSelectorState();
    }

    public void setNativeHeight(int height) {
        mNativeHeight = height;
        requestLayout();
        invalidate();
    }

    public void setNativeWidth(int width) {
        mNativeWidth = width;
        requestLayout();
        invalidate();
    }

    /**
     * Set a listener for a multi-touch click event.
     * 
     * @param numFingers Number of fingers listening for.
     * @param listener Click listener.
     */
    public void setOnStageClickListener(int numFingers, OnStageClickListener listener) {
        mOnStageClickListeners.put(numFingers, listener);
    }

    /**
     * Set a listener for a multi-touch long click event.
     * 
     * @param numFingers Number of fingers listening for.
     * @param listener Click listener.
     */
    public void setOnStageLongClickListener(int numFingers, OnStageLongClickListener listener) {
        mOnStageLongClickListeners.put(numFingers, listener);
    }

    @Override
    public void setSelection(int position) {
        throw new RuntimeException("Unsupport method: setSelection(int)");
    }

    public void setSelector(int fingers, Drawable s) {
        Drawable d = mSelectors.get(fingers);
        if (d != null) {
            d.setCallback(null);
            unscheduleDrawable(d);
        }
        mSelectors.put(fingers, s);
        s.setCallback(this);
        updateSelectorState();
    }

    /**
     * Send any events and feedback that the long press action has taken place.
     * 
     * @param handled True if the long press has taken place.
     * @param view The view the long press was on.
     * @return Pass through of 'handled' parameter.
     */
    private boolean doLongPressFeedback(final boolean handled, final View view) {
        if (handled) {
            if (view != null) {
                view.sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_LONG_CLICKED);
            }
            performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);
        }
        return handled;
    }

    private void drawSelector(Canvas canvas) {
        if (!mSelectorRect.isEmpty()) {
            Drawable selector;

            selector = mSelectors.get(getVirtualFingers());
            if (selector == null) {
                selector = mSelectors.get(0);
            }

            selector.setBounds(mSelectorRect);
            selector.draw(canvas);
        }
    }

    private void layoutAdapterChildren() {
        if (mAdapter == null) {
            return;
        }

        detachAllViewsFromParent();

        int numChildren = mAdapter.getCount();
        boolean[] added = new boolean[1];
        for (int i = 0; i < numChildren; i++) {
            View child = obtainView(i, added);
            LayoutParams params = (LayoutParams)child.getLayoutParams();
            int left = (int)(params.xPosition * mScaleFactor);
            int top = (int)(params.yPosition * mScaleFactor);

            int childWidthSpec = MeasureSpec.makeMeasureSpec((int)(params.width * mScaleFactor),
                    MeasureSpec.EXACTLY);
            int childHeightSpec = MeasureSpec.makeMeasureSpec((int)(params.height * mScaleFactor),
                    MeasureSpec.EXACTLY);
            child.measure(childWidthSpec, childHeightSpec);

            child.layout(left, top, left + child.getMeasuredWidth(),
                    top + child.getMeasuredHeight());

            if (added[0]) {
                attachViewToParent(child, i, params);
            } else {
                addViewInLayout(child, i, params, true);
            }
        }

        for (int i = 0; i < mViewIdMap.size(); i++) {
            View view = mViewIdMap.valueAt(i);
            if (view.getParent() == null) {
                removeDetachedView(view, false);
                mCachedViews.add(view);
            }
        }

        mDataChanged = false;
    }

    private View obtainView(int position, boolean[] added) {
        long id = mAdapter.getItemId(position);
        View view = mViewIdMap.get(id);

        boolean newView = view == null;

        if (newView) {
            added[0] = false;
            if (mCachedViews.size() > 0) {
                view = mCachedViews.remove(0);
            }
        } else {
            added[0] = true;
        }

        view = mAdapter.getView(position, view, this);

        if (newView) {
            mViewIdMap.put(id, view);
        }

        return view;
    }

    /**
     * Maps a point to a position in the list.
     * 
     * @param x X in local coordinate
     * @param y Y in local coordinate
     * @return The position of the item which contains the specified point, or
     *         {@link #INVALID_POSITION} if the point does not intersect an
     *         item.
     */
    private int pointToPosition(int x, int y) {
        Rect frame = mTouchFrame;
        if (frame == null) {
            mTouchFrame = new Rect();
            frame = mTouchFrame;
        }

        final int count = getChildCount();
        for (int i = count - 1; i >= 0; i--) {
            final View child = getChildAt(i);

            child.getHitRect(frame);
            if (frame.contains(x, y)) {
                return i;
            }
        }
        return NO_MATCHED_CHILD;
    }

    private void positionSelector(int l, int t, int r, int b) {
        mSelectorRect.set(l, t, r, b);
    }

    private void positionSelector(View v) {
        final Rect selectorRect = mSelectorRect;
        if (v == null) {
            selectorRect.set(0, 0, 0, 0);
        } else if (v == this) {
            selectorRect.set(0, 0, v.getWidth(), v.getHeight());
        } else {
            selectorRect.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        }
        positionSelector(selectorRect.left, selectorRect.top, selectorRect.right,
                selectorRect.bottom);
        refreshDrawableState();
    }

    private void resetSelectorTransition(int i) {
        Drawable d = mSelectors.get(i);
        if (d instanceof TransitionDrawable) {
            ((TransitionDrawable)d).resetTransition();
        }
    }

    private void resetStage() {
        removeAllViewsInLayout();
        mDataChanged = false;
        invalidate();
    }

    private boolean shouldShowSelector() {
        return (hasFocus() && !isInTouchMode()) || touchModeDrawsInPressedState();
    }

    private boolean touchModeDrawsInPressedState() {
        switch (mTouchMode) {
            case TOUCH_MODE_TAP:
            case TOUCH_MODE_DONE_WAITING:
                return true;
            default:
                return false;
        }
    }

    private void updateMotionCoords(MotionEvent event, final int pointerCount) {
        for (int pointerIndex = 0; pointerIndex < pointerCount; pointerIndex++) {
            int pointerId = event.getPointerId(pointerIndex);
            mMotionX.put(pointerId, event.getX(pointerIndex));
            mMotionY.put(pointerId, event.getY(pointerIndex));
        }
    }

    private void updateSelectorState() {
        for (int i = 0; i < mSelectors.size(); i++) {
            Drawable d = mSelectors.valueAt(i);
            if (d != null) {
                if (shouldShowSelector()) {
                    d.setState(getDrawableState());
                } else {
                    d.setState(StateSet.NOTHING);
                }
            }
        }
    }

    private void useDefaultSelector() {
        Drawable selector = getResources().getDrawable(R.drawable.scene_list_selector_holo_light);
        Drawable listSelector = getResources()
                .getDrawable(R.drawable.loop_list_selector_holo_light);
        Drawable propertiesSelector = getResources().getDrawable(
                R.drawable.rule_list_selector_holo_light);

        setItemSelector(selector);
        setSelector(2, listSelector);
        setSelector(3, selector);
        setSelector(4, propertiesSelector);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawSelector(canvas);

        super.dispatchDraw(canvas);
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
    }

    protected int getVirtualFingers() {
        if (mAdapter.getCount() == 0
                && (mForceFingersExemptions == null || Arrays.binarySearch(mForceFingersExemptions,
                        mMaxFingersDown) < 0)) {
            return mForceFingersWhenEmpty;
        }
        return mMaxFingersDown;
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams();
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(super.generateLayoutParams(p));
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        if (changed) {
            final int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).forceLayout();
            }
        }

        layoutAdapterChildren();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mSelectors.get(0) == null) {
            useDefaultSelector();
        }

        if (!isEnabled() && (mNativeHeight == -1 || mNativeWidth == -1)) {
            setMeasuredDimension(0, 0);
        }

        int specifiedWidth = MeasureSpec.getSize(widthMeasureSpec);
        int specifiedHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (mNativeHeight == -1) {
            mNativeHeight = specifiedHeight;
        }
        if (mNativeWidth == -1) {
            mNativeWidth = specifiedWidth;
        }

        float horizontalScaleFactor = ((float)specifiedWidth) / ((float)mNativeWidth);
        float verticalScaleFactor = ((float)specifiedHeight) / ((float)mNativeHeight);

        mScaleFactor = (horizontalScaleFactor < verticalScaleFactor) ? horizontalScaleFactor
                : verticalScaleFactor;

        float width = ((float)mNativeWidth) * mScaleFactor;
        float height = ((float)mNativeHeight) * mScaleFactor;

        setMeasuredDimension((int)width, (int)height);
    }

    public static class LayoutParams extends ViewGroup.LayoutParams {
        public int xPosition;

        public int yPosition;

        public LayoutParams() {
            this(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);

            TypedArray a = c.obtainStyledAttributes(attrs, R.styleable.StageLayout_Layout);
            xPosition = a.getInt(R.styleable.StageLayout_Layout_xPosition, 0);
            yPosition = a.getInt(R.styleable.StageLayout_Layout_yPosition, 0);
            a.recycle();
        }

        public LayoutParams(int width, int height) {
            super(width, height);
            xPosition = 0;
            yPosition = 0;
        }

        public LayoutParams(int x, int y, int width, int height) {
            super(width, height);
            xPosition = x;
            yPosition = y;
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
            if (source instanceof LayoutParams) {
                LayoutParams params = (LayoutParams)source;
                this.xPosition = params.xPosition;
                this.yPosition = params.yPosition;
            } else {
                xPosition = 0;
                yPosition = 0;
            }
        }
    }

    public interface OnStageClickListener {
        void onStageClick(StageView stage);
    }

    public interface OnStageLongClickListener {
        boolean onStageLongClick(StageView stage);
    }

    public interface StageAdapter extends Adapter {
        /**
         * Returns true if the item at the specified position is not a
         * separator.
         * 
         * @param position Position of item queried.
         * @return True if item is enabled.
         */
        boolean isEnabled(int position);
    }

    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataChanged = true;
            mOldItemCount = mItemCount;
            mItemCount = getAdapter().getCount();

            requestLayout();
            invalidate();
        }

        @Override
        public void onInvalidated() {
            mDataChanged = true;

            // Data is invalid so we should reset our state
            mOldItemCount = mItemCount;
            mItemCount = 0;

            requestLayout();
        }
    }

    private class CheckForLongPress extends WindowRunnable {
        @Override
        public void run() {
            boolean handled = false;
            final View child;

            if (getVirtualFingers() == 1) {
                child = getChildAt(mMotionPosition);
                if (child != null) {
                    final long longPressId = childViewPositionToId(mMotionPosition);

                    if (sameWindow() && !mDataChanged) {
                        handled = performLongPress(child, mMotionPosition, longPressId);
                    }

                }
            } else {
                handled = performStageMultipleFingerLongPress(getVirtualFingers());
                child = null;
            }

            if (handled) {
                mTouchMode = TOUCH_MODE_FINISHED_LONG_PRESS;
                setPressed(false);
                if (child != null) {
                    child.setPressed(false);
                }
            } else {
                mTouchMode = TOUCH_MODE_DONE_WAITING;
            }
        }
    }

    protected class CheckForTap extends WindowRunnable {
        @Override
        public void run() {
            if (mTouchMode == TOUCH_MODE_DOWN) {
                mTouchMode = TOUCH_MODE_TAP;

                final View child = getChildAt(mMotionPosition);

                setPressed(true);
                if (child != null && !child.hasFocusable()) {
                    child.setPressed(true);
                    positionSelector(child);
                } else if (getVirtualFingers() > 1) {
                    positionSelector(StageView.this);
                } else {
                    positionSelector(null);
                }

                refreshDrawableState();

                final int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                final boolean longClickable = isLongClickable();

                Drawable selector = mSelectors.get(getVirtualFingers());
                if (selector != null) {
                    Drawable d = selector.getCurrent();
                    if (d != null && d instanceof TransitionDrawable) {
                        if (longClickable) {
                            ((TransitionDrawable)d).startTransition(longPressTimeout);
                        } else {
                            ((TransitionDrawable)d).resetTransition();
                        }
                    }
                }

                if (longClickable) {
                    if (mPendingCheckForLongPress == null) {
                        mPendingCheckForLongPress = new CheckForLongPress();
                    }
                    mPendingCheckForLongPress.rememberWindowAttachCount();
                    postDelayed(mPendingCheckForLongPress, ViewConfiguration.getLongPressTimeout());
                } else {
                    mTouchMode = TOUCH_MODE_DONE_WAITING;
                }

                invalidate();
            }
        }
    }

    protected class PerformClick extends WindowRunnable {
        int mClickMotionPosition;

        @Override
        public void run() {
            if (mDataChanged) {
                return;
            }

            if (getVirtualFingers() > 1) {
                performStageMultipleFingerClick(getVirtualFingers());
                return;
            }

            final StageAdapter adapter = mAdapter;
            final int motionPosition = mClickMotionPosition;
            if (adapter != null && adapter.getCount() > 0 && motionPosition != INVALID_POSITION
                    && motionPosition < adapter.getCount() && sameWindow()) {
                final View view = getChildAt(motionPosition);
                if (view != null) {
                    performItemClick(view, motionPosition, childViewPositionToId(motionPosition));
                }
            }
        }
    }

    protected abstract class WindowRunnable implements Runnable {
        private int mOriginalAttachCount;

        public void rememberWindowAttachCount() {
            mOriginalAttachCount = getWindowAttachCount();
        }

        public boolean sameWindow() {
            return hasWindowFocus() && getWindowAttachCount() == mOriginalAttachCount;
        }
    }

    class OutBoolean {
        boolean value;
    }
}
