
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

import com.mobeta.android.dslv.DragSortListView.DragSortListener;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.subject.EditOptionDialogueFragment.OnDoneListener;
import nz.ac.otago.psyanlab.common.model.Question;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class ManyOptionsQuestionFragment extends OptionsFragment implements OnDoneListener {
    private static final String DIALOGUE_TAG = "dialogue_many_options_edit_option";

    public static OptionsFragment newInstance(Question q) {
        OptionsFragment f = new ManyOptionsQuestionFragment();
        return init(f, q);
    }

    public OnClickListener mOnAddClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            showNewOptionDialogue();
        }
    };

    public OnItemClickListener mOnOptionItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showEditOptionDialogue((int)id, (String)parent.getItemAtPosition(position));
        }
    };

    private LayoutInflater mInflater;

    private ViewHolder mViews;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_with_options, container, false);
        ListView list = (ListView)view.findViewById(R.id.options);
        list.addHeaderView(inflater.inflate(R.layout.header_question_with_options, list, false));
        mInflater = inflater;
        return view;
    }

    @Override
    public void onDone(int position, String text) {
        if (position == -1) {
            mQuestion.options.add(text);
        } else {
            mQuestion.options.set(position, text);
        }
        mViews.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViews = new ViewHolder(view);
        mViews.initViews();
        mViews.setViewValues(mQuestion);

        relinkDialogueIfPresent();
    }

    /**
     * This has to be done after a configuration change as the listener
     * reference will have been lost or be dangling.
     */
    private void relinkDialogueIfPresent() {
        EditOptionDialogueFragment f = (EditOptionDialogueFragment)getChildFragmentManager()
                .findFragmentByTag(DIALOGUE_TAG);
        if (f != null) {
            f.setOnDoneListener(this);
        }
    }

    @Override
    protected void loadQuestionValues() {
        super.loadQuestionValues();
        mQuestion.options = mViews.getOptions();
    }

    protected void showEditOptionDialogue(int position, String text) {
        EditOptionDialogueFragment dialogue = EditOptionDialogueFragment
                .getDialogue(position, text);
        dialogue.setOnDoneListener(this);
        dialogue.show(getChildFragmentManager(), DIALOGUE_TAG);
    }

    protected void showNewOptionDialogue() {
        EditOptionDialogueFragment dialogue = EditOptionDialogueFragment.getDialogue();
        dialogue.setOnDoneListener(this);
        dialogue.show(getChildFragmentManager(), DIALOGUE_TAG);
    }

    public class ViewHolder {
        private OptionsAdapter mAdapter;

        private View mAdd;

        private ListView mList;

        public ViewHolder(View view) {
            mList = (ListView)view.findViewById(R.id.options);
            mAdd = view.findViewById(R.id.add);
        }

        public OptionsAdapter getAdapter() {
            return mAdapter;
        }

        public ArrayList<String> getOptions() {
            return mAdapter.getOptions();
        }

        public void initViews() {
            mAdapter = new OptionsAdapter(mInflater, null);
            mList.setAdapter(mAdapter);
            mList.setOnItemClickListener(mOnOptionItemClickListener);
            mList.setHeaderDividersEnabled(false);
            mAdd.setOnClickListener(mOnAddClickListener);
        }

        public void setViewValues(Question question) {
            mAdapter.setOptions(question.options);
        }
    }

    private static class OptionsAdapter extends BaseAdapter implements DragSortListener {
        private LayoutInflater mInflater;

        private ArrayList<String> mOptions;

        public OptionsAdapter(LayoutInflater layoutInflater, ArrayList<String> options) {
            if (options != null) {
                mOptions = options;
            } else {
                mOptions = new ArrayList<String>();
            }
            mInflater = layoutInflater;
        }

        @Override
        public void drag(int from, int to) {
        }

        @Override
        public void drop(int from, int to) {
            String move = mOptions.remove(from);
            mOptions.add(to, move);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mOptions.size();
        }

        @Override
        public String getItem(int position) {
            return mOptions.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public ArrayList<String> getOptions() {
            return mOptions;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final TextViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_with_handle, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);

                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            String option = getItem(position);
            holder.textViews[0].setText(option);

            return convertView;
        }

        @Override
        public void remove(int which) {
            mOptions.remove(which);
            notifyDataSetChanged();
        }

        public void setOptions(ArrayList<String> options) {
            mOptions = options;
            notifyDataSetChanged();
        }
    }

    @Override
    public void onDelete(int position) {
        mViews.getAdapter().remove(position);
    }
}
