
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

package nz.ac.otago.psyanlab.common.designer.subject;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.Question;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

class QuestionsAdapter extends BaseAdapter implements ListAdapter {
    /**
     * A list of asset keys. This is sorted according to the referenced asset.
     */
    private Long[] mAssetKeys;

    private HashMap<Long, Question> mQuestions;

    private final LayoutInflater mInflater;

    public QuestionsAdapter(Context context, HashMap<Long, Question> questions) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mQuestions = questions;
        sortKeys(questions);
    }

    @Override
    public int getCount() {
        return mAssetKeys.length;
    }

    @Override
    public Question getItem(int pos) {
        return mQuestions.get(mAssetKeys[pos]);
    }

    @Override
    public long getItemId(int pos) {
        if (pos < 0 || mAssetKeys.length <= pos) {
            return ListView.INVALID_ROW_ID;
        }
        return mAssetKeys[pos];
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        TextViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_subject_question, parent, false);
            holder = new TextViewHolder(3);
            holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
            holder.textViews[1] = (TextView)convertView.findViewById(android.R.id.text2);
            holder.textViews[2] = (TextView)convertView.findViewById(R.id.type);
            convertView.setTag(holder);
        } else {
            holder = (TextViewHolder)convertView.getTag();
        }

        Question question = mQuestions.get(mAssetKeys[pos]);
        holder.textViews[0].setText(question.text);
        holder.textViews[1].setText(question.name);
        holder.textViews[2].setText(question.getKindResId());

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return mAssetKeys[position] >= 0;
    }

    @Override
    public void notifyDataSetChanged() {
        sortKeys(mQuestions);
        super.notifyDataSetChanged();
    }

    private void sortKeys(HashMap<Long, Question> questions) {
        mAssetKeys = new Long[questions.size()];
        int i = 0;
        for (Long entry : questions.keySet()) {
            mAssetKeys[i] = entry;
            i++;
        }

        Arrays.sort(mAssetKeys, new Comparator<Long>() {
            final Question.Comparator compr = new Question.Comparator();

            @Override
            public int compare(Long lhs, Long rhs) {
                return compr.compare(mQuestions.get(lhs), mQuestions.get(rhs));
            }
        });
    }

    public void setQuestions(HashMap<Long, Question> questions) {
        mQuestions = questions;
        sortKeys(questions);
        notifyDataSetChanged();
    }
}
