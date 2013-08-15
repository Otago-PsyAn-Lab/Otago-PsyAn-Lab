
package nz.ac.otago.psyanlab.common.view;

import android.content.Context;
import android.support.v4.widget.ViewDragHelper;
import android.support.v4.widget.ViewDragHelper.Callback;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ListView;

public class PrancingListView extends ListView {
    private int mDragHandleId;
    private Callback mVDCallback = new Callback() {
        @Override
        public boolean tryCaptureView(View v, int id) {
            // TODO Auto-generated method stub
            return true;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            Log.d("asdf", "view captured");
        };
    };
    private ViewDragHelper mViewDragHelper;
    private boolean mIsDragging;

    public PrancingListView(Context context) {
        super(context);
        mViewDragHelper = ViewDragHelper.create(this, mVDCallback);
    }

    public PrancingListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mViewDragHelper = ViewDragHelper.create(this, mVDCallback);
    }

    public PrancingListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mViewDragHelper = ViewDragHelper.create(this, mVDCallback);
    }

    public void setDragHandle(int resId) {
        mDragHandleId = resId;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int y = (int)ev.getY();
        int x = (int)ev.getX();
        Log.d("asdf", "pointerid" + ev.getPointerId(0));
        if (!mIsDragging) {
            int pos = pointToPosition(x, y);
            if (pos != INVALID_POSITION) {
                // View handle = mViewDragHelper.findTopChildUnder(x, y);
                View row = getChildAt(pos - getFirstVisiblePosition());
                View handle = row.findViewById(mDragHandleId);
                if (handle != null && handle.getId() == mDragHandleId) {
                    mViewDragHelper.captureChildView(row, ev.getPointerId(0));
                    mIsDragging = true;
                }
            }
        }

        if (mIsDragging) {
            mViewDragHelper.processTouchEvent(ev);
            if (mViewDragHelper.isPointerDown(0)) {
                View view = mViewDragHelper.getCapturedView();
                mViewDragHelper.smoothSlideViewTo(view, view.getLeft(), y);
            }
            return true;
        }
        return super.onTouchEvent(ev);
    }
}
