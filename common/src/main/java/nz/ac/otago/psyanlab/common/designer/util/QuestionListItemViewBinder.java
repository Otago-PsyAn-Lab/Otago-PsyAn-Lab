
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.util.ProgramCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter.ViewBinder;
import nz.ac.otago.psyanlab.common.model.Question;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public final class QuestionListItemViewBinder extends AbsViewBinder implements ViewBinder<Question> {
    public QuestionListItemViewBinder(Activity activity, ProgramCallbacks callbacks) {
        super(activity, callbacks);
    }

    @Override
    public View bind(Question question, View convertView, ViewGroup parent) {
        TextViewHolder holder;
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(
                    R.layout.list_item_subject_question, parent, false);
            holder = new TextViewHolder(3);
            holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
            holder.textViews[1] = (TextView)convertView.findViewById(android.R.id.text2);
            holder.textViews[2] = (TextView)convertView.findViewById(R.id.type);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder)convertView.getTag();
        }

        holder.textViews[0].setText(question.text);
        holder.textViews[1].setText(question.name);
        holder.textViews[2].setText(question.getKindResId());

        return convertView;
    }
}
