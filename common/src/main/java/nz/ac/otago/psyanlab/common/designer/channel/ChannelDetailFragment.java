package nz.ac.otago.psyanlab.common.designer.channel;

import com.mobeta.android.dslv.DragSortListView.DragSortListener;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.designer.ExperimentDesignerActivity
        .DataChannelDataChangeListener;
import nz.ac.otago.psyanlab.common.designer.util.DialogueRequestCodes;
import nz.ac.otago.psyanlab.common.designer.util.DialogueResultListenerRegistrar;
import nz.ac.otago.psyanlab.common.designer.util.EditDataColumnDialogueFragment;
import nz.ac.otago.psyanlab.common.model.DataChannel;
import nz.ac.otago.psyanlab.common.model.chansrc.Field;
import nz.ac.otago.psyanlab.common.model.util.Type;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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

public class ChannelDetailFragment extends Fragment {
    private static final String ARG_CHANNEL_ID = "arg_channel_id";

    private static final OnChannelDeletedListener sDummy = new OnChannelDeletedListener() {
        @Override
        public void onChannelDeleted() {
        }
    };

    private OnChannelDeletedListener mOnChannelDeletedListener = sDummy;

    public static ChannelDetailFragment newInstance(long id) {
        ChannelDetailFragment f = new ChannelDetailFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CHANNEL_ID, id);
        f.setArguments(args);
        return f;
    }

    private ViewHolder mViews;

    public OnItemClickListener mOnFieldClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Field field = (Field) parent.getItemAtPosition(position);
            showEditFieldDialogue(field, (int) parent.getItemIdAtPosition(position));
        }
    };

    protected DataChannelAdapter mAdapter;

    protected OnClickListener mAddClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            Field newField = new Field();
            newField.id = getNewParamId();
            newField.name = getString(R.string.default_name_data_channel_field, newField.id + 1);
            newField.type = Type.TYPE_STRING;
            showCreateFieldDialogue(newField);
        }
    };

    protected ChannelCallbacks mCallbacks;

    protected DataChannel mDataChannel;

    protected long mDataChannelId;

    private DialogueResultListenerRegistrar.DialogueResultListener mEditFieldResultListener = new
            DialogueResultListenerRegistrar.DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            Field field = data.getParcelable(EditDataColumnDialogueFragment.RESULT_FIELD);
            int position = data.getInt(EditDataColumnDialogueFragment.RESULT_POSITION);
            mDataChannel.fields.set(position, field);
            mAdapter.notifyDataSetChanged();
            mCallbacks.putDataChannel(mDataChannelId, mDataChannel);
        }

        @Override
        public void onResultDelete(Bundle data) {
            int position = data.getInt(EditDataColumnDialogueFragment.RESULT_POSITION);
            mDataChannel.fields.remove(position);
            mAdapter.notifyDataSetChanged();
            mCallbacks.putDataChannel(mDataChannelId, mDataChannel);
        }

        @Override
        public void onResultCancel() {
            // Nothing to do.
        }
    };

    private DialogueResultListenerRegistrar.DialogueResultListener mNewFieldResultListener = new
            DialogueResultListenerRegistrar.DialogueResultListener() {
        @Override
        public void onResult(Bundle data) {
            Field field = data.getParcelable(EditDataColumnDialogueFragment.RESULT_FIELD);
            mDataChannel.fields.add(field);
            mAdapter.notifyDataSetChanged();
            mCallbacks.putDataChannel(mDataChannelId, mDataChannel);
        }

        @Override
        public void onResultDelete(Bundle data) {
            // Nothing to do.
        }

        @Override
        public void onResultCancel() {
            // Nothing to do.
        }
    };

    private DataChannelDataChangeListener mDataChangeListener = new DataChannelDataChangeListener
            () {
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

    protected DataChannelAdapter.DataChannelChangedListener mDataChannelChangedListener = new
            DataChannelAdapter.DataChannelChangedListener() {
        @Override
        public void onDataChannelChange() {
            mCallbacks.putDataChannel(mDataChannelId, mDataChannel);
        }
    };

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
        mCallbacks = (ChannelCallbacks) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_designer_channel_detail, container, false);

        ListView list = (ListView) view.findViewById(R.id.fields);
        list.addHeaderView(inflater.inflate(R.layout.header_data_channel_detail, list, false));

        return view;
    }

    ;

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
        mCallbacks.registerDialogueResultListener(DialogueRequestCodes.CHANNEL_FIELD,
                mEditFieldResultListener);
        mCallbacks.registerDialogueResultListener(DialogueRequestCodes.CHANNEL_FIELD_NEW,
                mNewFieldResultListener);

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

    protected void showCreateFieldDialogue(Field field) {
        EditDataColumnDialogueFragment dialogue = EditDataColumnDialogueFragment.init(new
                EditDataColumnDialogueFragment(), DialogueRequestCodes.CHANNEL_FIELD_NEW, field);
        dialogue.show(getChildFragmentManager(), "dialogue_clear_operand");
    }

    protected void showEditFieldDialogue(Field field, int position) {
        EditDataColumnDialogueFragment dialogue = EditDataColumnDialogueFragment.init(new
                EditDataColumnDialogueFragment(), DialogueRequestCodes.CHANNEL_FIELD, field,
                position);
        dialogue.show(getChildFragmentManager(), "dialogue_clear_operand");
    }

    public interface OnChannelDeletedListener {
        void onChannelDeleted();
    }

    private class ViewHolder {
        private EditText description;

        public View addField;

        public View delete;

        public ListView fields;

        public TextView name;

        public ViewHolder(View view) {
            name = (TextView) view.findViewById(R.id.name);
            description = (EditText) view.findViewById(R.id.description);
            fields = (ListView) view.findViewById(R.id.fields);
            addField = view.findViewById(R.id.add);
            delete = view.findViewById(R.id.button_delete);
        }

        public void initViews() {
            delete.setOnClickListener(mDeleteClickListener);
            addField.setOnClickListener(mAddClickListener);
            name.addTextChangedListener(mNameChangedListener);
            description.addTextChangedListener(mDescriptionChangedListener);
            fields.setOnItemClickListener(mOnFieldClickListener);
        }

        public void setViewValues(Context context, DataChannel dc) {
            name.setText(dc.name);
            description.setText(dc.description);
            mAdapter = new DataChannelAdapter(context, dc, mDataChannelChangedListener);
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

    private static class DataChannelAdapter extends BaseAdapter implements DragSortListener {
        private Context mContext;

        private DataChannel mDataChannel;

        private DataChannelChangedListener mDataChannelChangedListener;

        private LayoutInflater mInflater;

        public DataChannelAdapter(Context context, DataChannel dataChannel,
                                  DataChannelChangedListener listener) {
            mContext = context;
            mDataChannel = dataChannel;
            mDataChannelChangedListener = listener;
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public void drag(int from, int to) {
        }

        @Override
        public void drop(int from, int to) {
            Field move = mDataChannel.fields.remove(from);
            mDataChannel.fields.add(to, move);
            notifyDataSetChanged();
            mDataChannelChangedListener.onDataChannelChange();
        }

        @Override
        public int getCount() {
            return mDataChannel.fields.size();
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
        public Field getItem(int position) {
            return mDataChannel.fields.get(position);
        }

        @Override
        public void remove(int which) {
            mDataChannel.fields.remove(which);
            notifyDataSetChanged();
            mDataChannelChangedListener.onDataChannelChange();
        }

        public void setDataChannel(DataChannel dataChannel) {
            mDataChannel = dataChannel;
        }

        public interface DataChannelChangedListener {
            void onDataChannelChange();
        }
    }

    protected static class ListItemViewHolder {
        /**
         * Handle to drag-sort row by.
         */
        protected ImageView mHandle;

        /**
         * Edit text for the name of the field.
         */
        protected TextView mName;

        /**
         * Button to call a dialogue to set the type of subject detail this is.
         * Also holds the detail object this row is working on.
         */
        protected TextView mType;

        public ListItemViewHolder(View view) {
            mHandle = (ImageView) view.findViewById(R.id.handle);
            mName = (TextView) view.findViewById(R.id.name);
            mType = (TextView) view.findViewById(R.id.type);
        }

        public void setViewValues(Context context, Field field) {
            mName.setText(field.name);
            mType.setText(Type.getTypeString(context, field.type));
        }
    }
}
