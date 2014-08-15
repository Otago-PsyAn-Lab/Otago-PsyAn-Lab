/*
 Copyright (C) 2012, 2013, 2014 University of Otago, Tonic Artos <tonic.artos@gmail.com>

 Otago PsyAn Lab is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program. If not, see <http://www.gnu.org/licenses/>.

 In accordance with Section 7(b) of the GNU General Public License version 3,
 all legal notices and author attributions must be preserved.
 */

package nz.ac.otago.psyanlab.common.designer.util;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.util.ProgramCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter.ViewBinder;
import nz.ac.otago.psyanlab.common.model.ExperimentObject;
import nz.ac.otago.psyanlab.common.model.Operand;
import nz.ac.otago.psyanlab.common.model.operand.kind.CallOperand;
import nz.ac.otago.psyanlab.common.model.operand.kind.LiteralOperand;
import nz.ac.otago.psyanlab.common.model.util.NameResolverFactory;
import nz.ac.otago.psyanlab.common.model.util.Type;
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
            convertView = mActivity.getLayoutInflater()
                                   .inflate(R.layout.list_item_operand, parent, false);
            holder = new TextViewHolder(3);
            holder.textViews[0] = (TextView) convertView.findViewById(android.R.id.text1);
            holder.textViews[1] = (TextView) convertView.findViewById(android.R.id.text2);
            holder.textViews[2] = (TextView) convertView.findViewById(R.id.type);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder) convertView.getTag();
        }

        if (operand instanceof CallOperand) {
            CallOperand callOperand = (CallOperand) operand;
            ExperimentObject experimentObject =
                    mCallbacks.getExperimentObject(callOperand.getObject());
            final NameResolverFactory nameFactory = experimentObject.getMethodNameFactory();

            holder.textViews[1].setVisibility(View.VISIBLE);
            holder.textViews[1].setText(mActivity.getString(R.string.format_call_operand_value,
                                                            experimentObject
                                                                    .getExperimentObjectName(
                                                                            mActivity), nameFactory
                                                                    .getName(mActivity, callOperand
                                                                            .getMethod())));
            holder.textViews[0].setText(operand.getName());
        } else {
            holder.textViews[0].setText(operand.getName());
            if (operand instanceof LiteralOperand) {
                holder.textViews[1].setText(((LiteralOperand) operand).getValue());
                holder.textViews[1].setVisibility(View.VISIBLE);
            } else {
                holder.textViews[1].setVisibility(View.GONE);
            }
        }
        holder.textViews[2].setText(
                TextUtils.join("\n", Type.typeToStringArray(mActivity, operand.getType())));
        return convertView;
    }
}
