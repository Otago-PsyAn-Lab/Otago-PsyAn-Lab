
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
import nz.ac.otago.psyanlab.common.designer.subject.SubjectFragment.Callbacks;
import nz.ac.otago.psyanlab.common.model.Question;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import java.text.Collator;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

public abstract class OptionsFragment extends Fragment {
    private static final String ARG_HINT = "arg_hint";

    private static final String ARG_KIND = "arg_kind";

    private static final String ARG_NAME = "arg_name";

    private static final String ARG_OPTIONS = "arg_options";

    private static final String ARG_REQUIRED = "arg_requred";

    private static final String ARG_TEXT = "arg_text";

    public static <T extends OptionsFragment> T init(T f, Question q) {
        Bundle args = new Bundle();
        args.putInt(ARG_KIND, q.getKind());
        args.putString(ARG_NAME, q.name);
        args.putString(ARG_TEXT, q.text);
        args.putString(ARG_HINT, q.hint);
        args.putBoolean(ARG_REQUIRED, q.required);
        args.putStringArrayList(ARG_OPTIONS, q.options);
        f.setArguments(args);
        return f;
    }

    private OnQuestionKindChangeListener mListener;

    private ViewHolder mViews;

    protected Callbacks mCallbacks;

    protected Question mQuestion;

    public final Question getQuestion() {
        loadQuestionValues();
        return mQuestion;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof Callbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (Callbacks)activity;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        int kind = args.getInt(ARG_KIND);
        mQuestion = Question.getNewInstance(kind);
        mQuestion.name = args.getString(ARG_NAME);
        mQuestion.text = args.getString(ARG_TEXT);
        mQuestion.hint = args.getString(ARG_HINT);
        mQuestion.required = args.getBoolean(ARG_REQUIRED);
        mQuestion.options = args.getStringArrayList(ARG_OPTIONS);

        mViews = new ViewHolder(view);
        mViews.setOnQuestionTypeChangeListener(mListener);
        mViews.initViews(getActivity());
        mViews.setViewValues(mQuestion);
    }

    public void setOnQuestionTypeChangeListener(OnQuestionKindChangeListener listener) {
        mListener = listener;
    }

    protected void loadQuestionValues() {
        mQuestion.name = mViews.getName();
        mQuestion.required = mViews.getIsRequired();
        mQuestion.text = mViews.getText();
        mQuestion.hint = mViews.getHint();
    }

    public static class KindAdapter extends BaseAdapter implements ListAdapter, SpinnerAdapter {
        private LayoutInflater mInflater;

        private Integer[] mKeys;

        private HashMap<Integer, String> mKinds;

        public KindAdapter(Context context) {
            mKinds = Question.getKinds(context);
            mKeys = mKinds.keySet().toArray(new Integer[] {});

            final Collator collator = Collator.getInstance();
            Arrays.sort(mKeys, new Comparator<Integer>() {
                @Override
                public int compare(Integer lhs, Integer rhs) {
                    return collator.compare(mKinds.get(lhs), mKinds.get(rhs));
                }
            });
            mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mKeys.length;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            TextViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            holder.textViews[0].setText(mKinds.get(mKeys[position]));
            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return mKinds.get(mKeys[position]);
        }

        @Override
        public long getItemId(int position) {
            return mKeys[position];
        }

        public int getPosition(int kind) {
            for (int i = 0; i < mKeys.length; i++) {
                if (mKeys[i] == kind) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextViewHolder holder;

            if (convertView == null) {
                convertView = mInflater.inflate(android.R.layout.simple_list_item_1, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] = (TextView)convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder)convertView.getTag();
            }

            holder.textViews[0].setText(mKinds.get(mKeys[position]));
            return convertView;
        }
    }

    public static interface OnQuestionKindChangeListener {
        void onQuestionKindChange(int newKind);
    }

    private static class ViewHolder {
        private final static OnQuestionKindChangeListener sDummyListener = new OnQuestionKindChangeListener() {
            @Override
            public void onQuestionKindChange(int newType) {
                Log.w("OPAL", "Dummy listener called on question type change.");
            }
        };

        private KindAdapter mAdapter;

        private EditText mHint;

        private CheckBox mIsRequired;

        private Spinner mKind;

        private EditText mName;

        private OnItemSelectedListener mOnKindSelectedListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (mLastKindId != id) {
                    mListener.onQuestionKindChange((int)id);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        private EditText mText;

        protected int mLastKindId;

        protected OnQuestionKindChangeListener mListener = sDummyListener;

        public ViewHolder(View view) {
            mKind = (Spinner)view.findViewById(R.id.kind);
            mName = (EditText)view.findViewById(R.id.name);
            mText = (EditText)view.findViewById(R.id.text);
            mHint = (EditText)view.findViewById(R.id.hint);
            mIsRequired = (CheckBox)view.findViewById(R.id.required);
        }

        public String getHint() {
            return mHint.getText().toString();
        }

        public boolean getIsRequired() {
            return mIsRequired.isChecked();
        }

        public String getName() {
            return mName.getText().toString();
        }

        public String getText() {
            return mText.getText().toString();
        }

        public void initViews(Context context) {
            mAdapter = new KindAdapter(context);
            mKind.setAdapter(mAdapter);
        }

        public void setOnQuestionTypeChangeListener(OnQuestionKindChangeListener listener) {
            mListener = listener;
        }

        public void setViewValues(Question question) {
            if (question == null) {
                return;
            }

            mLastKindId = question.getKind();
            mKind.setSelection(mAdapter.getPosition(mLastKindId));
            mKind.setOnItemSelectedListener(mOnKindSelectedListener);
            mName.setText(question.name);
            mText.setText(question.text);
            mHint.setText(question.hint);
            mIsRequired.setChecked(question.required);
        }
    }
}
