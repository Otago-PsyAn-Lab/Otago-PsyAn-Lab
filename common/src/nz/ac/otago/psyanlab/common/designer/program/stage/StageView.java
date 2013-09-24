
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.stage.StageView.PropAdapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
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

import java.util.HashMap;

public class StageView extends AdapterView<PropAdapter> {
    public static final int INVALID_POSITION = -1;

    private static final int NO_MATCHED_CHILD = INVALID_POSITION;

    protected static final int TOUCH_MODE_AT_REST = -0x01;

    protected static final int TOUCH_MODE_DONE_WAITING = 0x02;

    protected static final int TOUCH_MODE_DOWN = 0x00;

    protected static final int TOUCH_MODE_FINISHED_LONG_PRESS = -0x02;

    protected static final int TOUCH_MODE_TAP = 0x01;

    public PropAdapter mAdapter;

    public boolean mDataChanged;

    public int mItemCount;

    public int mOldItemCount;

    private DataSetObserver mDataSetObserver;

    private SparseArray<OnStageClickListener> mOnStageClickListeners = new SparseArray<StageView.OnStageClickListener>(
            1);

    private SparseArray<OnStageLongClickListener> mOnStageLongClickListeners = new SparseArray<StageView.OnStageLongClickListener>(
            1);

    private CheckForLongPress mPendingCheckForLongPress;

    private CheckForTap mPendingCheckForTap;

    private PerformClick mPerformPropClick;

    private Drawable mSelector;

    private Rect mSelectorRect = new Rect();

    private Runnable mTouchModeReset;

    private HashMap<Long, Integer> mViewIdMap = new HashMap<Long, Integer>();

    protected int mFingersDown;

    protected int mMotionPosition;

    protected int mMotionX;

    protected int mMotionY;

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

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    public PropAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public View getSelectedView() {
        throw new RuntimeException("Unsupport method: View setSelectedView()");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int action = event.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_POINTER_DOWN: {
                mFingersDown = event.getPointerCount();
                mTouchMode = TOUCH_MODE_DOWN;
                mMotionPosition = INVALID_POSITION;
                updateSelectorState();
                invalidate();
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                mFingersDown = event.getPointerCount();

                if (mPendingCheckForTap == null) {
                    mPendingCheckForTap = new CheckForTap();
                }
                postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());

                final int y = (int)event.getY();
                final int x = (int)event.getX();
                mMotionY = y;
                mMotionX = x;
                mMotionPosition = findMotionChildPosition(x, y);

                mTouchMode = TOUCH_MODE_DOWN;
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mMotionPosition != NO_MATCHED_CHILD
                        && (Math.abs(event.getY() - mMotionY) > mTouchSlop || Math.abs(event.getX()
                                - mMotionX) > mTouchSlop)) {
                    // Too much movement to be a tap event.
                    mTouchMode = TOUCH_MODE_AT_REST;
                    final View child = getChildAt(mMotionPosition);
                    if (child != null) {
                        child.setPressed(false);
                    }
                    setPressed(false);
                    final Handler handler = getHandler();
                    if (handler != null) {
                        handler.removeCallbacks(mPendingCheckForLongPress);
                    }
                    mMotionPosition = NO_MATCHED_CHILD;
                    updateSelectorState();
                    invalidate();
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mTouchMode == TOUCH_MODE_FINISHED_LONG_PRESS) {
                    return true;
                }

                if (mTouchMode == TOUCH_MODE_AT_REST) {
                    break;
                }

                // Handle stage multi-touch.

