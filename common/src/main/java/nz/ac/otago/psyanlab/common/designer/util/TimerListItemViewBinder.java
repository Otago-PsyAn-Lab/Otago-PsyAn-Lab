/*
 Copyright (C) 2014 Tonic Artos <tonic.artos@gmail.com>

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.program.util.ProgramCallbacks;
import nz.ac.otago.psyanlab.common.designer.util.ProgramComponentAdapter.ViewBinder;
import nz.ac.otago.psyanlab.common.model.Generator;
import nz.ac.otago.psyanlab.common.model.Timer;
import nz.ac.otago.psyanlab.common.model.generator.Random;
import nz.ac.otago.psyanlab.common.model.generator.Shuffle;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

public final class TimerListItemViewBinder extends AbsViewBinder implements ViewBinder<Timer> {
    public TimerListItemViewBinder(Activity activity, ProgramCallbacks callbacks) {
        super(activity, callbacks);
    }

    @Override
    public View bind(Timer timer, View convertView, ViewGroup parent) {
        TextViewHolder holder;
        if (convertView == null) {
            convertView =
                    mActivity.getLayoutInflater().inflate(R.layout.list_item_timer, parent, false);
            holder = new TextViewHolder(4);
            holder.textViews[0] = (TextView) convertView.findViewById(android.R.id.text1);
            holder.textViews[1] = (TextView) convertView.findViewById(android.R.id.text2);
            holder.textViews[2] = (TextView) convertView.findViewById(R.id.timer_type);
            holder.textViews[3] = (TextView) convertView.findViewById(R.id.timer_iterations);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder) convertView.getTag();
        }

        holder.textViews[0].setText(timer.getStringFor(mActivity, android.R.id.text1));
        holder.textViews[2].setText(timer.getStringFor(mActivity, R.id.timer_type));

        final String line2 = timer.getStringFor(mActivity, android.R.id.text2);
        if (TextUtils.isEmpty(line2)) {
            holder.textViews[1].setVisibility(View.GONE);
        } else {
            holder.textViews[1].setVisibility(View.VISIBLE);
            holder.textViews[1].setText(line2);
        }

        final String timerIterations = timer.getStringFor(mActivity, R.id.timer_iterations);
        if (TextUtils.isEmpty(timerIterations)) {
            holder.textViews[3].setVisibility(View.GONE);
        } else {
            holder.textViews[3].setVisibility(View.VISIBLE);
            holder.textViews[3].setText(timerIterations);
        }

        return convertView;
    }
}
