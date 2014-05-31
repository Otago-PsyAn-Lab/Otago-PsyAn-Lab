
package nz.ac.otago.psyanlab.common.designer.channels;

import com.mobeta.android.dslv.DragSortListView.DragSortListener;
import com.mobeta.android.dslv.DragSortListView.RemoveListener;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity.DataChannelDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.channels.ChannelDetailFragment.ListItemViewHolder.TextFocusedListener;
import nz.ac.otago.psyanlab.common.model.DataChannel;
import nz.ac.otago.psyanlab.common.model.channel.Field;
import nz.ac.otago.psyanlab.common.model.util.Type;
import nz.ac.otago.psyanlab.common.util.TextViewHolder;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

public class ChannelDetailFragment extends Fragment {
    private static final String ARG_CHANNEL_ID = "arg_channel_id";

    private static final OnChannelDeletedListener sDummy = new OnChannelDeletedListener() {
        @Override
        public void onChannelDeleted() {
        }
    };

    public static ChannelDetailFragment newInstance(long id) {
        ChannelDetailFragment f = new ChannelDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CHANNEL_ID, id);
        f.setArguments(args);
        return f;
    }

    private DataChannelDataChangeListener mDataChangeListener = new DataChannelDataChangeListener() {
        @Override
        public void onDataChannelDataChange() {
            DataChannel old = mDataChannel;
            mDataChannel = mCallbacks.getDataChannel(mDataChannelId);

            if (mDataChannel == null) {
                return;
            }

            mViews.updateViewValues(mDataChannel, old);
        }
    };

    private OnChannelDeletedListener mOnChannelDeletedListener = sDummy;

    private ViewHolder mViews;

    protected DataChannelAdapter mAdapter;

    protected OnClickListener mAddClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Field newField = new Field();
            newField.id = getNewParamId();
            newField.name = getString(R.string.default_name_data_channel_field, newField.id + 1);
            newField.type = Type.TYPE_STRING;
            mDataChannel.fields.add(newField);
            mAdapter.notifyDataSetChanged();
        }
    };

    protected ChannelCallbacks mCallbacks;

    protected DataChannel mDataChannel;

    protected long mDataChannelId;

    protected OnClickListener mDeleteClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // Post delayed because we want to let the visual feedback have time
            // to show.
            v.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mCallbacks.deleteDataChannel(mDataChannelId);
                    mOnChannelDeletedListener.onChannelDeleted();
                }
            }, ViewConfiguration.getTapTimeout());
        }
    };

    protected TextWatcher mDescriptionChangedListener = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mDataChannel.description = s.toString();
            mCallbacks.putDataChannel(mDataChannelId, mDataChannel);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    protected TextWatcher mNameChangedListener = new TextWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mDataChannel.name = s.toString();
            mCallbacks.putDataChannel(mDataChannelId, mDataChannel);
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }
    };

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (!(activity instanceof ChannelCallbacks)) {
            throw new RuntimeException("Activity must implement fragment callbacks.");
        }
        mCallbacks = (ChannelCallbacks)activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_designer_channel_detail, container, false);

        ListView list = (ListView)view.findViewById(R.id.fields);
        list.addHeaderView(inflater.inflate(R.layout.header_data_channel_detail, list, false));

        return view;
    };

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks.removeDataChannelDataChangeListener(mDataChangeListener);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        mDataChannelId = args.getLong(ARG_CHANNEL_ID);

        mDataChannel = mCallbacks.getDataChannel(mDataChannelId);
        mCallbacks.addDataChannelDataChangeListener(mDataChangeListener);

        mViews = new ViewHolder(view);
        mViews.setViewValues(getActivity(), mDataChannel);
        mViews.initViews();
    }

    public void setOnChannelDeletedListener(OnChannelDeletedListener listener) {
        mOnChannelDeletedListener = listener;
    }

    /**
     * Look through existing fields until we find an index that hasn't yet been
     * used.
     * 
     * @return New unused id for a field within this data channel.
     */
    protected int getNewParamId() {
        int i;
        for (i = 0; i < mDataChannel.fields.size(); i++) {
            boolean found = false;
            for (Field f : mDataChannel.fields) {
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

    public interface OnChannelDeletedListener {
        void onChannelDeleted();
    }

    private static class DataChannelAdapter extends BaseAdapter implements DragSortListener {
        private Context mContext;

        private DataChannel mDataChannel;

        private LayoutInflater mInflater;

        private ListView mList;

        /**
         * Hack around edit text losing focus inside a list view.
         */
        private TextFocusedListener mTextFocusedListener = new TextFocusedListener() {
            @Override
            public void onTextFocused(int position) {
                mList.setSelection(position);

                ViewGroup dragSortListItem = (ViewGroup)mList.getChildAt(position
                        - mList.getFirstVisiblePosition() + mList.getHeaderViewsCount());
                if (dragSortListItem == null) {
                    return;
                }

                View listItem = dragSortListItem.getChildAt(0);
                ListItemViewHolder viewHolder = (ListItemViewHolder)listItem.getTag();
                viewHolder.focusName();
            }
        };

        public DataChannelAdapter(Context context, DataChannel dataChannel, ListView list) {
            mContext = context;
            mDataChannel = dataChannel;
            mList = list;
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public void drag(int from, int to) {
        }

        @Override
        public void drop(int from, int to) {
            Field move = mDataChannel.fields.remove(from);
            mDataChannel.fields.add(to, move);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mDataChannel.fields.size();
        }

        @Override
        public Field getItem(int position) {
            return mDataChannel.fields.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ListItemViewHolder holder;

            // View reuse seems to play havoc with interaction between edit text
            // views and the IME. So for now a new view is created everytime.
            // if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_item_data_channel_field, parent, false);
            holder = new ListItemViewHolder(convertView);
            holder.initViews(new TypeAdapter(mContext), this, mTextFocusedListener);
            convertView.setTag(holder);
            // } else {
            // holder = (ListItemViewHolder)convertView.getTag();
            // }

            holder.setViewValues(getItem(position), position);

            return convertView;
        }

        @Override
        public void remove(int which) {
            mDataChannel.fields.remove(which);
            notifyDataSetChanged();
        }

        public void setDataChannel(DataChannel dataChannel) {
            mDataChannel = dataChannel;
        }
    }

    private static class TypeAdapter extends BaseAdapter implements ListAdapter, SpinnerAdapter {
        final static private int[] sTypes = new int[] {
                Type.TYPE_BOOLEAN, Type.TYPE_INTEGER, Type.TYPE_NUMBER, Type.TYPE_FLOAT,
                Type.TYPE_STRING
        };

        public static int positionOf(int type) {
            for (int i = 0; i < sTypes.length; i++) {
                if (sTypes[i] == type) {
                    return i;
                }
            }
            throw new RuntimeException("Unknown or unsupported field type " + type
                    + " for data channels.");
        }

        private Context mContext;

        private LayoutInflater mInflater;

        public TypeAdapter(Context context) {
            mContext = context;
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return sTypes.length;
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

            holder.textViews[0].setText(Type.getTypeString(mContext, sTypes[position]));

            return convertView;
        }

        @Override
        public Integer getItem(int position) {
            return sTypes[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
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

            holder.textViews[0].setText(Type.getTypeString(mContext, sTypes[position]));

            return convertView;
        }

    }

    private class ViewHolder {
        public View addField;

        public View delete;

        public ListView fields;

        public TextView name;

        private EditText description;

        public ViewHolder(View view) {
            name = (TextView)view.findViewById(R.id.name);
            description = (EditText)view.findViewById(R.id.description);
            fields = (ListView)view.findViewById(R.id.fields);
            addField = view.findViewById(R.id.add);
            delete = view.findViewById(R.id.button_delete);
        }

        public void initViews() {
            delete.setOnClickListener(mDeleteClickListener);
            addField.setOnClickListener(mAddClickListener);
            name.addTextChangedListener(mNameChangedListener);
            description.addTextChangedListener(mDescriptionChangedListener);
        }

        public void setViewValues(Context context, DataChannel dc) {
            name.setText(dc.name);
            description.setText(dc.description);
            mAdapter = new DataChannelAdapter(context, dc, mViews.fields);
            fields.setAdapter(mAdapter);
        }

        public void updateViewValues(DataChannel newDataChannel, DataChannel oldDataChannel) {
            if (!TextUtils.equals(newDataChannel.name, oldDataChannel.name)) {
                name.setText(newDataChannel.name);
            }
            if (!TextUtils.equals(newDataChannel.description, oldDataChannel.description)) {
                description.setText(newDataChannel.description);
            }
            mAdapter.setDataChannel(newDataChannel);
        }
    }

    protected static class ListItemViewHolder {
        protected static Handler mHandler;

        protected static EnsureFocus sEnsureFocus = new EnsureFocus();

        private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {
            @Override
            public void onFocusChange(final View v, boolean hasFocus) {
                // Only show the remove icon when the row is selected.
                if (hasFocus) {
                    // Use the ugly hack around edit text problems in list
                    // views.
                    if (mHandler == null) {
                        mHandler = new Handler();
                    }

                    mHandler.removeCallbacks(sEnsureFocus);
                    sEnsureFocus.setTextFocusedListener(mTextFocusedListener);
                    sEnsureFocus.setPosition(mPosition);
                    mHandler.postDelayed(sEnsureFocus, 200);

                    mRemove.setVisibility(View.VISIBLE);
                    mType.setEnabled(true);
                } else {
                    mRemove.setVisibility(View.INVISIBLE);
                    mType.setEnabled(false);
                }
            }
        };

        private OnItemSelectedListener mOnTypeSelectedListener = new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                mField.type = mTypeAdapter.getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        };

        private OnClickListener mRemoveClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition != ListView.INVALID_POSITION) {
                    mRemoveListener.remove(mPosition);
                }
            }
        };

        private TextFocusedListener mTextFocusedListener;

        private TextWatcher mTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                mField.name = s.toString();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        };

        private TypeAdapter mTypeAdapter;

        /**
         * Field referenced by this view holder.
         */
        protected Field mField;

        /**
         * Handle to drag-sort row by.
         */
        protected ImageView mHandle;

        /**
         * Edit text for the name of the field.
         */
        protected EditText mName;

        /**
         * Position of the item this view holder is responsible for.
         */
        protected int mPosition = ListView.INVALID_POSITION;

        /**
         * Button to remove the row.
         */
        protected ImageButton mRemove;

        /**
         * Listener for when this item is to be removed by events originating
         * from within the list item.
         */
        protected RemoveListener mRemoveListener;

        /**
         * Button to call a dialogue to set the type of subject detail this is.
         * Also holds the detail object this row is working on.
         */
        protected Spinner mType;

        public ListItemViewHolder(View view) {
            mHandle = (ImageView)view.findViewById(R.id.handle);
            mRemove = (ImageButton)view.findViewById(R.id.remove);
            mName = (EditText)view.findViewById(R.id.text);
            mType = (Spinner)view.findViewById(R.id.type);

            mName.addTextChangedListener(mTextWatcher);
            mName.setOnFocusChangeListener(mOnFocusChangeListener);
            mType.setOnItemSelectedListener(mOnTypeSelectedListener);
        }

        public void focusName() {
            mName.requestFocus();
        }

        public void initViews(TypeAdapter adapter, RemoveListener removeListener,
                TextFocusedListener textFocusedListener) {
            mTypeAdapter = adapter;
            mRemoveListener = removeListener;
            mTextFocusedListener = textFocusedListener;

            mType.setAdapter(adapter);
            mType.setEnabled(false);
            mRemove.setOnClickListener(mRemoveClickListener);
        }

        public void setViewValues(Field field, int position) {
            mField = field;
            mPosition = position;
            mName.setText(field.name);
            mType.setSelection(TypeAdapter.positionOf(field.type));
        }

        public interface TextFocusedListener {
            void onTextFocused(int position);
        }

        static class EnsureFocus implements Runnable {
            private int mPosition;

            private TextFocusedListener mTextFocusedListener;

            @Override
            public void run() {
                mTextFocusedListener.onTextFocused(mPosition);
            }

            public void setPosition(int position) {
                mPosition = position;
            }

            public void setTextFocusedListener(TextFocusedListener listener) {
                mTextFocusedListener = listener;
            }
        }
    }
}
