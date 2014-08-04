
package nz.ac.otago.psyanlab.common.designer.channel;

import nz.ac.otago.psyanlab.common.R;
import nz.ac.otago.psyanlab.common.model.DataChannel;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ChannelListFragment extends Fragment {
    private static final ShowChannelListener sDummy = new ShowChannelListener() {
        @Override
        public void showChannel(long id) {
        }
    };

    private ChannelCallbacks mCallbacks;

    private View mRootView;

    private ShowChannelListener mShowChannelListener = sDummy;

    private ViewHolder mViews;

    protected OnClickListener mAddChannelClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            DataChannel dataChannel = new DataChannel();
            final long id = mCallbacks.addDataChannel(dataChannel);
            dataChannel.name = getString(R.string.default_name_new_channel, id + 1);
            mCallbacks.putDataChannel(id, dataChannel);

            mShowChannelListener.showChannel(id);

            ListAdapter adapter = mViews.mList.getAdapter();
            for (int pos = 0; pos < adapter.getCount(); pos++) {
                if (id == adapter.getItemId(pos)) {
                    mViews.mList.setItemChecked(pos, true);
                    break;
                }
            }
        }
    };

    protected OnItemClickListener mOnItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            mShowChannelListener.showChannel(id);
            mRootView.setBackgroundResource(R.color.card_background);
        }
    };

    public void deselectItem() {
        mRootView.setBackgroundResource(R.drawable.background_designer_program_default);
    }

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
        return inflater.inflate(R.layout.fragment_designer_channel_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRootView = view;

        mViews = new ViewHolder(view);
        mViews.initViews();
        updateBackground();
    }

    public void setShowChannelListener(ShowChannelListener listener) {
        mShowChannelListener = listener;
    }

    private void updateBackground() {
        if (mViews.mList.getCheckedItemPosition() == ListView.INVALID_POSITION) {
            mRootView.setBackgroundResource(R.drawable.background_designer_program_default);
        } else {
            mRootView.setBackgroundResource(R.color.card_background);
        }
    }

    public interface ShowChannelListener {
        void showChannel(long id);
    }

    class ViewHolder {
        private View mAdd;

        private View mEmpty;

        private ListView mList;

        public ViewHolder(View view) {
            mList = (ListView)view.findViewById(R.id.list);
            mAdd = view.findViewById(R.id.button_add);
            mEmpty = view.findViewById(android.R.id.empty);
        }

        public void initViews() {
            mAdd.setOnClickListener(mAddChannelClickListener);
            mList.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);
            mList.setOnItemClickListener(mOnItemClickListener);
            mList.setAdapter(mCallbacks.getDataChannelsAdapter());
            mList.setDrawSelectorOnTop(false);
            mList.setEmptyView(mEmpty);
        }
    }
}
