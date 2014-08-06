
package nz.ac.otago.psyanlab.common.designer.program.stage;

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

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.stage.StageView.StageAdapter;

/**
 * The Thumbnail stage view plays nice with fill gravity layout settings when filling spaces. It
 * does this by not being greedy for its maximum size, instead requesting a size of 0. The downside
 * of this is that it will be a size of 0 unless its parent specifies a size.
 */
public class StageThumbnailView extends StageView {

    public StageThumbnailView(Context context) {
        super(context);
    }

    public StageThumbnailView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public StageThumbnailView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mSelectors.get(0) == null) {
            useDefaultSelector();
        }

        if (!isEnabled() && (mNativeHeight == -1 || mNativeWidth == -1)) {
            setMeasuredDimension(0, 0);
            return;
        }

        int specifiedWidth = MeasureSpec.getSize(widthMeasureSpec);
        int specifiedHeight = MeasureSpec.getSize(heightMeasureSpec);

        if (mNativeHeight == -1) {
            mNativeHeight = specifiedHeight;
        }
        if (mNativeWidth == -1) {
            mNativeWidth = specifiedWidth;
        }
        float horizontalScaleFactor = 1;
        if (specifiedWidth != 0) {
            horizontalScaleFactor = (float) specifiedWidth / (float) mNativeWidth;
        }
        float verticalScaleFactor = 1;
        if (specifiedHeight != 0) {
            verticalScaleFactor = (float) specifiedHeight / (float) mNativeHeight;
        }

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        float width;
        float height;
        if (widthMode == MeasureSpec.EXACTLY) {
            width = mNativeWidth * horizontalScaleFactor;
            height = mNativeHeight * horizontalScaleFactor;
        } else if (heightMode == MeasureSpec.EXACTLY) {
            width = mNativeWidth * verticalScaleFactor;
            height = mNativeHeight * verticalScaleFactor;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            if (mNativeWidth > specifiedWidth) {
                width = mNativeWidth * horizontalScaleFactor;
                height = mNativeHeight * horizontalScaleFactor;
            } else {
                width = mNativeWidth;
                height = mNativeHeight;
            }
        } else if (heightMode == MeasureSpec.AT_MOST) {
            if (mNativeHeight > specifiedHeight) {
                width = mNativeWidth * verticalScaleFactor;
                height = mNativeHeight * verticalScaleFactor;
            } else {
                width = mNativeWidth;
                height = mNativeHeight;
            }
        } else {
            // We want to try and force a sizing decision from above.
            width = 0;
            height = 0;
        }

        setMeasuredDimension((int) width, (int) height);
    }
}
