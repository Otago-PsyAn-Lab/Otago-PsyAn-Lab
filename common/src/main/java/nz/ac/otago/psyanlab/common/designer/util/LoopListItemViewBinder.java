package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.util.ProgramCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter.ViewBinder;
import nz.ac.otago.psyanlab.common.model.Loop;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class LoopListItemViewBinder extends AbsViewBinder implements ViewBinder<Loop> {

    public LoopListItemViewBinder(Activity activity, ProgramCallbacks callbacks) {
        super(activity, callbacks);
    }

    @Override
    public View bind(Loop loop, View convertView, ViewGroup parent) {
        TextViewHolder holder;
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item_loop, parent,
                    false);
            holder = new TextViewHolder(1);
            holder.textViews[0] = (TextView) convertView.findViewById(android.R.id.text1);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder) convertView.getTag();
        }

        holder.textViews[0].setText(loop.name);

        return convertView;
    }
}