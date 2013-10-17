
package nz.ac.otago.psyanlab.common.designer.program;

import nz.ac.otago.psyanlab.common.R;

import android.content.Context;
import android.view.View;

public abstract class AbsViewHolder<T> {
    public View rightColumn;

    protected Context mContext;

    public View leftColumn;

    public AbsViewHolder(Context context, View view) {
        mContext = context;

        leftColumn = view.findViewById(R.id.column);
        rightColumn = view.findViewById(R.id.right_column);
    }

    public void alignRightColumnRight() {
//        RelativeLayout.LayoutParams params = (LayoutParams)rightColumn.getLayoutParams();
//        params.width = mContext.getResources().getDimensionPixelSize(R.dimen.right_column_aligned);
//        rightColumn.setLayoutParams(params);
//        rightColumn.invalidate();

        rightColumn.setPadding(rightColumn.getPaddingLeft(), rightColumn.getPaddingTop(), 0,
                rightColumn.getPaddingBottom());
    }

    public abstract void initViews();

    public void restoreRightColumnPadding() {
//        RelativeLayout.LayoutParams params = (LayoutParams)rightColumn.getLayoutParams();
//        params.width = mContext.getResources().getDimensionPixelSize(R.dimen.right_column);
//        rightColumn.setLayoutParams(params);
//        rightColumn.invalidate();

        rightColumn.setPadding(rightColumn.getPaddingLeft(), rightColumn.getPaddingTop(), mContext
                .getResources().getDimensionPixelSize(R.dimen.program_right_padding), rightColumn
                .getPaddingBottom());
    }

    public abstract void setViewValues(T t);
}
