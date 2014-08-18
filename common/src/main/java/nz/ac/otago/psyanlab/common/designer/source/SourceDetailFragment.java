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

package nz.ac.otago.psyanlab.common.designer.source;

import com.mobeta.android.dslv.DragSortListView.DragSortListener;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.SourceDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.util.DialogueRequestCodes;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar.DialogueResultListener;

import nz.ac.otago.psyanlab.common.designer.util.EditDataColumnDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.util.EditIndexedStringDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.util.EditStringDialogueFragment;
import nz.ac.otago.psyanlab.common.designer.util.ExperimentUtils;
import nz.ac.otago.psyanlab.common.designer.util.NumberPickerDialogueFragment;
import nz.ac.otago.psyanlab.common.model.Source;
import nz.ac.otago.psyanlab.common.model.chansrc.Field;
import nz.ac.otago.psyanlab.common.model.util.Type;
import nz.ac.otago.psyanlab.common.util.FileUtils;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

public class SourceDetailFragment extends Fragment {

    private static final String DIALOG_EDIT_COL_NAME = "dialog_edit_col_name";

    public OnClickListener mAddColumnNameClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Field field = new Field();
            field.id = getNewParamId();
            field.name = getString(R.string.default_name_new_source_column, field.id + 1);
            field.type = Type.TYPE_STRING;
            EditDataColumnDialogueFragment dialogue = EditDataColumnDialogueFragment
                    .init(new EditDataColumnDialogueFragment(),
                          DialogueRequestCodes.SOURCE_NEW_COLUMN_NAME, field);
            dialogue.show(getChildFragmentManager(), DIALOG_EDIT_COL_NAME);
        }
    };

    protected OnItemClickListener mOnColumnNameItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Field item = (Field) parent.getItemAtPosition(position);
            int index = (int) parent.getItemIdAtPosition(position);

            EditDataColumnDialogueFragment dialog = EditDataColumnDialogueFragment
                    .init(new EditDataColumnDialogueFragment(),
                          DialogueRequestCodes.SOURCE_COLUMN_NAME, item, index);
            dialog.show(getChildFragmentManager(), DIALOG_EDIT_COL_NAME);
        }
    };

    private static final String ARG_SOURCE_ID = "arg_source_id";

    private static final String DIALOG_EDIT_COL_START = "dialog_edit_col_start";

    private OnClickListener mColStartClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            NumberPickerDialogueFragment dialog = NumberPickerDialogueFragment
                    .newDialog(R.string.title_set_start_column,
                               ExperimentUtils.zeroBasedToUserValue(mSource.colStart), 1,
                               mSource.getTotalCols(), DialogueRequestCodes.SOURCE_START_COLUMN,
                               false);
            dialog.showRange(true);
            dialog.show(getChildFragmentManager(), DIALOG_EDIT_COL_START);
        }
    };

    private static final String DIALOG_EDIT_NUM_ROWS = "dialog_edit_num_rows";

    private OnClickListener mNumRowsClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            NumberPickerDialogueFragment dialog = NumberPickerDialogueFragment
                    .newDialog(R.string.title_set_number_of_rows, mSource.numRows, 1,
                               mSource.getTotalRows() - mSource.rowStart,
                               DialogueRequestCodes.SOURCE_NUM_ROWS, false);
            dialog.showRange(true);
            dialog.show(getChildFragmentManager(), DIALOG_EDIT_NUM_ROWS);
        }
    };

    private static final String DIALOG_EDIT_ROW_START = "dialog_edit_row_start";

    private OnClickListener mRowStartClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            NumberPickerDialogueFragment dialog = NumberPickerDialogueFragment
                    .newDialog(R.string.title_set_start_row,
                               ExperimentUtils.zeroBasedToUserValue(mSource.rowStart), 1,
                               mSource.getTotalRows(), DialogueRequestCodes.SOURCE_START_ROW,
                               false);
            dialog.showRange(true);
            dialog.show(getChildFragmentManager(), DIALOG_EDIT_ROW_START);
        }
    };

    private static final OnSourceDeletedListener mDeletedDummy = new OnSourceDeletedListener() {
        @Override
        public void onSourceDeleted(long id) {
        }
    };

    private OnSourceDeletedListener mOnSourceDeletedListener = mDeletedDummy;

    public static SourceDetailFragment newInstance(long id) {
        SourceDetailFragment f = new SourceDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_SOURCE_ID, id);
        f.setArguments(args);
        return f;
    }

    protected static class ListItemViewHolder {

        /**
         * Name of the field.
         */
        protected TextView mName;

        /**
         * Type of the field.
         */
        protected TextView mType;

        public ListItemViewHolder(View view) {
            mName = (TextView) view.findViewById(R.id.name);
            mType = (TextView) view.findViewById(R.id.type);
        }

        public void setViewValues(Context context, Field field) {
            mName.setText(field.name);
            mType.setText(Type.getTypeString(context, field.type));
        }
    }

    private static class ColumnNameAdapter extends BaseAdapter implements DragSortListener {

        private Context mContext;

        private LayoutInflater mInflater;

        private Source mSource;

        private SourceChangedListener mSourceChangedListener;

        public ColumnNameAdapter(Context context, Source source,
                                 SourceChangedListener sourceChangedListener) {
            mContext = context;
            mSource = source;
            mSourceChangedListener = sourceChangedListener;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public void drag(int from, int to) {
        }

        @Override
        public void drop(int from, int to) {
            Field field = mSource.columns.remove(from);
            mSource.columns.add(to, field);
            notifyDataSetChanged();
            mSourceChangedListener.onSourceChange();
        }

        @Override
        public int getCount() {
            return mSource.columns.size();
        }

        @Override
        public Field getItem(int position) {
            return mSource.columns.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View view, ViewGroup parent) {
            final ListItemViewHolder holder;
            if (view == null) {
                view = mInflater.inflate(R.layout.list_item_data_channel_field, parent, false);
                holder = new ListItemViewHolder(view);
                view.setTag(holder);
            } else {
                holder = (ListItemViewHolder) view.getTag();
            }

            holder.setViewValues(mContext, getItem(position));
            return view;
        }

        @Override
        public void remove(int which) {
            mSource.columns.remove(which);
            notifyDataSetChanged();
            mSourceChangedListener.onSourceChange();
        }

        public void setSource(Source source) {
            mSource = source;
        }

        public interface SourceChangedListener {

            void onSourceChange();
        }
    }

    public ColumnNameAdapter mAdapter;

    protected SourceCallbacks mCallbacks;

    protected Source mSource;

    protected long mSourceId;

    public OnClickListener mDeleteClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCallbacks.deleteSource(mSourceId);
                    mOnSourceDeletedListener.onSourceDeleted(mSourceId);
                }
            }, ViewConfiguration.getTapTimeout());
        }
    };

    public SourceDataChangeListener mSourceDataChangedListener = new SourceDataChangeListener() {
        @Override
        public void onSourceDataChange() {
            Source old = mSource;
            mSource = mCallbacks.getSource(mSourceId);

            if (mSource == null) {
                return;
            }

            mViews.updateViewValues(mSource, old);
        }
    };

    private DialogueResultListener mOnColumnNameEditListener = new DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            int position = data.getInt(EditDataColumnDialogueFragment.RESULT_POSITION);
            Field field = data.getParcelable(EditDataColumnDialogueFragment.RESULT_FIELD);
            mSource.columns.remove(position);
            mSource.columns.add(position, field);
            mAdapter.notifyDataSetChanged();
            mCallbacks.putSource(mSourceId, mSource);
        }

        @Override
        public void onResultCancel() {
        }

        @Override
        public void onResultDelete(Bundle data) {
            int position = data.getInt(EditDataColumnDialogueFragment.RESULT_POSITION);
            mSource.columns.remove(position);
            mAdapter.notifyDataSetChanged();
            mCallbacks.putSource(mSourceId, mSource);
        }
    };

    private DialogueResultListener mOnNewColumnNameListener = new DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            Field field = data.getParcelable(EditDataColumnDialogueFragment.RESULT_FIELD);
            mSource.columns.add(field);
            mAdapter.notifyDataSetChanged();
            mCallbacks.putSource(mSourceId, mSource);
        }

        @Override
        public void onResultCancel() {
        }

        @Override
        public void onResultDelete(Bundle data) {
        }
    };

    private DialogueResultListener mOnNumRowsPickedListener = new DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            mSource.numRows = data.getInt(NumberPickerDialogueFragment.RESULT_PICKED_NUMBER);
            mViews.mNumRows.setText(String.valueOf(mSource.numRows));
            mCallbacks.putSource(mSourceId, mSource);
        }

        @Override
        public void onResultCancel() {
        }

        @Override
        public void onResultDelete(Bundle data) {
        }
    };

    private DialogueResultListener mOnStartColumnPickedListener = new DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            mSource.colStart = ExperimentUtils.userValueToZeroBased(
                    data.getInt(NumberPickerDialogueFragment.RESULT_PICKED_NUMBER));
            mViews.mColStart.setText(
                    String.valueOf(ExperimentUtils.zeroBasedToUserValue(mSource.colStart)));
            mCallbacks.putSource(mSourceId, mSource);
        }

        @Override
        public void onResultCancel() {
        }

        @Override
        public void onResultDelete(Bundle data) {
        }
    };

    private DialogueResultListener mOnStartRowPickedListener = new DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            mSource.rowStart = ExperimentUtils.userValueToZeroBased(
                    data.getInt(NumberPickerDialogueFragment.RESULT_PICKED_NUMBER));
            if (mSource.rowStart + mSource.numRows > mSource.getTotalRows()) {
                mSource.numRows = mSource.getTotalRows() - mSource.rowStart;
            }
            mViews.mRowStart.setText(
                    String.valueOf(ExperimentUtils.zeroBasedToUserValue(mSource.rowStart)));
            mViews.mNumRows.setText(String.valueOf(mSource.numRows));
            mCallbacks.putSource(mSourceId, mSource);
        }

        @Override
        public void onResultCancel() {
        }

        @Override
        public void onResultDelete(Bundle data) {
        }
    };

    private ColumnNameAdapter.SourceChangedListener mSourceChangedListener =
            new ColumnNameAdapter.SourceChangedListener() {
                @Override
                public void onSourceChange() {
                    mCallbacks.putSource(mSourceId, mSource);
                }
            };

    protected TextWatcher mNameChangeWatcher = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mSource.name = s.toString();
            mCallbacks.putSource(mSourceId, mSource);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    private ViewHolder mViews;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof SourceCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (SourceCallbacks) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_designer_source_detail, container, false);
        ListView list = (ListView) view.findViewById(R.id.columns);
        list.addHeaderView(inflater.inflate(R.layout.header_source_detail, list, false));
        list.addFooterView(inflater.inflate(R.layout.footer_source_detail, list, false));
        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mCallbacks.removeSourceDataChangeListener(mSourceDataChangedListener);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (!args.containsKey(ARG_SOURCE_ID)) {
            throw new RuntimeException("Expected source id in arguments.");
        }
        mSourceId = args.getLong(ARG_SOURCE_ID);

        mSource = mCallbacks.getSource(mSourceId);

        if (!mSource.fileCounted()) {
            try {
                if (mSource.isExternal()) {
                    Source.countRowsAndCols(mSource, new File(mSource.path));
                } else {
                    Source.countRowsAndCols(mSource, mCallbacks.getCachedFile(mSource.path));
                }
            } catch (IOException e) {
                e.printStackTrace();
                // TODO: Throw error up to user interface.
            }
        }

        mCallbacks.addSourceDataChangeListener(mSourceDataChangedListener);

        mViews = new ViewHolder(view);
        mViews.setViewValues(getActivity(), mSource);
        mViews.initViews();

        mCallbacks.registerDialogueResultListener(DialogueRequestCodes.SOURCE_START_COLUMN,
                                                  mOnStartColumnPickedListener);
        mCallbacks.registerDialogueResultListener(DialogueRequestCodes.SOURCE_START_ROW,
                                                  mOnStartRowPickedListener);
        mCallbacks.registerDialogueResultListener(DialogueRequestCodes.SOURCE_NUM_ROWS,
                                                  mOnNumRowsPickedListener);
        mCallbacks.registerDialogueResultListener(DialogueRequestCodes.SOURCE_NEW_COLUMN_NAME,
                                                  mOnNewColumnNameListener);
        mCallbacks.registerDialogueResultListener(DialogueRequestCodes.SOURCE_COLUMN_NAME,
                                                  mOnColumnNameEditListener);
    }

    public void setOnSourceDeletedListener(OnSourceDeletedListener listener) {
        mOnSourceDeletedListener = listener;
    }

    /**
     * Look through existing fields until we find an index that hasn't yet been used.
     *
     * @return New unused id for a field within this data channel.
     */
    protected int getNewParamId() {
        int i;
        for (i = 0; i < mSource.columns.size(); i++) {
            boolean found = false;
            for (Field f : mSource.columns) {
                if (i == f.id) {
                    found = true;
                }
            }
            if (!found) {
                return i;
            }
        }

        return i;
    }

    public interface OnSourceDeletedListener {

        void onSourceDeleted(long id);
    }

    private class ViewHolder {

        private View mAddColName;

        private ListView mColNames;

        private TextView mColStart;

        private View mDelete;

        private TextView mFilename;

        private TextView mFilesize;

        private EditText mName;

        private TextView mNumRows;

        private TextView mRowStart;

        public ViewHolder(View view) {
            mName = (EditText) view.findViewById(R.id.name);
            mAddColName = view.findViewById(R.id.add);
            mColNames = (ListView) view.findViewById(R.id.columns);
            mColStart = (TextView) view.findViewById(R.id.col_start);
            mRowStart = (TextView) view.findViewById(R.id.row_start);
            mNumRows = (TextView) view.findViewById(R.id.num_rows);
            mFilename = (TextView) view.findViewById(R.id.filename);
            mFilesize = (TextView) view.findViewById(R.id.filesize);
            mDelete = view.findViewById(R.id.button_delete);
        }

        public void initViews() {
            mDelete.setOnClickListener(mDeleteClickListener);

            mColStart.setOnClickListener(mColStartClickListener);
            mAddColName.setOnClickListener(mAddColumnNameClickListener);
            mRowStart.setOnClickListener(mRowStartClickListener);
            mNumRows.setOnClickListener(mNumRowsClickListener);

            mColNames.setOnItemClickListener(mOnColumnNameItemClickListener);

            mName.addTextChangedListener(mNameChangeWatcher);
        }

        public void setViewValues(Context context, Source source) {
            mName.setText(source.name);

            mAdapter = new ColumnNameAdapter(context, source, mSourceChangedListener);
            mColNames.setAdapter(mAdapter);

            setContentValues(source);
        }

        public void updateViewValues(Source newSource, Source oldSource) {
            if (!TextUtils.equals(newSource.name, oldSource.name)) {
                mName.setText(newSource.name);
            }
            mAdapter.setSource(newSource);
            setContentValues(newSource);
        }

        private void setContentValues(Source source) {
            mFilename.setText(source.filename);
            mFilesize.setText(FileUtils.formatBytes(source.filesize));

            mColStart.setText(
                    String.valueOf(ExperimentUtils.zeroBasedToUserValue(mSource.colStart)));
            mRowStart.setText(
                    String.valueOf(ExperimentUtils.zeroBasedToUserValue(mSource.rowStart)));
            mNumRows.setText(String.valueOf(mSource.numRows));
        }
    }
}
