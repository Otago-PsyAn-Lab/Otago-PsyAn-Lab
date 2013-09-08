
package nz.ac.otago.psyanlab.common.designer.program.stage;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.stage.Stage.PropAdapter;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.DataSetObserver;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.SparseArray;
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

public class Stage extends AdapterView<PropAdapter> {
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

    private SparseArray<OnStageClickListener> mOnStageClickListeners = new SparseArray<Stage.OnStageClickListener>(
            1);

    private SparseArray<OnStageLongClickListener> mOnStageLongClickListeners = new SparseArray<Stage.OnStageLongClickListener>(
            1);

    private CheckForLongPress mPendingCheckForLongPress;

    private CheckForTap mPendingCheckForTap;

    private PerformClick mPerformPropClick;

    private Runnable mTouchModeReset;

    private HashMap<Long, Integer> mViewIdMap = new HashMap<Long, Integer>();

    protected int mFingersDown;

    protected int mMotionPosition;

    protected int mMotionX;

    protected int mMotionY;

    protected int mTouchMode;

    protected int mTouchSlop;

    public Stage(Context context) {
        super(context);
    }

    public Stage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Stage(Context context, AttributeSet attrs, int defStyle) {
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
            case MotionEvent.ACTION_DOWN: {
                if (mPendingCheckForTap == null) {
                    mPendingCheckForTap = new CheckForTap();
                    postDelayed(mPendingCheckForTap, ViewConfiguration.getTapTimeout());
                }

                final int y = (int)event.getY();
                final int x = (int)event.getX();
                mMotionY = y;
                mMotionX = x;
                mMotionPosition = findMotionChildPosition(x, y);
                if (mMotionPosition == NO_MATCHED_CHILD) {
                    // Don't consume the event and pass it to super because we
                    // can't handle it yet.
                    break;
                }
                mTouchMode = TOUCH_MODE_DOWN;
                return true;
            }
            case MotionEvent.ACTION_MOVE: {
                if (mMotionPosition != NO_MATCHED_CHILD
                        && Math.abs(event.getY() - mMotionY) > mTouchSlop) {
                    // Too much movement to be a tap event.
                    mTouchMode = TOUCH_MODE_AT_REST;
                    final View child = getChildAt(mMotionPosition);
                    if (child != null) {
                        child.setPressed(false);
                    }
                    final Handler handler = getHandler();
                    if (handler != null) {
                        handler.removeCallbacks(mPendingCheckForLongPress);
                    }
                    mMotionPosition = NO_MATCHED_CHILD;
                }
                break;
            }
            case MotionEvent.ACTION_UP: {
                if (mTouchMode == TOUCH_MODE_FINISHED_LONG_PRESS) {
                    return true;
                }
                if (mTouchMode == TOUCH_MODE_AT_REST || mMotionPosition == NO_MATCHED_CHILD) {
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
                            // Got here so must be a tap. The long press would
                            // have triggered inside the delayed runnable.
                            mTouchMode = TOUCH_MODE_TAP;
                            child.setPressed(true);
                            setPressed(true);
                            if (mTouchModeReset != null) {
                                removeCallbacks(mTouchModeReset);
                            }
                            mTouchModeReset = new Runnable() {
                                @Override
                                public void run() {
                                    mTouchMode = TOUCH_MODE_AT_REST;
                                    child.setPressed(false);
                                    setPressed(false);
                                    if (!mDataChanged) {
                                        performPropClick.run();
                                    }
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
                }
                mTouchMode = TOUCH_MODE_AT_REST;
                return true;
            }
        }
        return super.onTouchEvent(event);
    }

    public boolean performLongPress(int fingersDown) {
        OnStageLongClickListener listener = mOnStageLongClickListeners.get(fingersDown);
        if (listener != null) {
            return doLongPressFeedback(listener.onStageLongClick(this), this);
        }
        return false;
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

    public void setOnStageLongClickListener(int numFingers, OnStageLongClickListener listener) {
        mOnStageLongClickListeners.setValueAt(numFingers, listener);
    }

    @Override
    public void setSelection(int position) {
        throw new RuntimeException("Unsupport method: setSelection(int)");
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
        invalidate();

        int numChildren = mAdapter.getCount();
        for (int i = 0; i < numChildren; i++) {
            OutBoolean viewAdded = new OutBoolean();
            View child = obtainView(i, viewAdded);
            LayoutParams params = (LayoutParams)child.getLayoutParams();
            int left = params.xPosition;
            int top = params.yPosition;

            child.layout(left, top, left + child.getMeasuredWidth(),
                    top + child.getMeasuredHeight());

            if (!viewAdded.value) {
                addViewInLayout(child, i, params, true);
            }
        }
    }

    private View obtainView(int position) {
        return obtainView(position, null);
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

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
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
        int childWidthSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);

        int numChildren = mAdapter.getCount();
        for (int i = 0; i < numChildren; i++) {
            View child = obtainView(i);
            child.measure(childWidthSpec, childHeightSpec);
        }

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                MeasureSpec.getSize(heightMeasureSpec));
    }

    public class LayoutParams extends ViewGroup.LayoutParams {
        private int xPosition;

        private int yPosition;

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

        public LayoutParams(ViewGroup.LayoutParams p) {
            super(p);
            if (p instanceof LayoutParams) {
                LayoutParams params = (LayoutParams)p;
                this.xPosition = params.xPosition;
                this.yPosition = params.yPosition;
            } else {
                xPosition = 0;
                yPosition = 0;
            }
        }
    }

    public interface OnStageClickListener {
        boolean onStageClick(Stage stage);
    }

    public interface OnStageLongClickListener {
        boolean onStageLongClick(Stage stage);
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
                performLongPress(mFingersDown);
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
                }

                refreshDrawableState();

                if (isLongClickable()) {
                    if (mPendingCheckForLongPress == null) {
                        mPendingCheckForLongPress = new CheckForLongPress();
                    }
                    mPendingCheckForLongPress.rememberWindowAttachCount();
                    postDelayed(mPendingCheckForLongPress, ViewConfiguration.getLongPressTimeout());
                } else {
                    mTouchMode = TOUCH_MODE_DONE_WAITING;
                }
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
}
