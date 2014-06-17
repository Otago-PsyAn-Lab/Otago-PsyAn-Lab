
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
