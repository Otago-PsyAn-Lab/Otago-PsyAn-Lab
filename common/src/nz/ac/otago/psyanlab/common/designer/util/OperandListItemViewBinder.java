
package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.util.ProgramCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter.ViewBinder;
import nz.ac.otago.psyanlab.common.model.ExperimentObject;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.CallOperand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;
import nz.ac.otago.psyanlab.common.model.util.ModelUtils;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

final public class OperandListItemViewBinder extends AbsViewBinder implements ViewBinder<Operand> {
    public OperandListItemViewBinder(Activity activity, ProgramCallbacks callbacks) {
        super(activity, callbacks);
    }

    @Override
    public View bind(Operand operand, View convertView, ViewGroup parent) {
        TextViewHolder holder;
        if (convertView == null) {
            convertView = mActivity.getLayoutInflater().inflate(R.layout.list_item_operand, parent,
                    false);
            holder = new TextViewHolder(3);
            holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
            holder.textViews[1] = (TextView)convertView.findViewById(android.R.id.text2);
            holder.textViews[2] = (TextView)convertView.findViewById(R.id.type);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder)convertView.getTag();
        }

        if (operand instanceof CallOperand) {
            CallOperand callOperand = (CallOperand)operand;
            ExperimentObject experimentObject = mCallbacks.getExperimentObject(callOperand
                    .getActionObject());
            final NameResolverFactory nameFactory = ModelUtils
                    .getMethodNameFactory(experimentObject.getClass());

            holder.textViews[1].setVisibility(View.VISIBLE);
            holder.textViews[1].setText(experimentObject.getPrettyName(mActivity));
            holder.textViews[0].setText(nameFactory.getResId(callOperand.getActionMethod()));
        } else {
            holder.textViews[0].setText(operand.getName());
            if (operand instanceof LiteralOperand) {
                holder.textViews[1].setText(((LiteralOperand)operand).getValue());
                holder.textViews[1].setVisibility(View.VISIBLE);
            } else {
                holder.textViews[1].setVisibility(View.GONE);
            }
        }
        holder.textViews[2].setText(TextUtils.join("\n",
                Operand.typeToStringArray(mActivity, operand.getType())));
        return convertView;
    }
}