                if (mFingersDown > 1) {
                    if (mPerformPropClick == null) {
                        mPerformPropClick = new PerformClick();
                    }

                    final PerformClick performPropClick = mPerformPropClick;
                    performPropClick.mClickMotionPosition = mMotionPosition;
                    performPropClick.rememberWindowAttachCount();

                    if (mTouchMode != TOUCH_MODE_DOWN || mTouchMode != TOUCH_MODE_TAP) {
                        final Handler handler = getHandler();
                        if (handler != null) {
                            handler.removeCallbacks(mTouchMode == TOUCH_MODE_DOWN ? mPendingCheckForTap
                                    : mPendingCheckForLongPress);
                        }

                        if (!mDataChanged) {
                            // Got here so must be a tap. The long press would
                            // have triggered inside the delayed runnable.
                            mTouchMode = TOUCH_MODE_TAP;
                            setPressed(true);
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
                            final Handler handler = getHandler();
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
                                positionSelector(mMotionPosition, child);
                                setPressed(true);
                                updateSelectorState();
                                invalidate();

                                if (mSelector != null) {
                                    Drawable d = mSelector.getCurrent();
                                    if (d != null && d instanceof TransitionDrawable) {
                                        ((TransitionDrawable)d).resetTransition();
                                    }
                                }

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
    public void setAdapter(PropAdapter adapter) {
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

    public void setSelector(Drawable s) {
        if (mSelector != null) {
            mSelector.setCallback(null);
            unscheduleDrawable(mSelector);
        }
        mSelector = s;
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
            final Drawable selector = mSelector;
            selector.setBounds(mSelectorRect);
            selector.draw(canvas);
        }
    }

    private int findMotionChildPosition(int x, int y) {
        for (int i = 0; i < getChildCount(); i++) {
            Rect hitRect = new Rect();
            getChildAt(i).getHitRect(hitRect);
            if (hitRect.contains(x, y)) {
                return i;
            }
        }
        return NO_MATCHED_CHILD;
    }

    private void layoutAdapterChildren() {
        int numChildren = mAdapter.getCount();
        for (int i = 0; i < numChildren; i++) {
            OutBoolean viewAdded = new OutBoolean();
            View child = obtainView(i, viewAdded);
            LayoutParams params = (LayoutParams)child.getLayoutParams();
            int left = params.xPosition;
            int top = params.yPosition;

            int childWidthSpec = MeasureSpec.makeMeasureSpec(params.width, MeasureSpec.EXACTLY);
            int childHeightSpec = MeasureSpec.makeMeasureSpec(params.height, MeasureSpec.EXACTLY);
            child.measure(childWidthSpec, childHeightSpec);

            child.layout(left, top, left + child.getMeasuredWidth(),
                    top + child.getMeasuredHeight());

            if (!viewAdded.value) {
                addViewInLayout(child, i, params, true);
            }
        }

        mDataChanged = false;
    }

    private View obtainView(int position, OutBoolean viewAlreadyAdded) {
        long id = mAdapter.getItemId(position);
        Integer childPos = mViewIdMap.get(id);
        View convertView = null;

        if (childPos != null) {
            convertView = getChildAt(childPos);

            if (childPos != position) {
                mViewIdMap.remove(id);
                mViewIdMap.put(id, position);
            }
        } else {
            mViewIdMap.put(id, position);
        }

        if (viewAlreadyAdded != null) {
            viewAlreadyAdded.value = childPos != null;
        }
        return mAdapter.getView(position, convertView, this);
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

    private void updateSelectorState() {
        if (mSelector != null) {
            if (shouldShowSelector()) {
                mSelector.setState(getDrawableState());
            } else {
                mSelector.setState(StateSet.NOTHING);
            }
        }
    }

    private void useDefaultSelector() {
        setSelector(getResources().getDrawable(R.drawable.scene_list_selector_holo_light));
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
            int childCount = getChildCount();
            for (int i = 0; i < childCount; i++) {
                getChildAt(i).forceLayout();
            }
        }

        layoutAdapterChildren();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mSelector == null) {
            useDefaultSelector();
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
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

    public interface PropAdapter extends Adapter {

    }

    private class AdapterDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            mDataChanged = true;
            mOldItemCount = mItemCount;
            mItemCount = getAdapter().getCount();

            requestLayout();
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
            if (mFingersDown == 1) {
                final View child = getChildAt(mMotionPosition);
                if (child != null) {
                    final long longPressId = childViewPositionToId(mMotionPosition);

                    boolean handled = false;
                    if (sameWindow() && !mDataChanged) {
                        handled = performLongPress(child, mMotionPosition, longPressId);
                    }

                    if (handled) {
                        mTouchMode = TOUCH_MODE_FINISHED_LONG_PRESS;
                        setPressed(false);
                        child.setPressed(false);
                    } else {
                        mTouchMode = TOUCH_MODE_DONE_WAITING;
                    }
                }
            } else {
                performStageMultipleFingerLongPress(mFingersDown);
            }
        }
    }

    @Override
    protected void drawableStateChanged() {
        super.drawableStateChanged();
        updateSelectorState();
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
                    positionSelector(mMotionPosition, child);
                }

                refreshDrawableState();

                final int longPressTimeout = ViewConfiguration.getLongPressTimeout();
                final boolean longClickable = isLongClickable();

                if (mSelector != null) {
                    Drawable d = mSelector.getCurrent();
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

            if (mFingersDown > 1) {
                performStageMultipleFingerClick(mFingersDown);
                return;
            }

            final PropAdapter adapter = mAdapter;
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

    private void positionSelector(int position, View sel) {
        final Rect selectorRect = mSelectorRect;
        selectorRect.set(sel.getLeft(), sel.getTop(), sel.getRight(), sel.getBottom());
        positionSelector(selectorRect.left, selectorRect.top, selectorRect.right,
                selectorRect.bottom);
        refreshDrawableState();
    }

    private void positionSelector(int l, int t, int r, int b) {
        mSelectorRect.set(l, t, r, b);
    }
}
