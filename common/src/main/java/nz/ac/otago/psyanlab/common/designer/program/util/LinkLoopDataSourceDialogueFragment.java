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

package nz.ac.otago.psyanlab.common.designer.program.util;

import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity;
import nz.ac.otago.psyanlab.common.designer.util.LoopCallbacks;
import nz.ac.otago.psyanlab.common.model.Loop;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

public class LinkLoopDataSourceDialogueFragment extends DialogFragment {
    private static final String ARG_ID = "arg_id";

    /**
     * Create a new instance of the dialogue fragment.
     *
     * @param id Id of loop to link with picked data source.
     * @return New dialogue fragment.
     */
    public static LinkLoopDataSourceDialogueFragment newDialogue(long id) {
        LinkLoopDataSourceDialogueFragment f = new LinkLoopDataSourceDialogueFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        f.setArguments(args);
        return f;
    }

    private static class AddDeselectItemToSourcesAdapter implements ListAdapter {
        private final Context mContext;

        private final ListAdapter mDelegate;

        private final LayoutInflater mInflater;

        public AddDeselectItemToSourcesAdapter(Context context, ListAdapter adapter) {
            mContext = context;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mDelegate = adapter;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return mDelegate.areAllItemsEnabled();
        }

        @Override
        public int getCount() {
            return mDelegate.getCount() + 1;
        }

        @Override
        public Object getItem(int i) {
            if (i == 0) {
                return null;
            }

            return mDelegate.getItem(i - 1);
        }

        @Override
        public long getItemId(int i) {
            if (i == 0) {
                return -1;
            }

            return mDelegate.getItemId(i - 1);
        }

        @Override
        public int getItemViewType(int i) {
            if (i == 0) {
                return 0;
            }

            return mDelegate.getItemViewType(i - 1) + 1;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (position == 0) {
                return getInsertedView(position, convertView, parent);
            } else {
                return mDelegate.getView(position - 1, convertView, parent);
            }
        }

        @Override
        public int getViewTypeCount() {
            return mDelegate.getViewTypeCount() + 1;
        }

        @Override
        public boolean hasStableIds() {
            return mDelegate.hasStableIds();
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public boolean isEnabled(int i) {
            return i == 0 || mDelegate.isEnabled(i - 1);
        }

        @Override
        public void registerDataSetObserver(DataSetObserver dataSetObserver) {
            mDelegate.registerDataSetObserver(dataSetObserver);
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {
            mDelegate.unregisterDataSetObserver(dataSetObserver);
        }

        private View getInsertedView(int position, View convertView, ViewGroup parent) {
            TextViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.list_item_source, parent, false);
                holder = new TextViewHolder(1);
                holder.textViews[0] =
                        (android.widget.TextView) convertView.findViewById(android.R.id.text1);
                convertView.setTag(holder);
            } else {
                holder = (TextViewHolder) convertView.getTag();
            }

            holder.textViews[0]
                    .setText(mContext.getString(R.string.option_no_link_loop_data_source));
            return convertView;
        }
    }

    protected LoopCallbacks mCallbacks;

    protected Loop mLoop;

    protected long mLoopId;

    private AdapterView.OnItemClickListener mOnSourceItemClickListener =
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mLoop.linkedSource = l;
                    mCallbacks.putLoop(mLoopId, mLoop);
                    dismiss();
                }
            };

    protected ViewHolder mViews;

    private ExperimentDesignerActivity.SourceDataChangeListener mSourcedataChangeListener =
            new ExperimentDesignerActivity.SourceDataChangeListener() {
                @Override
                public void onSourceDataChange() {
                    mViews.updateViews(getActivity());
                }
            };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof LoopCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (LoopCallbacks) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialogue_link_loop_data_source, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            throw new RuntimeException("Expected arguments.");
        }

        mViews = new ViewHolder(view);
        mViews.initViews(getActivity());

        mLoopId = args.getLong(ARG_ID);
        mLoop = mCallbacks.getLoop(mLoopId);

        getDialog().setTitle(getString(R.string.format_title_link_loop_data_source, mLoop.name));
    }

    private class ViewHolder {
        private View cancel;

        private ListView sources;

        public ViewHolder(View view) {
            cancel = view.findViewById(R.id.action_cancel);
            sources = (ListView) view.findViewById(android.R.id.list);
        }

        public void initViews(Context context) {
            loadAdapter(context);
            sources.setOnItemClickListener(mOnSourceItemClickListener);
        }

        public void updateViews(Context context) {
            loadAdapter(context);
        }

        private void loadAdapter(Context context) {
            ListAdapter adapter = mCallbacks.getSourcesAdapter();
            adapter = new AddDeselectItemToSourcesAdapter(context, adapter);
            sources.setAdapter(adapter);
        }
    }
}

